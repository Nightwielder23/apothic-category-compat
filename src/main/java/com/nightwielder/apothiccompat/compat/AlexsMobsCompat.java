package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Alex's Mobs is mostly armor and vanilla class items that UniversalCompat handles. Only the custom
// weapons need an override.
public final class AlexsMobsCompat {
    private static final String NAMESPACE = "alexsmobs";

    private static final Set<String> BOW_PATHS = Set.of("blood_sprayer");

    private AlexsMobsCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, AlexsMobsCompat::categorize);
    }

    private static String categorize(String path) {
        if (BOW_PATHS.contains(path)) {
            return LootCategory.BOW.getName();
        }
        return null;
    }
}
