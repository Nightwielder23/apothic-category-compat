package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Bladed weapons subclass SwordItem/AxeItem with attack damage, so UniversalCompat splits them by speed.
// The only gap is pumpkinhandgun (the Pumpkin Pistol), a GeoItem gun with no attack damage, so crossbow.
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
