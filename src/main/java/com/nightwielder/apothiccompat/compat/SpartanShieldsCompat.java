package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Spartan Shields adds only shields; scan the namespace and accept anything that
 * extends {@link ShieldItem}. The instanceof check needs the item, so this keeps its
 * own scan rather than CompatScan.byPath. Non-shield items (if any are added in a
 * future update) fall through untouched.
 */
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
