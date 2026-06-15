package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Melee weapons extend SwordItem, so UniversalCompat handles them. The three Guns Without Roses compat guns
// extend GWR's own GunItem, not a vanilla ranged class, so override them to crossbow. They register only
// with Guns Without Roses installed, so without it the ids stay unbound and SILENT skips them.
public final class MeetYourFightCompat {
    private static final String NAMESPACE = "meetyourfight";

    private static final Map<String, String> OVERRIDES = Map.of(
            "cocktail_shotgun", LootCategory.CROSSBOW.getName(),
            "phantasmal_rifle", LootCategory.CROSSBOW.getName(),
            "dredged_cannonade", LootCategory.CROSSBOW.getName()
    );

    private MeetYourFightCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
