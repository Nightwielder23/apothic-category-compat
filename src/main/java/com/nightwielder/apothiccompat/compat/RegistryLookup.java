package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.ApothicCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class RegistryLookup {
    private RegistryLookup() {}

    // ForgeRegistries.ITEMS.getValue returns Items.AIR (the registry's default) for
    // unregistered keys, not null. Use containsKey to filter cleanly and log misses
    // at warn so version-skew entries surface in user logs instead of silently
    // dispatching overrides against minecraft:air.
    public static Item item(ResourceLocation id) {
        if (!ForgeRegistries.ITEMS.containsKey(id)) {
            ApothicCompat.LOGGER.warn("Skipping override for unknown item id {}", id);
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(id);
    }
}
