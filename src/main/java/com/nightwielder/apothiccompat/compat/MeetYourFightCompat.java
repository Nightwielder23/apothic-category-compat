package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Meet Your Fight boss drops. Pickaxe/axe/shovel/hoe variants are vanilla-class tools UniversalCompat
// handles so only the combat weapons need overrides. Suffix fallback covers weapon-shaped items added
// in later versions without an exact-name update.
public final class MeetYourFightCompat {
    private static final String NAMESPACE = "meetyourfight";

    private static final Set<String> SWORD_PATHS = Set.of(
            "mossy_sword", "shoulder_revolver", "dusk_blade");

    private static final Set<String> HEAVY_PATHS = Set.of("dusk_greatsword");

    private static final Set<String> CROSSBOW_PATHS = Set.of("bell_crossbow");

    private static final String[] HEAVY_SUFFIXES = {"_greatsword"};

    private static final String[] SWORD_SUFFIXES = {"_sword"};

    private static final String[] CROSSBOW_SUFFIXES = {"_crossbow"};

    private MeetYourFightCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, MeetYourFightCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        if (CROSSBOW_PATHS.contains(path)) return LootCategory.CROSSBOW.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        for (String s : CROSSBOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.CROSSBOW.getName();
        return null;
    }
}
