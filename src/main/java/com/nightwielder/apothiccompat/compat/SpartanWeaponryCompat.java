package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

// Suffix-matched rather than tag-matched because item tags aren't bound at InterModEnqueueEvent time so
// the tag-lookup path would send zero overrides. SW item ids follow a {material}_{weapontype}
// convention. Throwing weapons have no Apotheosis category and are skipped.
public final class SpartanWeaponryCompat {
    private static final String NAMESPACE = "spartanweaponry";

    private static final String[] SWORD_SUFFIXES = {
            "_parrying_dagger", "_dagger", "_longsword", "_katana", "_saber", "_rapier",
            "_spear", "_javelin"
    };

    private static final String[] HEAVY_SUFFIXES = {
            "_greatsword", "_battleaxe", "_battle_hammer", "_warhammer", "_flanged_mace",
            "_club", "_quarterstaff", "_glaive", "_halberd", "_lance", "_pike", "_scythe"
    };

    private static final String[] BOW_SUFFIXES = {
            "_longbow"
    };

    private static final String[] CROSSBOW_SUFFIXES = {
            "_heavy_crossbow"
    };

    private SpartanWeaponryCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, SpartanWeaponryCompat::categorize);
    }

    private static String categorize(String path) {
        if (path.equals("cestus") || path.endsWith("_cestus")) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        for (String s : BOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.BOW.getName();
        for (String s : CROSSBOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.CROSSBOW.getName();
        return null;
    }
}
