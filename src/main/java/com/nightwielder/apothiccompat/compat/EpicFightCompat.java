package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Epic Fight has material-suffixed weapon families (greatsword, longsword, dagger, spear, tachi) plus
// three bare uniques (bokken, uchigatana, glove). Greatswords are heavy, the rest are swords.
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
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
