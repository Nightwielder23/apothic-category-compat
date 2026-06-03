package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LEnderCataclysmCompat {
    private static final String NAMESPACE = "cataclysm";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        // cursed_bow extends ProjectileWeaponItem and wrath_of_the_desert extends plain Item, so neither
        // is caught by the BowItem class check. The shoulder weapons and laser_gatling extend plain Item.
        put(LootCategory.BOW, "cursed_bow", "wrath_of_the_desert");
        put(LootCategory.CROSSBOW,
                "void_assault_shoulder_weapon", "wither_assault_shoulder_weapon",
                "laser_gatling");
        // void_forge and infernal_forge are the only Cataclysm items that extend PickaxeItem, so
        // UniversalCompat would file them under pickaxe, both are wielded as heavy weapons, so override them
        // explicitly (per mod modules run after the universal pass and win). Every melee Cataclysm_Weapon_Item
        // carries attack damage and routes through the speed split, and the shields all extend ShieldItem, so
        // none of those need an entry here.
        put(LootCategory.HEAVY_WEAPON, "void_forge", "infernal_forge");
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
