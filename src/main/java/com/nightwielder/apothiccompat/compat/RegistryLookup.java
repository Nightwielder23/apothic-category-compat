package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.ApothicCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class RegistryLookup {
    private RegistryLookup() {}

    // ForgeRegistries.ITEMS.getValue hands back Items.AIR (the registry default) for unregistered keys,
    // not null. containsKey filters those out, and warns on a miss so version-skew ids show up in the log
    // instead of dispatching overrides onto minecraft:air.
    public static Item item(ResourceLocation id) {
        if (!ForgeRegistries.ITEMS.containsKey(id)) {
            ApothicCompat.LOGGER.warn("Skipping override for unknown item id {}", id);
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(id);
    }
}
