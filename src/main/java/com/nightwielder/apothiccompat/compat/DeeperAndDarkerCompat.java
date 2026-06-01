package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

// Deeper and Darker is armor-focused and its warden armor, soul elytra, and sculk blocks fall through to
// UniversalCompat. Only the bladed weapons need an override and their paths follow the _sword / _knife
// convention so suffix matching is enough.
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
