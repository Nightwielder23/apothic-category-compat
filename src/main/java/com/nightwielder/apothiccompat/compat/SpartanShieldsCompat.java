package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.registries.ForgeRegistries;

// Spartan Shields only adds shields, so scan the namespace and take anything extending ShieldItem. The
// instanceof needs the item itself so it keeps its own scan instead of CompatScan.byPath.
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
