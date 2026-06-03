package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Marium's melee weapons are mostly TieredItem/SwordItem with attack damage, so UniversalCompat splits
// them by speed. The ranged weapons are the gap: the bows extend ModdedBow (which extends a Ranged Weapon
// API CustomBow, not vanilla BowItem) and the crossbows extend ModdedCrossbow, so the BowItem/CrossbowItem
// class checks miss them. Galeforce and Kraken Slayer have one off names, the rest follow a
// {name}_{shape} convention so suffix matching covers them.
public final class MariumsSoulslikeCompat {
    private static final String NAMESPACE = "soulsweapons";

    private static final Set<String> BOW_PATHS = Set.of("galeforce", "kraken_slayer");

    private static final String[] BOW_SUFFIXES = {
            "_longbow", "_bowblade"
    };

    private static final String[] CROSSBOW_SUFFIXES = {
            "_crossbow"
    };

    private MariumsSoulslikeCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, MariumsSoulslikeCompat::categorize);
    }

    private static String categorize(String path) {
        for (String s : CROSSBOW_SUFFIXES) {
            if (path.endsWith(s)) {
                return LootCategory.CROSSBOW.getName();
            }
        }
        if (BOW_PATHS.contains(path)) {
            return LootCategory.BOW.getName();
        }
        for (String s : BOW_SUFFIXES) {
            if (path.endsWith(s)) {
                return LootCategory.BOW.getName();
            }
        }
        return null;
    }
}
