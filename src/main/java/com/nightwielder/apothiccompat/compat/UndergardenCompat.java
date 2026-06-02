package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// Most Undergarden weapons extend vanilla classes UniversalCompat already handles, and melee routes by
// attack speed. Only the slingshot needs an override: it extends ProjectileWeaponItem instead of BowItem
// so the fallback skips it.
public final class UndergardenCompat {
    private static final String NAMESPACE = "undergarden";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("slingshot", LootCategory.BOW.getName());
    }

    private UndergardenCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
