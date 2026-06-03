package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Meet Your Fight's melee weapons (cocktail_cutlass, depth_star, twilights_thorn) extend SwordItem, so
// UniversalCompat handles them by speed. The three Guns Without Roses compat guns extend GWR's own GunItem
// instead of a vanilla ranged class, so the fallback skips them and they need explicit crossbow overrides.
// All three register only when Guns Without Roses is installed, so on packs without it these ids stay
// unbound and SILENT skips them.
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
