package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Born in Chaos is MCreator generated. Its bladed weapons subclass SwordItem/AxeItem and carry attack
// damage, so UniversalCompat splits them by speed. Two items extend plain Item and slip past the class and
// speed checks: trident_hayfork carries no attack damage so it keeps a heavy_weapon override, and
// pumpkinhandgun (the Pumpkin Pistol) is a GeoItem gun that fires projectiles, so it maps to crossbow.
public final class BornInChaosCompat {
    private static final String NAMESPACE = "born_in_chaos_v1";

    private static final Map<String, String> OVERRIDES = Map.of(
            "trident_hayfork", LootCategory.HEAVY_WEAPON.getName(),
            "pumpkinhandgun", LootCategory.CROSSBOW.getName()
    );

    private BornInChaosCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
