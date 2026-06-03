package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Epic Fight carries each weapon's real combat power in its own attribute system (impact, armor negation,
// max strikes), loaded from a datapack capability, and leaves the vanilla ATTACK_DAMAGE/ATTACK_SPEED at the
// item's tier defaults. UniversalCompat reads those vanilla attributes, so it sees fast, low damage stats
// and Apotheosis falls back to its SWORD_DIG class default for every Epic Fight weapon. This module instead
// categorizes by Epic Fight's own weapon type, taken from data/epicfight/capabilities/weapons. Item ids
// follow {material}_{type}, so the type is matched on the id suffix; glove (fist) and uchigatana have no
// suffix, so they are matched exactly. Per mod modules run after the universal pass, so these win.
public final class EpicFightCompat {
    private static final String NAMESPACE = "epicfight";

    // Epic Fight swings two handers slowly, the rest are one handers and medium weapons.
    private static final Set<String> SWORD_PATHS = Set.of("glove", "uchigatana");

    private static final String[] HEAVY_SUFFIXES = {"_greatsword"};

    private static final String[] SWORD_SUFFIXES = {"_longsword", "_tachi", "_dagger", "_spear"};

    private EpicFightCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EpicFightCompat::categorize);
    }

    private static String categorize(String path) {
        if (SWORD_PATHS.contains(path)) {
            return LootCategory.SWORD.getName();
        }
        for (String suffix : HEAVY_SUFFIXES) {
            if (path.endsWith(suffix)) {
                return LootCategory.HEAVY_WEAPON.getName();
            }
        }
        for (String suffix : SWORD_SUFFIXES) {
            if (path.endsWith(suffix)) {
                return LootCategory.SWORD.getName();
            }
        }
        return null;
    }
}
