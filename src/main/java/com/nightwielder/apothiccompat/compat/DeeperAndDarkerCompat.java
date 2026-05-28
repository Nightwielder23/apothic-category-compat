package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

/**
 * Deeper and Darker is armor-focused. UniversalCompat handles the warden armor,
 * soul elytra, and sculk blocks via their vanilla-class fallbacks. Only a couple
 * of bladed weapons need an override, and suffix matching alone is enough since
 * their paths follow the _sword / _knife convention.
 */
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
