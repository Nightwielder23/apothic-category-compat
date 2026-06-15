package com.nightwielder.apothiccategorycompat.compat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.nightwielder.apothiccategorycompat.ApothicCategoryCompat;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Rebuilds Apotheosis's affix-by-type map without the blacklisted ids, mirroring AffixRegistry.onReload.
// Apoth rebuilds that map on every datapack reload, so this reruns each time (see ApothicCategoryCompat's hooks).
// The backing registry is left alone, so existing affixed items keep working.
public final class AffixBlacklist {
    private static final String BY_TYPE_FIELD = "byType";

    private static Set<ResourceLocation> blacklist = Set.of();

    private AffixBlacklist() {}

    public static void setBlacklist(Set<ResourceLocation> ids) {
        blacklist = (ids == null) ? Set.of() : Set.copyOf(ids);
    }

    // Returns the number of affixes actually disabled, those that matched a registered affix.
    public static int apply() {
        Set<ResourceLocation> bl = blacklist;
        if (bl.isEmpty()) {
            // Nothing to remove, so don't reflectively overwrite Apoth's pool with an identical copy.
            return 0;
        }

        Collection<Affix> all = AffixRegistry.INSTANCE.getValues();
        if (all.isEmpty()) {
            // Affixes haven't loaded yet, so rebuilding now would wipe the whole pool.
            ApothicCategoryCompat.LOGGER.debug("Affix registry empty; skipping blacklist apply.");
            return 0;
        }

        ImmutableMultimap.Builder<AffixType, DynamicHolder<Affix>> builder = ImmutableMultimap.builder();
        Set<ResourceLocation> matched = new HashSet<>();

        for (Affix affix : all) {
            // A broken third party affix can throw out of id() or definition(). Catch per affix so it drops
            // out of the pool instead of taking down the rebuild and the server start that triggers it.
            try {
                ResourceLocation id = affix.id();
                if (bl.contains(id)) {
                    matched.add(id);
                    continue;
                }
                builder.put(affix.definition().type(), AffixRegistry.INSTANCE.holder(affix));
            } catch (Throwable t) {
                ApothicCategoryCompat.LOGGER.warn("Skipping an affix while rebuilding the pool: {}", t.toString());
            }
        }

        // Blacklist ids that matched no affix, either misspelled or from a mod that isn't installed.
        List<ResourceLocation> unknown = new ArrayList<>();
        for (ResourceLocation id : bl) {
            if (!matched.contains(id)) {
                unknown.add(id);
            }
        }
        if (!unknown.isEmpty()) {
            ApothicCategoryCompat.LOGGER.warn("Affix blacklist: {} unknown affix id(s) skipped: {}", unknown.size(), unknown);
        }

        Multimap<AffixType, DynamicHolder<Affix>> rebuilt = builder.build();
        try {
            Field field = AffixRegistry.class.getDeclaredField(BY_TYPE_FIELD);
            field.setAccessible(true);
            field.set(AffixRegistry.INSTANCE, rebuilt);
        } catch (ReflectiveOperationException | RuntimeException e) {
            ApothicCategoryCompat.LOGGER.warn("Could not rewrite AffixRegistry.{}; affix blacklist not applied: {}", BY_TYPE_FIELD, e.toString());
            return 0;
        }

        if (matched.isEmpty()) {
            ApothicCategoryCompat.LOGGER.debug("Affix blacklist applied: nothing disabled.");
        } else {
            ApothicCategoryCompat.LOGGER.info("Affix blacklist applied: {} affix(es) disabled.", matched.size());
        }
        return matched.size();
    }
}
