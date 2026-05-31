package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Two unrelated mods both go by Knight Quest. The GPL one (knightquest) names items {shape}_{suffix},
// and Count Grimhart's (knight_quest) uses kq_{kind}_{name}; armor and mining axes fall through to
// UniversalCompat in both.
public final class KnightQuestCompat {
    private static final String NAMESPACE_GPL = "knightquest";
    private static final String NAMESPACE_GRIMHART = "knight_quest";

    private static final Set<String> GPL_SWORD_PATHS = Set.of(
            "cleaver", "uchigatana_katana", "nail_glaive", "kukri_dagger");

    private static final Set<String> GPL_HEAVY_PATHS = Set.of("paladin_sword");

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
