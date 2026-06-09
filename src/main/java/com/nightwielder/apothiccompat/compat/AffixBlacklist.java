package com.nightwielder.apothiccompat.compat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.nightwielder.apothiccompat.ApothicCompat;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.AffixType;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Rebuilds Apoth's affix-by-type map without the blacklisted ids. The backing AffixManager is left alone
// so existing affixed items keep working, and the map is rebuilt on every datapack reload, so this reruns.
public final class AffixBlacklist {
    private static final String BY_TYPE_FIELD = "byType";

    private static Set<ResourceLocation> blacklist = Set.of();

    private AffixBlacklist() {}

    public static void setBlacklist(Set<ResourceLocation> ids) {
        blacklist = (ids == null) ? Set.of() : Set.copyOf(ids);
    }

    // Returns the number of affixes actually disabled, those that matched a registered affix.
    public static int apply() {
        Collection<Affix> all = AffixManager.INSTANCE.getValues();
        if (all.isEmpty()) {
            // Affixes haven't loaded yet so rebuilding now would wipe the whole pool
            ApothicCompat.LOGGER.debug("Affix registry empty; skipping blacklist apply.");
            return 0;
        }

        Set<ResourceLocation> bl = blacklist;
        ImmutableMultimap.Builder<AffixType, Affix> builder = ImmutableMultimap.builder();
        Set<ResourceLocation> matched = new HashSet<>();
        Map<AffixType, Integer> originalByType = new EnumMap<>(AffixType.class);
        Map<AffixType, Integer> keptByType = new EnumMap<>(AffixType.class);

        for (Affix affix : all) {
            // A broken third-party affix can throw out of getType/getId. Catch per affix so it drops out of
            // the pool instead of crashing the rebuild (and the server start that triggers it).
            try {
                AffixType type = affix.getType();
                originalByType.merge(type, 1, Integer::sum);
                if (bl.contains(affix.getId())) {
                    matched.add(affix.getId());
                    continue;
                }
                builder.put(type, affix);
                keptByType.merge(type, 1, Integer::sum);
            } catch (Throwable t) {
                ResourceLocation badId = null;
                try {
                    badId = affix.getId();
                } catch (Throwable ignored) {
                }
                ApothicCompat.LOGGER.warn("Skipping affix {} while rebuilding the pool: {}", badId, t.toString());
            }
        }

        // Blacklist ids with no matching affix, either misspelled or from a mod that isn't installed.
        List<ResourceLocation> unknown = new ArrayList<>();
        for (ResourceLocation id : bl) {
            if (!matched.contains(id)) {
                unknown.add(id);
            }
        }
        if (!unknown.isEmpty()) {
            ApothicCompat.LOGGER.warn("Affix blacklist: {} unknown affix id(s) skipped: {}", unknown.size(), unknown);
        }

        // Types the blacklist emptied out completely. Apoth rolls fewer affixes when a type has no
        // candidates so it's allowed, but flag it so the operator knows that category can't appear anymore.
        List<AffixType> emptied = new ArrayList<>();
        for (Map.Entry<AffixType, Integer> e : originalByType.entrySet()) {
            if (keptByType.getOrDefault(e.getKey(), 0) == 0) {
                emptied.add(e.getKey());
            }
        }
        if (!emptied.isEmpty()) {
            ApothicCompat.LOGGER.warn("Affix blacklist emptied these affix types completely: {}", emptied);
        }

        // AffixManager has getTypeMap() for reads but no setter, so the rebuilt map gets written straight
        // to the private byType field.
        Multimap<AffixType, Affix> rebuilt = builder.build();
        try {
            Field field = AffixManager.class.getDeclaredField(BY_TYPE_FIELD);
            field.setAccessible(true);
            field.set(AffixManager.INSTANCE, rebuilt);
        } catch (ReflectiveOperationException | RuntimeException e) {
            ApothicCompat.LOGGER.warn("Could not rewrite AffixManager.{}; affix blacklist not applied: {}", BY_TYPE_FIELD, e.toString());
            return 0;
        }

        if (matched.isEmpty()) {
            ApothicCompat.LOGGER.debug("Affix blacklist applied: nothing disabled.");
        } else {
            ApothicCompat.LOGGER.info("Affix blacklist applied: {} affix(es) disabled.", matched.size());
        }
        return matched.size();
    }
}
