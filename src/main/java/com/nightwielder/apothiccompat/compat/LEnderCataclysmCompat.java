package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LEnderCataclysmCompat {
    private static final String NAMESPACE = "cataclysm";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        put(LootCategory.SWORD,
                "ancient_spear", "athame", "black_steel_sword",
                "blazing_grips", "coral_spear",
                "khopesh", "lionfish", "soul_render", "sticky_gloves",
                "the_immolator", "tidal_claws", "void_forge", "zweiender");
        put(LootCategory.HEAVY_WEAPON,
                "black_steel_axe", "coral_bardiche", "emp", "final_fractal",
                "gauntlet_of_bulwark", "gauntlet_of_guard", "gauntlet_of_maelstrom",
                "meat_shredder", "the_annihilator", "the_incinerator");
        put(LootCategory.BOW, "cursed_bow", "wrath_of_the_desert");
        put(LootCategory.CROSSBOW,
                "void_assault_shoulder_weapon", "wither_assault_shoulder_weapon",
                "laser_gatling");
        put(LootCategory.SHIELD,
                "black_steel_targe", "bulwark_of_the_flame");
        put(LootCategory.HELMET,
                "bone_reptile_helmet", "cursium_helmet", "ignitium_helmet",
                "monstrous_helm");
        put(LootCategory.CHESTPLATE,
                "bone_reptile_chestplate", "cursium_chestplate",
                "ignitium_chestplate", "ignitium_elytra_chestplate",
                "bloom_stone_pauldrons");
        put(LootCategory.LEGGINGS, "cursium_leggings", "ignitium_leggings");
        put(LootCategory.BOOTS, "cursium_boots", "ignitium_boots");
        put(LootCategory.PICKAXE, "black_steel_pickaxe");
        put(LootCategory.SHOVEL, "black_steel_shovel");
    }

    private LEnderCataclysmCompat() {}

    private static void put(LootCategory cat, String... ids) {
        String name = cat.getName();
        for (String id : ids) OVERRIDES.put(id, name);
    }

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
