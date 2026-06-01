package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.ForgeRegistries;

public final class TetraCompat {
    private static final String NAMESPACE = "tetra";

    private TetraCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            LootCategory cat = categorize(item);
            if (cat == null) continue;
            CompatImc.send(item, cat.getName());
        }
    }

    private static LootCategory categorize(Item item) {
        if (item instanceof BowItem) return LootCategory.BOW;
        if (item instanceof CrossbowItem) return LootCategory.CROSSBOW;
        if (item instanceof SwordItem) {
            return CompatImc.getAttackDamage(item) > CompatImc.HEAVY_WEAPON_THRESHOLD ? LootCategory.HEAVY_WEAPON : LootCategory.SWORD;
        }
        return null;
    }
}
