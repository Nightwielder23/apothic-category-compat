package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

// Simply Swords has no per-weapon-type item tags, only material-based tags plus a catch-all swords tag.
// Item ids follow a {material}_{weapontype} convention so matching is by suffix. Uniques that break the
// convention fall through to UniversalCompat.
public final class SimplySwordsCompat {
    private static final String NAMESPACE = "simplyswords";

    private static final String[] SWORD_SUFFIXES = {
            "_longsword", "_claymore", "_rapier", "_katana", "_chakram",
            "_cutlass", "_twinblade", "_scythe", "_sai", "_warglaive", "_spear"
    };

    private static final String[] HEAVY_SUFFIXES = {
            "_greathammer", "_greataxe", "_glaive", "_halberd"
    };

    private SimplySwordsCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, SimplySwordsCompat::categorize);
    }

    private static String categorize(String path) {
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        return null;
    }
}
