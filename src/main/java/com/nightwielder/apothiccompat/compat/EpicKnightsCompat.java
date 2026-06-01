package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

// UniversalCompat handles Epic Knights' vanilla-class weapons and armor. Shields are miscategorized by
// default and polearms/mauls extend SwordItem but should roll heavy-weapon affixes, so both are matched
// on registry path because the tag tree isn't publicly documented.
public final class EpicKnightsCompat {
    private static final String NAMESPACE = "magistuarmory";

    private static final String[] HEAVY_TOKENS = {
            "claymore", "glaive", "halberd", "hammer", "lance",
            "mace", "maul", "pike", "warhammer", "zweihander"
    };

    private EpicKnightsCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EpicKnightsCompat::categorize);
    }

    // substring matcher for magistuarmory weapon ids
    private static String categorize(String path) {
        if (path.contains("shield")) return LootCategory.SHIELD.getName();
        for (String token : HEAVY_TOKENS) {
            if (path.contains(token)) return LootCategory.HEAVY_WEAPON.getName();
        }
        return null;
    }
}
