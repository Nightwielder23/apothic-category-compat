package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public final class MowziesMobsCompat {
    private static final String NAMESPACE = "mowziesmobs";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.ofEntries(
            Map.entry("spear", LootCategory.SWORD),
            Map.entry("wrought_axe", LootCategory.HEAVY_WEAPON),
            Map.entry("naga_fang_dagger", LootCategory.SWORD),
            Map.entry("earthrend_gauntlet", LootCategory.SWORD),
            Map.entry("blowgun", LootCategory.BOW),
            Map.entry("sol_visage", LootCategory.HELMET),
            Map.entry("umvuthana_mask", LootCategory.HELMET),
            Map.entry("umvuthana_mask_bliss", LootCategory.HELMET),
            Map.entry("umvuthana_mask_faith", LootCategory.HELMET),
            Map.entry("umvuthana_mask_fear", LootCategory.HELMET),
            Map.entry("umvuthana_mask_fury", LootCategory.HELMET),
            Map.entry("umvuthana_mask_misery", LootCategory.HELMET),
            Map.entry("umvuthana_mask_rage", LootCategory.HELMET),
            Map.entry("wrought_helmet", LootCategory.HELMET),
            Map.entry("geomancer_robe", LootCategory.CHESTPLATE),
            Map.entry("geomancer_sandals", LootCategory.BOOTS)
    );

    private MowziesMobsCompat() {}

    public static void send() {
        for (var e : OVERRIDES.entrySet()) {
            ResourceLocation id = new ResourceLocation(NAMESPACE, e.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
