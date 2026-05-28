package com.nightwielder.apothiccompat.compat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.nightwielder.apothiccompat.ApothicCompat;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stops blacklisted affixes from rolling on new gear by rebuilding Apotheosis's
 * cached affix-by-type map without them. Loot, reforging, trades, and gems all draw
 * candidates from that map via LootController.getAvailableAffixes, so removing an
 * affix here removes it from every roll path at once. The backing AffixRegistry is
 * never touched, so existing affixed items keep working and clearing an entry
 * restores it on the next apply. AffixRegistry rebuilds the map from scratch on each
 * datapack reload, so this has to run again after every reload.
 */
public final class AffixBlacklist {
    private static final String BY_TYPE_FIELD = "byType";

    private static Set<ResourceLocation> blacklist = Set.of();

    private AffixBlacklist() {}

    public static void setBlacklist(Set<ResourceLocation> ids) {
        blacklist = (ids == null) ? Set.of() : Set.copyOf(ids);
    }

    public static void apply() {
        Collection<Affix> all = AffixRegistry.INSTANCE.getValues();
        if (all.isEmpty()) {
            // Affixes have not loaded yet; rebuilding now would wipe the whole pool.
            ApothicCompat.LOGGER.debug("Affix registry empty; skipping blacklist apply.");
            return;
        }

        Set<ResourceLocation> bl = blacklist;
        ImmutableMultimap.Builder<AffixType, DynamicHolder<Affix>> builder = ImmutableMultimap.builder();
        Set<ResourceLocation> matched = new HashSet<>();
        Map<AffixType, Integer> originalByType = new EnumMap<>(AffixType.class);
        Map<AffixType, Integer> keptByType = new EnumMap<>(AffixType.class);

        for (Affix affix : all) {
            AffixType type = affix.getType();
            originalByType.merge(type, 1, Integer::sum);
            if (bl.contains(affix.getId())) {
                matched.add(affix.getId());
                continue;
            }
            builder.put(type, AffixRegistry.INSTANCE.holder(affix));
            keptByType.merge(type, 1, Integer::sum);
        }

        // Blacklist ids with no matching affix: misspelled, or from a mod that is not
        // installed. Warn so the operator can correct the toml.
        List<ResourceLocation> unknown = new ArrayList<>();
        for (ResourceLocation id : bl) {
            if (!matched.contains(id)) unknown.add(id);
        }
        if (!unknown.isEmpty()) {
            ApothicCompat.LOGGER.warn("Affix blacklist: {} unknown affix id(s) skipped: {}", unknown.size(), unknown);
        }

        // Types the blacklist emptied completely. Apotheosis just rolls fewer affixes
        // when a type has no candidates, so this is allowed, but flag it so the
        // operator knows that whole category can no longer appear.
        List<AffixType> emptied = new ArrayList<>();
        for (Map.Entry<AffixType, Integer> e : originalByType.entrySet()) {
            if (keptByType.getOrDefault(e.getKey(), 0) == 0) emptied.add(e.getKey());
        }
        if (!emptied.isEmpty()) {
            ApothicCompat.LOGGER.warn("Affix blacklist emptied these affix types completely: {}", emptied);
        }

        // AffixRegistry exposes getTypeMap() for reads but no setter, so the rebuilt
        // map has to be written straight to the private byType field.
        Multimap<AffixType, DynamicHolder<Affix>> rebuilt = builder.build();
        try {
            Field field = AffixRegistry.class.getDeclaredField(BY_TYPE_FIELD);
            field.setAccessible(true);
            field.set(AffixRegistry.INSTANCE, rebuilt);
        } catch (ReflectiveOperationException | RuntimeException e) {
            ApothicCompat.LOGGER.warn("Could not rewrite AffixRegistry.{}; affix blacklist not applied: {}", BY_TYPE_FIELD, e.toString());
            return;
        }

        if (matched.isEmpty()) {
            ApothicCompat.LOGGER.debug("Affix blacklist applied: nothing disabled.");
        } else {
            ApothicCompat.LOGGER.info("Affix blacklist applied: {} affix(es) disabled.", matched.size());
        }
    }
}
