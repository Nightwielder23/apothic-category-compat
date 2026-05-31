package com.nightwielder.apothiccompat.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;
import java.util.function.Function;

// Registry scan helpers for modules that categorize by item id instead of an explicit override map.
// the categorizer returns an Apotheosis category name or null to skip.
public final class CompatScan {
    private CompatScan() {}

    public static void byPath(String namespace, Function<String, String> categorizer) {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!namespace.equals(id.getNamespace())) continue;
            String categoryName = categorizer.apply(id.getPath());
            if (categoryName == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            CompatImc.send(item, categoryName);
        }
    }

    public static void scanAll(BiFunction<String, String, String> categorizer) {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            String categoryName = categorizer.apply(id.getNamespace(), id.getPath());
            if (categoryName == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            CompatImc.send(item, categoryName);
        }
    }
}
