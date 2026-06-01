package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

public final class DreadSteelCompat {
    private static final String NAMESPACE = "dreadsteel";

    private static final Map<String, String> OVERRIDES = Map.of(
            "dreadsteel_scythe", LootCategory.HEAVY_WEAPON.getName(),
            "dreadsteel_shield", LootCategory.SHIELD.getName()
    );

    private DreadSteelCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
