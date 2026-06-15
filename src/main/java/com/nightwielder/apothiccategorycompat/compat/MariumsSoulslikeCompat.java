package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Melee weapons are TieredItem/SwordItem, so UniversalCompat splits them by speed. The ranged weapons are
// the gap: bows extend ModdedBow (a Ranged Weapon API CustomBow, not vanilla BowItem) and crossbows extend
// ModdedCrossbow, so the class checks miss them. Suffix matching covers them, plus the one off names.
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
