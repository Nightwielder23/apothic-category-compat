package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Undergarden weapons mostly extend vanilla classes that UniversalCompat already
 * categorizes. Two cases need correcting: the battleaxes extend SwordItem, so the
 * fallback would file them as swords instead of heavy weapons, and the slingshot
 * extends ProjectileWeaponItem directly rather than BowItem, so the fallback skips
 * it. spear and utherium_battleaxe are absent from UG 0.8.14 but may return in a
 * later version, so they stay listed and skip silently when missing.
 */
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
