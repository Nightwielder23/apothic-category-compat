package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Placeholder — UniversalCompat handles vanilla-extending weapons by class.
 * Add explicit entries here for items the fallback can't derive (scythes,
 * staves, custom gauntlets, custom armor). Verify item IDs via JEI first.
 */
public final class ArmageddonCompat {
    private static final String NAMESPACE = "armageddon_mod";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.ofEntries(
            // e.g. Map.entry("zoranths_scythe", LootCategory.HEAVY_WEAPON)
    );

    private ArmageddonCompat() {}

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
