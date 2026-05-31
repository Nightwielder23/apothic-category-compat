package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Epic Fight Resurrection (cdmoveset) ids don't always put an underscore before the weapon-type token
// (e.g. s_irongreatsword vs s_iron_greatsword), so suffixes are matched without a leading underscore.
// Anything that matches no suffix falls through to UniversalCompat.
public final class EpicFightResurrectionCompat {
    private static final String NAMESPACE = "cdmoveset";

    private static final Set<String> HEAVY_PATHS = Set.of("great_tachi");

    private static final String[] HEAVY_SUFFIXES = {
            "greatsword"
    };

    private static final String[] SWORD_SUFFIXES = {
            "longsword"
    };

    private EpicFightResurrectionCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EpicFightResurrectionCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
