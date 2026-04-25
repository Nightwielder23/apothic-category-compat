package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Alex's Caves weapons don't extend vanilla weapon classes, so they go uncategorized
 * by default. The set is small and the IDs follow no shared suffix convention worth
 * pattern-matching, so each weapon is listed explicitly.
 */
public final class AlexsCavesCompat {
    private static final String NAMESPACE = "alexscaves";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.ofEntries(
            Map.entry("desolate_dagger", LootCategory.SWORD),
            Map.entry("limestone_spear", LootCategory.SWORD),
            Map.entry("extinction_spear", LootCategory.SWORD),
            Map.entry("frostmint_spear", LootCategory.SWORD),
            Map.entry("sea_staff", LootCategory.SWORD),
            Map.entry("sugar_staff", LootCategory.SWORD),
            Map.entry("ortholance", LootCategory.SWORD),
            Map.entry("galena_gauntlet", LootCategory.SWORD),
            Map.entry("primitive_club", LootCategory.HEAVY_WEAPON),
            Map.entry("dreadbow", LootCategory.BOW),
            Map.entry("raygun", LootCategory.BOW)
    );

    private AlexsCavesCompat() {}

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
