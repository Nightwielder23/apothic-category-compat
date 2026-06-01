package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Marium's Soulslike Weaponry registers most weapons as TieredItem so neither Apotheosis nor
// UniversalCompat picks them up. Suffix matching handles the {material}_{shape} names and the rest are
// one-off legendaries listed exactly. Exact paths are checked first so master_sword (heavy by design)
// beats the generic _sword suffix.
public final class MariumsSoulslikeCompat {
    private static final String NAMESPACE = "soulsweapons";

    private static final Set<String> HEAVY_PATHS = Set.of(
            "darkin_blade", "darkin_scythe_pre", "glaive_of_hodir", "kirkhammer",
            "leviathan_axe", "master_sword", "mjolnir", "soul_reaper",
            "whirligig_sawblade");

    private static final Set<String> SWORD_PATHS = Set.of(
            "bloodthirster", "dawnbreaker", "dragonbane", "draugr",
            "empowered_dawnbreaker", "excalibur", "featherlight", "frostmourne",
            "lich_bane", "mehrunes_razor", "moonveil", "nightfall",
            "nights_edge_item", "rageblade", "simons_blade", "skofnung",
            "sting", "tonitrus");

    private static final Set<String> BOW_PATHS = Set.of("galeforce", "kraken_slayer");

    private static final String[] HEAVY_SUFFIXES = {
            "_greatsword", "_scythe", "_glaive"
    };

    private static final String[] SWORD_SUFFIXES = {
            "_shortsword", "_swordspear", "_spear", "_sword"
    };

    private static final String[] BOW_SUFFIXES = {
            "_longbow", "_bowblade"
    };

    private static final String[] CROSSBOW_SUFFIXES = {
            "_crossbow"
    };

    private MariumsSoulslikeCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, MariumsSoulslikeCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        if (BOW_PATHS.contains(path)) return LootCategory.BOW.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        for (String s : BOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.BOW.getName();
        for (String s : CROSSBOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.CROSSBOW.getName();
        return null;
    }
}
