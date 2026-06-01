package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Two unrelated mods ship under the Knight Quest name. Namespace knightquest is the GPL mod whose items
// follow {shape}_{suffix} (paladin_sword, uchigatana_katana). Namespace knight_quest is Count Grimhart's
// 1.19.2 mod whose items follow kq_{kind}_{name} (kq_sword_paladin). Armor and mining axes fall through
// to UniversalCompat in both.
public final class KnightQuestCompat {
    private static final String NAMESPACE_GPL = "knightquest";
    private static final String NAMESPACE_GRIMHART = "knight_quest";

    private static final Set<String> GPL_SWORD_PATHS = Set.of(
            "cleaver", "uchigatana_katana", "nail_glaive", "kukri_dagger");

    private static final Set<String> GPL_HEAVY_PATHS = Set.of("paladin_sword", "khopesh_claymore");

    private static final String[] GPL_HEAVY_SUFFIXES = {};

    private static final String[] GPL_SWORD_SUFFIXES = {"_sword", "_spear"};

    private static final Set<String> GRIMHART_SWORD_PATHS = Set.of(
            "kq_sword_cleaver", "kq_sword_crimson", "kq_sword_hollow",
            "kq_sword_khopesh", "kq_sword_kukri", "kq_sword_steel",
            "kq_sword_uchigatana", "kq_sword_water");

    private static final Set<String> GRIMHART_HEAVY_PATHS = Set.of("kq_sword_paladin");

    private KnightQuestCompat() {}

    public static void send() {
        CompatScan.scanAll(KnightQuestCompat::categorize);
    }

    private static String categorize(String namespace, String path) {
        if (NAMESPACE_GPL.equals(namespace)) {
            if (GPL_HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
            if (GPL_SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
            for (String s : GPL_HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
            for (String s : GPL_SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
            return null;
        }
        if (NAMESPACE_GRIMHART.equals(namespace)) {
            if (GRIMHART_HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
            if (GRIMHART_SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
            return null;
        }
        return null;
    }
}
