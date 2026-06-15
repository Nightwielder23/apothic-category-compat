package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Most Alex's Mobs items are armor or vanilla class weapons UniversalCompat already handles. Only the
// custom weapons need an explicit override.
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
