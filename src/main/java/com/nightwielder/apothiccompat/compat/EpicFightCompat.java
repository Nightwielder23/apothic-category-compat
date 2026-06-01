package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

// Epic Fight weapon families are material-suffixed. Greatswords are heavy and the rest of the melee
// families are swords. Anything that matches no suffix falls through to UniversalCompat.
public final class EpicFightCompat {
    private static final String NAMESPACE = "epicfight";

    private static final String[] HEAVY_SUFFIXES = {"_greatsword"};

    private static final String[] SWORD_SUFFIXES = {
            "_longsword", "_dagger", "_spear", "_tachi", "_bokken", "_glove", "_uchigatana"
    };

    private EpicFightCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EpicFightCompat::categorize);
    }

    private static String categorize(String path) {
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
