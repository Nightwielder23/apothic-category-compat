package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LEnderCataclysmCompat {
    private static final String NAMESPACE = "cataclysm";
    private static final String IMC_METHOD = "loot_category_override";
    private static final Map<String, LootCategory> OVERRIDES = new LinkedHashMap<>();

    static {
        put(LootCategory.SWORD,
                "ancient_spear", "athame", "black_steel_sword", "khopesh",
                "soul_render", "the_immolator", "astrape", "ceraunus",
                "lionfish", "void_forge", "blazing_grips", "sticky_gloves",
                "chitin_claw", "tidal_claws", "wrath_of_the_desert",
                "astrape_throwing", "ceraunus_throwing",
                "coral_spear", "coral_spear_throwing");
        put(LootCategory.HEAVY_WEAPON,
                "black_steel_axe", "coral_bardiche",
                "gauntlet_of_bulwark", "gauntlet_of_guard", "gauntlet_of_maelstrom",
                "meat_shredder", "the_annihilator", "the_incinerator", "zweiender",
                "coral_bardiche_throwing",
                "void_assault_shoulder_weapon", "wither_assault_shoulder_weapon",
                "laser_gatling", "emp", "final_fractal");
        put(LootCategory.BOW, "cursed_bow");
        put(LootCategory.SHIELD,
                "azure_sea_shield", "black_steel_targe", "bulwark_of_the_flame");
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
        for (String id : ids) OVERRIDES.put(id, cat);
    }

    public static void send() {
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            ResourceLocation id = new ResourceLocation(NAMESPACE, e.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
