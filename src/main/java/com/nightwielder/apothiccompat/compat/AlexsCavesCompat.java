package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

/**
 * Alex's Caves weapons don't extend vanilla weapon classes, so they go uncategorized
 * by default. The set is small and the IDs follow no shared suffix convention worth
 * pattern-matching, so each weapon is listed explicitly.
 */
public final class AlexsCavesCompat {
    private static final String NAMESPACE = "alexscaves";

    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("desolate_dagger", LootCategory.SWORD.getName()),
            Map.entry("limestone_spear", LootCategory.SWORD.getName()),
            Map.entry("extinction_spear", LootCategory.SWORD.getName()),
            Map.entry("frostmint_spear", LootCategory.SWORD.getName()),
            Map.entry("sea_staff", LootCategory.SWORD.getName()),
            Map.entry("sugar_staff", LootCategory.SWORD.getName()),
            Map.entry("ortholance", LootCategory.SWORD.getName()),
            Map.entry("galena_gauntlet", LootCategory.SWORD.getName()),
            Map.entry("primitive_club", LootCategory.HEAVY_WEAPON.getName()),
            Map.entry("dreadbow", LootCategory.BOW.getName()),
            Map.entry("raygun", LootCategory.BOW.getName())
    );

    private AlexsCavesCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.WARN);
    }
}
