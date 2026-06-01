package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Bosses of Mass Destruction has a couple of custom-class weapons that Apotheosis and UniversalCompat
// miss on their own.
public final class BossesOfMassDestructionCompat {
    private static final String NAMESPACE = "bosses_of_mass_destruction";

    private static final Set<String> HEAVY_PATHS = Set.of();

    private static final Set<String> SWORD_PATHS = Set.of("nether_staff", "obsidian_spear");

    private BossesOfMassDestructionCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, BossesOfMassDestructionCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        return null;
    }
}
