package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// Most Undergarden weapons extend vanilla classes UniversalCompat already handles. The battleaxes
// extend SwordItem so the fallback would call them swords, and the slingshot extends
// ProjectileWeaponItem instead of BowItem so the fallback skips it.
public final class UndergardenCompat {
    private static final String NAMESPACE = "undergarden";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("cloggrum_battleaxe", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("forgotten_battleaxe", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("froststeel_battleaxe", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("utherium_battleaxe", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("spear", LootCategory.SWORD.getName());
        OVERRIDES.put("slingshot", LootCategory.BOW.getName());
    }

    private UndergardenCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
