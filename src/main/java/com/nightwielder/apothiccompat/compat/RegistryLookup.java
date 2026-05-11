package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.ApothicCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class RegistryLookup {
    private RegistryLookup() {}

    // ForgeRegistries.ITEMS.getValue returns Items.AIR (the registry's default) for
    // unregistered keys, not null. Use containsKey to filter cleanly and log misses
    // at debug so version-skew entries don't dispatch overrides against minecraft:air.
    public static Item item(ResourceLocation id) {
        if (!ForgeRegistries.ITEMS.containsKey(id)) {
            ApothicCompat.LOGGER.debug("Skipping override for unknown item id {}", id);
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(id);
    }
}
