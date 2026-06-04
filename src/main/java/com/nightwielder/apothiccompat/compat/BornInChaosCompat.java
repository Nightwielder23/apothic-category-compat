package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Born in Chaos is MCreator generated. Its bladed weapons subclass SwordItem/AxeItem and carry attack
// damage (trident_hayfork is a SwordItem in this version), so UniversalCompat splits them by speed. The
// only gap is pumpkinhandgun (the Pumpkin Pistol), a GeoItem gun that fires projectiles with no attack
// damage attribute, so it maps to crossbow.
public final class BornInChaosCompat {
    private static final String NAMESPACE = "born_in_chaos_v1";

    private static final Map<String, String> OVERRIDES = Map.of(
            "pumpkinhandgun", LootCategory.CROSSBOW.getName()
    );

    private BornInChaosCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
