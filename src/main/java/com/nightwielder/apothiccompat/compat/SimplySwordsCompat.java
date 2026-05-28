package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

/**
 * Simply Swords has no per-weapon-type item tags in 1.20.1, only material-based
 * tags (iron_gear, gold_gear, etc.) plus a catch-all "swords" tag. The mod's item IDs
 * follow a consistent {material}_{weapontype} convention, so we match on suffix.
 * Uniques that don't follow the convention fall through to UniversalCompat.
 */
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
        // _warglaive ends with _glaive; keep this short-circuit so future HEAVY
        // suffix additions can't accidentally steal it.
        if (path.endsWith("_warglaive")) return LootCategory.SWORD.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON.getName();
        return null;
    }
}
