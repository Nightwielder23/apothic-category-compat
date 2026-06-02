package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.registries.ForgeRegistries;

// Fallback run after the per-mod modules across every namespace. Vanilla non-melee classes decide by
// hierarchy; anything that deals melee damage is split into sword or heavy_weapon by attack speed (with a
// high-damage cutoff), read live from the stack so combat mods like Epic Fight are reflected.
public final class UniversalCompat {
    private UniversalCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) {
                continue;
            }
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) {
                continue;
            }
            LootCategory cat = categorize(stack);
            if (cat == null) {
                continue;
            }
            CompatImc.send(item, cat.getName());
        }
    }

    private static LootCategory categorize(ItemStack stack) {
        Item item = stack.getItem();
        // Vanilla non-melee classes carry their category in the class hierarchy, so they decide first.
        if (item instanceof BowItem) {
            return LootCategory.BOW;
        }
        if (item instanceof CrossbowItem) {
            return LootCategory.CROSSBOW;
        }
        if (item instanceof TridentItem) {
            return LootCategory.HEAVY_WEAPON;
        }
        if (item instanceof PickaxeItem) {
            return LootCategory.PICKAXE;
        }
        if (item instanceof ShovelItem) {
            return LootCategory.SHOVEL;
        }
        if (item instanceof HoeItem) {
            return null;
        }
        if (item instanceof ShieldItem) {
            return LootCategory.SHIELD;
        }
        if (item instanceof ArmorItem armor) {
            return switch (armor.getSlot()) {
                case HEAD -> LootCategory.HELMET;
                case CHEST -> LootCategory.CHESTPLATE;
                case LEGS -> LootCategory.LEGGINGS;
                case FEET -> LootCategory.BOOTS;
                default -> null;
            };
        }
        // The static damage check confirms a melee weapon before the live reads, which fire
        // ItemAttributeModifierEvent and so reflect combat-mod stats. Slow weapons read heavy; so do
        // medium-or-faster weapons at or above the heavy-damage cutoff.
        if (CompatImc.getAttackDamageGeneric(item) > 0) {
            if (CompatImc.getAttackSpeed(stack) <= CompatImc.SLOW_SPEED_MAX) {
                return LootCategory.HEAVY_WEAPON;
            }
            if (CompatImc.getAttackDamage(stack) >= CompatImc.HEAVY_DAMAGE_THRESHOLD) {
                return LootCategory.HEAVY_WEAPON;
            }
            return LootCategory.SWORD;
        }
        return null;
    }
}
