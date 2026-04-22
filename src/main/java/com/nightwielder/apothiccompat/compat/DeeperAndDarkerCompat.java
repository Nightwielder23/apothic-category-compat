package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Deeper and Darker is armor-focused — UniversalCompat handles the warden armor,
 * soul elytra, and sculk blocks via their vanilla-class fallbacks. Only a couple
 * of bladed weapons need an override, and suffix matching alone is enough since
 * their paths follow the _sword / _knife convention.
 */
public final class DeeperAndDarkerCompat {
    private static final String NAMESPACE = "deeperdarker";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String[] SWORD_SUFFIXES = {"_sword", "_knife"};

    private DeeperAndDarkerCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            LootCategory cat = categorize(id.getPath());
            if (cat == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = cat.getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }

    private static LootCategory categorize(String path) {
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        return null;
    }
}
