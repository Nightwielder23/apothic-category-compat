package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Born in Chaos is MCreator generated. Its bladed weapons subclass SwordItem/AxeItem and carry attack
// damage, so UniversalCompat splits them by speed. The gaps extend plain Item: trident_hayfork carries no
// attack damage so it keeps a heavy_weapon override, and the two pumpkin pistols are GeoItem guns that fire
// projectiles, so they map to crossbow.
public final class BornInChaosCompat {
    private static final String NAMESPACE = "born_in_chaos_v1";

    private static final Map<String, String> OVERRIDES = Map.of(
            "trident_hayfork", LootCategory.HEAVY_WEAPON.getName(),
            "pumpkin_pistol", LootCategory.CROSSBOW.getName(),
            "pumpkin_pistol_2", LootCategory.CROSSBOW.getName()
    );

    private BornInChaosCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
