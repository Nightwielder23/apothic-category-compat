package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

// Simply Swords has no per-weapon-type tags, just material tags plus a catch-all swords tag, so matching
// goes by id suffix on the {material}_{weapontype} names. Uniques that break the convention fall through
// to UniversalCompat.
public final class SimplySwordsCompat {
    private static final String NAMESPACE = "simplyswords";

    private static final String[] SWORD_SUFFIXES = {
            "_chakram", "_claymore", "_cutlass", "_katana", "_longsword", "_rapier",
            "_sai", "_scythe", "_spear", "_twinblade", "_warglaive"
    };

    private static final String[] HEAVY_SUFFIXES = {
            "_glaive", "_greataxe", "_greathammer", "_halberd"
    };

    private SimplySwordsCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, SimplySwordsCompat::categorize);
    }

    private static String categorize(String path) {
        // _warglaive also ends with _glaive, so keep this check first or the HEAVY _glaive suffix grabs it
        if (path.endsWith("_warglaive")) return LootCategory.SWORD.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        return null;
    }
}
