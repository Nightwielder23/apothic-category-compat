package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.registries.ForgeRegistries;

// Spartan Shields adds only shields. Scan the namespace and accept anything extending ShieldItem.
public final class SpartanShieldsCompat {
    private static final String NAMESPACE = "spartanshields";

    private SpartanShieldsCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (!(item instanceof ShieldItem)) continue;
            CompatImc.send(item, LootCategory.SHIELD.getName());
        }
    }
}
