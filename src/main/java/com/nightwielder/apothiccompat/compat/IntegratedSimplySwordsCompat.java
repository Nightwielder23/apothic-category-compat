package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Integrated Simply Swords adds Simply Swords-style weapons under cross-mod material variants. Paths
// look like {sourcemod}/{material}/{weapontype} so suffixes match on /{type}, same mapping as
// SimplySwordsCompat.
public final class IntegratedSimplySwordsCompat {
    private static final String NAMESPACE = "integrated_simply_swords";

    // Galenic Polarizer is a SwordItem subclass but acts like a heavy ranged launcher. TravelopticsCompat
    // already maps galenic_polarizer to HEAVY so the integrated copy matches.
    private static final Set<String> HEAVY_PATHS = Set.of(
            "alexscaves/polarizer");

    private static final String[] HEAVY_SUFFIXES = {
            "/glaive", "/greataxe", "/greathammer", "/halberd"
    };

    private static final String[] SWORD_SUFFIXES = {
            "/chakram", "/claymore", "/cutlass", "/katana", "/longsword", "/rapier",
            "/sai", "/scythe", "/spear", "/twinblade", "/warglaive"
    };

    private IntegratedSimplySwordsCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, IntegratedSimplySwordsCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
