package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

// RPG Style More Weapons R adds a few weapons plus armor that UniversalCompat already handles. Only the
// three weapon families need overrides.
public final class RpgStyleMoreWeaponsCompat {
    private static final String NAMESPACE = "rpg_style_more_weapons_r";

    private static final String[] HEAVY_SUFFIXES = {
            "_battle_axe", "_greatsword"
    };

    private static final String[] SWORD_SUFFIXES = {
            "_knife"
    };

    private RpgStyleMoreWeaponsCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, RpgStyleMoreWeaponsCompat::categorize);
    }

    private static String categorize(String path) {
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
