package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.ApothicCompat;
import com.nightwielder.apothiccompat.config.ApothicCompatConfig;
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

import java.util.Locale;
import java.util.Set;

// Default pass run before the per mod modules across every namespace, so their explicit decisions override
// it (Apoth keeps overrides last wins). Vanilla non melee classes decide by hierarchy, anything that deals
// melee damage is split into sword or heavy_weapon by attack speed (with a high damage cutoff), read live
// from the stack so combat mods like Epic Fight are reflected.
public final class UniversalCompat {
    // PickaxeItem-class items that are wielded as weapons, not mining tools. weapon_pickaxes_as_heavy routes
    // these to heavy_weapon instead of pickaxe. Full registry ids so the match is exact and never catches an
    // unrelated mod's same-named item.
    private static final Set<String> DUAL_PURPOSE_PICKAXES = Set.of(
            "cataclysm:void_forge",
            "cataclysm:infernal_forge",
            "forbidden_arcanus:wooden_blacksmith_gavel",
            "forbidden_arcanus:stone_blacksmith_gavel",
            "forbidden_arcanus:golden_blacksmith_gavel",
            "forbidden_arcanus:iron_blacksmith_gavel",
            "forbidden_arcanus:diamond_blacksmith_gavel",
            "forbidden_arcanus:netherite_blacksmith_gavel",
            "forbidden_arcanus:deorum_blacksmith_gavel",
            "forbidden_arcanus:reinforced_deorum_blacksmith_gavel"
    );

    // Heavy weapon name fragments matched against a lowercase registry id path. name_based_heavy_override
    // uses these to force heavy_weapon regardless of the stat read.
    private static final String[] HEAVY_NAMES = {
            "greatsword", "claymore", "zweihander", "warhammer", "halberd", "bardiche", "glaive",
            "warglaive", "battleaxe", "battle_axe", "greataxe", "great_axe", "lance", "pike", "maul",
            "naginata", "odachi", "flamberge", "scythe"
    };

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
            // A broken attribute resolver in another mod can throw out of stack.getAttributeModifiers (e.g.
            // Enigmatic Addons reaching for the server at IMC time). Catch per item so one bad item can't
            // sink the whole dispatch, and the server boot with it.
            try {
                LootCategory cat = categorize(stack, id);
                if (cat != null) {
                    CompatImc.send(item, cat.getName());
                }
            } catch (Throwable t) {
                ApothicCompat.LOGGER.warn("Skipping {} during categorization: {}", id, t.toString());
            }
        }
    }

    private static LootCategory categorize(ItemStack stack, ResourceLocation id) {
        Item item = stack.getItem();
        // Vanilla non melee classes carry their category in the class hierarchy, so they decide first.
        if (item instanceof BowItem) {
            return LootCategory.BOW;
        }
        if (item instanceof CrossbowItem) {
            return LootCategory.CROSSBOW;
        }
        if (item instanceof TridentItem) {
            return LootCategory.TRIDENT;
        }
        // Combat tools that subclass PickaxeItem (the forges and gavels) read as weapons, not mining gear, so
        // route them to heavy when the toggle is on. With it off they fall through to the pickaxe branch below.
        if (ApothicCompatConfig.weaponPickaxesAsHeavy() && DUAL_PURPOSE_PICKAXES.contains(id.toString())) {
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
        // ItemAttributeModifierEvent and so reflect combat mod stats. Slow weapons read heavy, so do
        // medium or faster weapons at or above the heavy damage cutoff.
        if (CompatImc.getAttackDamageGeneric(item) > 0) {
            LootCategory category;
            if (CompatImc.getAttackSpeed(stack) <= CompatImc.SLOW_SPEED_MAX) {
                category = LootCategory.HEAVY_WEAPON;
            } else if (CompatImc.getAttackDamage(stack) >= CompatImc.HEAVY_DAMAGE_THRESHOLD) {
                category = LootCategory.HEAVY_WEAPON;
            } else {
                category = LootCategory.SWORD;
            }
            // Opt-in name override: a heavy weapon name forces heavy even when the speed read landed on sword,
            // so a fast greatsword still classes as heavy. Off by default, so the stat read stands.
            if (ApothicCompatConfig.nameBasedHeavyOverride() && isHeavyByName(id)) {
                return LootCategory.HEAVY_WEAPON;
            }
            return category;
        }
        return null;
    }

    private static boolean isHeavyByName(ResourceLocation id) {
        String path = id.getPath().toLowerCase(Locale.ROOT);
        for (String name : HEAVY_NAMES) {
            if (path.contains(name)) {
                return true;
            }
        }
        return false;
    }
}
