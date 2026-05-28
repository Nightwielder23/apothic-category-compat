package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

/**
 * Born in Chaos is MCreator-generated so weapons don't extend vanilla weapon
 * classes and would otherwise go uncategorized. Most follow a consistent
 * {name}_{shape} convention (_sword, _axe, _hammer, _scythe, _dagger, _mace,
 * _cutlass, _blade) so suffix matching covers them. The handful that don't
 * (darkwarblade, trident_hayfork, soulbane) are listed exactly.
 */
public final class BornInChaosCompat {
    private static final String NAMESPACE = "born_in_chaos_v1";

    private static final Set<String> HEAVY_PATHS = Set.of(
            "darkwarblade", "trident_hayfork");

    private static final Set<String> SWORD_PATHS = Set.of(
            "soulbane");

    private static final String[] HEAVY_SUFFIXES = {
            "_scythe", "_axe", "_hammer", "_mace"
    };

    private static final String[] SWORD_SUFFIXES = {
            "_sword", "_dagger", "_cutlass", "_blade"
    };

    private BornInChaosCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, BornInChaosCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
