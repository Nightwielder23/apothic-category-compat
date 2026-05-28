package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

/**
 * Integrated Simply Swords adds Simply Swords-style weapons under cross-mod
 * material variants. Item paths use slash separators in the form
 * {sourcemod}/{material}/{weapontype}, so suffixes match "/{type}". Same
 * weapon-to-category mapping as SimplySwordsCompat.
 */
public final class IntegratedSimplySwordsCompat {
    private static final String NAMESPACE = "integrated_simply_swords";

    // Galenic Polarizer extends a SwordItem subclass but plays as a heavy
    // ranged launcher; TravelopticsCompat treats galenic_polarizer as HEAVY,
    // so the cross-integrated copy follows suit.
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
