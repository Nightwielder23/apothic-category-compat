package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.registries.ForgeRegistries;

// Fallback that runs last across every namespace. Skips items Apotheosis already categorized and
// derives the rest by vanilla item class.
public final class UniversalCompat {
    private UniversalCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) continue;
            if (!LootCategory.forItem(stack).isNone()) continue;
            LootCategory cat = categorize(item);
            if (cat == null) continue;
            CompatImc.send(item, cat.getName());
        }
    }

    private static LootCategory categorize(Item item) {
        if (item instanceof SwordItem) return LootCategory.SWORD;
        if (item instanceof AxeItem) {
            return CompatImc.getAttackDamage(item) > CompatImc.HEAVY_WEAPON_THRESHOLD ? LootCategory.HEAVY_WEAPON : LootCategory.SWORD;
        }
        if (item instanceof BowItem) return LootCategory.BOW;
        if (item instanceof CrossbowItem) return LootCategory.CROSSBOW;
        if (item instanceof TridentItem) return LootCategory.HEAVY_WEAPON;
        if (item instanceof PickaxeItem) return LootCategory.PICKAXE;
        if (item instanceof ShovelItem) return LootCategory.SHOVEL;
        if (item instanceof HoeItem) return null;
        if (item instanceof ArmorItem armor) {
            return switch (armor.getSlot()) {
                case HEAD -> LootCategory.HELMET;
                case CHEST -> LootCategory.CHESTPLATE;
                case LEGS -> LootCategory.LEGGINGS;
                case FEET -> LootCategory.BOOTS;
                default -> null;
            };
        }
        return null;
    }
}
