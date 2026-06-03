package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
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

// Default pass run before the per mod modules across every namespace. Vanilla non melee classes decide by
// hierarchy, anything that deals melee damage is split into sword or heavy_weapon by attack speed (with a
// high damage cutoff), read live from the stack so combat mods like Epic Fight are reflected. With FG&A
// loaded, an item whose id names a staff, scepter, or wand routes to the staffs category before the speed
// split.
// Per mod modules run after this and overwrite where their explicit decision disagrees (Apoth keeps
// overrides last wins).
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
            String category = categorize(stack, id);
            if (category == null) {
                continue;
            }
            CompatImc.send(item, category);
        }
    }

    private static String categorize(ItemStack stack, ResourceLocation id) {
        Item item = stack.getItem();
        // Vanilla non melee classes carry their category in the class hierarchy, so they decide first.
        if (item instanceof BowItem) {
            return LootCategory.BOW.getName();
        }
        if (item instanceof CrossbowItem) {
            return LootCategory.CROSSBOW.getName();
        }
        if (item instanceof TridentItem) {
            return LootCategory.HEAVY_WEAPON.getName();
        }
        if (item instanceof PickaxeItem) {
            return LootCategory.PICKAXE.getName();
        }
        if (item instanceof ShovelItem) {
            return LootCategory.SHOVEL.getName();
        }
        if (item instanceof HoeItem) {
            return null;
        }
        if (item instanceof ShieldItem) {
            return LootCategory.SHIELD.getName();
        }
        if (item instanceof ArmorItem armor) {
            return switch (armor.getEquipmentSlot()) {
                case HEAD -> LootCategory.HELMET.getName();
                case CHEST -> LootCategory.CHESTPLATE.getName();
                case LEGS -> LootCategory.LEGGINGS.getName();
                case FEET -> LootCategory.BOOTS.getName();
                default -> null;
            };
        }
        // Name based staff fallback so modded casters don't each need a per mod override. Runs after the
        // class branches so a bow or shield still wins, and before the speed rule so a slow scepter doesn't
        // read as heavy. Only fires with FG&A loaded, since the staffs category exists only then.
        if (FallenGemsCompat.hasStaffsCategory() && isStaffByName(id)) {
            return FallenGemsCompat.STAFFS_CATEGORY;
        }
        // The static damage check confirms a melee weapon before the live reads, which fire
        // ItemAttributeModifierEvent and so reflect combat mod stats. Slow weapons read heavy, so do
        // medium or faster weapons at or above the heavy damage cutoff.
        if (CompatImc.getAttackDamageGeneric(item) > 0) {
            if (CompatImc.getAttackSpeed(stack) <= CompatImc.SLOW_SPEED_MAX) {
                return LootCategory.HEAVY_WEAPON.getName();
            }
            if (CompatImc.getAttackDamage(stack) >= CompatImc.HEAVY_DAMAGE_THRESHOLD) {
                return LootCategory.HEAVY_WEAPON.getName();
            }
            return LootCategory.SWORD.getName();
        }
        return null;
    }

    // Matches a scepter, staff, or wand by registry id. Battle and war staves are melee polearms, and the
    // spartanweaponry quarterstaves and wom staves swing as weapons, so those are left to the speed rule.
    private static boolean isStaffByName(ResourceLocation id) {
        String namespace = id.getNamespace();
        if (namespace.equals("spartanweaponry") || namespace.equals("wom")) {
            return false;
        }
        String path = id.getPath();
        // The dungeons_and_combat namespace registers two ids for the same Wooden Battle Staff. The
        // wooden_battle_staff id is already caught by the battle_staff exclusion below, but wooden_staff
        // isn't and points at the same melee weapon, so exclude it explicitly.
        if (namespace.equals("dungeons_and_combat") && path.equals("wooden_staff")) {
            return false;
        }
        if (path.contains("battle_staff") || path.contains("battlestaff")
                || path.contains("war_staff") || path.contains("warstaff")) {
            return false;
        }
        // The "wandering" armor and "wanderer" items carry the "wand" substring without being wands, so drop
        // them before the wand match below.
        if (path.contains("wandering") || path.contains("wanderer")) {
            return false;
        }
        return path.contains("staff") || path.contains("scepter") || path.contains("wand");
    }
}
