package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Meet Your Fight's melee weapons extend SwordItem so UniversalCompat handles them. The two Guns Without
// Roses compat guns extend GWR's own ShotgunItem/GunItem instead of a vanilla ranged class, so the
// fallback skips them and they need explicit crossbow overrides.
public final class MeetYourFightCompat {
    private static final String NAMESPACE = "meetyourfight";

    private static final Map<String, String> OVERRIDES = Map.of(
            "cocktail_shotgun", LootCategory.CROSSBOW.getName(),
            "phantasmal_rifle", LootCategory.CROSSBOW.getName()
    );

    private MeetYourFightCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
