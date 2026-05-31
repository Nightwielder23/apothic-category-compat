package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

// Deeper and Darker is mostly armor, which UniversalCompat already handles through vanilla-class
// fallbacks. Only the bladed weapons need an override, and their _sword/_knife paths make suffix
// matching enough.
public final class DeeperAndDarkerCompat {
    private static final String NAMESPACE = "deeperdarker";

    private static final String[] SWORD_SUFFIXES = {"_sword", "_knife"};

    private DeeperAndDarkerCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, DeeperAndDarkerCompat::categorize);
    }

    private static String categorize(String path) {
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
