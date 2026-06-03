package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Most Undergarden weapons extend vanilla classes UniversalCompat handles, and the battleaxes extend
// SwordItem so the speed split reads them (slow, so heavy). Only the slingshot needs an override: it
// extends ProjectileWeaponItem instead of BowItem, so the class branch skips it.
public final class UndergardenCompat {
    private static final String NAMESPACE = "undergarden";

    private static final Map<String, String> OVERRIDES = Map.of(
            "slingshot", LootCategory.BOW.getName()
    );

    private UndergardenCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
