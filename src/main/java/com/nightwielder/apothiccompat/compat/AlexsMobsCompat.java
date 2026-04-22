package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Alex's Mobs ships mostly armor and vanilla-class items that UniversalCompat
 * already handles; only the custom weapons need an explicit override.
 */
public final class AlexsMobsCompat {
    private static final String NAMESPACE = "alexsmobs";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> SWORD_PATHS = Set.of("blood_sprayer");

    private AlexsMobsCompat() {}

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
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD;
        return null;
    }
}
