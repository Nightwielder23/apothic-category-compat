package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Base Epic Fight has five material suffixed weapon families (greatsword, longsword, dagger, spear,
// tachi) plus three uniques (bokken, uchigatana, glove). Greatswords are HEAVY, and everything else is
// SWORD since spears, tachis, and gloves all count as light melee.
public final class EpicFightCompat {
    private static final String NAMESPACE = "epicfight";

    private static final Set<String> SWORD_PATHS = Set.of(
            "bokken", "glove", "uchigatana");

    private static final String[] HEAVY_SUFFIXES = {"_greatsword"};

    private static final String[] SWORD_SUFFIXES = {
            "_dagger", "_longsword", "_spear", "_tachi"
    };

    private EpicFightCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EpicFightCompat::categorize);
    }

    private static String categorize(String path) {
        if (SWORD_PATHS.contains(path)) {
            return LootCategory.SWORD.getName();
        }
        for (String s : HEAVY_SUFFIXES) {
            if (path.endsWith(s)) {
                return LootCategory.HEAVY_WEAPON.getName();
            }
        }
        for (String s : SWORD_SUFFIXES) {
            if (path.endsWith(s)) {
                return LootCategory.SWORD.getName();
            }
        }
        return null;
    }
}
