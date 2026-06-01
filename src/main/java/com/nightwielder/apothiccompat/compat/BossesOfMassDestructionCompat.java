package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Bosses of Mass Destruction has one custom-class weapon that Apotheosis and UniversalCompat miss on
// their own. The Earthdive Spear is a thrusting melee weapon so it maps to sword.
public final class BossesOfMassDestructionCompat {
    private static final String NAMESPACE = "bosses_of_mass_destruction";

    private static final Set<String> SWORD_PATHS = Set.of("earthdive_spear");

    private BossesOfMassDestructionCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, BossesOfMassDestructionCompat::categorize);
    }

    private static String categorize(String path) {
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        return null;
    }
}
