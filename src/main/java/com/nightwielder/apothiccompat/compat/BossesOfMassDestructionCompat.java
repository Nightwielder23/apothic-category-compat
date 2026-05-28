package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

/**
 * Bosses of Mass Destruction only has a couple of custom-class weapons that
 * Apotheosis and UniversalCompat don't categorize on their own.
 */
public final class BossesOfMassDestructionCompat {
    private static final String NAMESPACE = "bosses_of_mass_destruction";

    // nether_staff and obsidian_spear were renamed/removed in BoMD 1.1.x but
    // are kept here as no-ops for users still on older releases.
    private static final Set<String> SWORD_PATHS = Set.of(
            "earthdive_spear", "nether_staff", "obsidian_spear");

    private BossesOfMassDestructionCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, BossesOfMassDestructionCompat::categorize);
    }

    private static String categorize(String path) {
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        return null;
    }
}
