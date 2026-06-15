package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LEnderCataclysmCompat {
    private static final String NAMESPACE = "cataclysm";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        // The cursed_bow extends ProjectileWeaponItem and wrath_of_the_desert extends plain Item, so neither
        // is caught by the BowItem class check. The shoulder weapons and laser_gatling extend plain Item.
        put(LootCategory.BOW, "cursed_bow", "wrath_of_the_desert");
        put(LootCategory.CROSSBOW,
                "void_assault_shoulder_weapon", "wither_assault_shoulder_weapon",
                "laser_gatling");
        // The bulwark_of_the_flame extends plain Item, so the ShieldItem class check misses it. The
        // black_steel_targe extends ShieldItem, so UniversalCompat already maps it and no entry is needed here.
        put(LootCategory.SHIELD, "bulwark_of_the_flame");
        // void_forge and infernal_forge are PickaxeItem combat tools handled by the weapon_pickaxes_as_heavy
        // config in UniversalCompat, so they no longer need an explicit override here.
    }

    private LEnderCataclysmCompat() {}

    private static void put(LootCategory cat, String... ids) {
        String name = cat.getName();
        for (String id : ids) {
            OVERRIDES.put(id, name);
        }
    }

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
