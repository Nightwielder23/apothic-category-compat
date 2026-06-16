package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.ApothicCategoryCompat;
import com.nightwielder.apothiccategorycompat.config.ApothicCategoryCompatConfig;
import com.nightwielder.apothiccategorycompat.util.CompatImc;
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

// Default pass before the per mod modules, so their explicit decisions override it (Apoth keeps overrides
// last wins). Vanilla non melee classes decide by class; melee splits into sword or heavy_weapon by attack
// speed (with a high damage cutoff), read live so combat mods like Epic Fight are reflected.
public final class UniversalCompat {
    // Block breakers that are also swung as weapons, not mined with. weapon_pickaxes_as_heavy routes these
    // to heavy_weapon. Full registry ids so the match never catches an unrelated mod's same-named item.
    private static final Set<String> DUAL_PURPOSE_PICKAXES = Set.of(
            "cataclysm:void_forge",
            "cataclysm:infernal_forge",
            "twilightforest:cube_of_annihilation",
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
            // A broken attribute resolver in another mod can throw out of getAttributeModifiers (e.g.
            // Enigmatic Addons hitting the server at IMC time), so catch per item to keep the dispatch alive.
            try {
                LootCategory cat = categorize(stack, id);
                if (cat != null) {
                    CompatImc.send(item, cat.getName());
                }
            } catch (Throwable t) {
                ApothicCategoryCompat.LOGGER.warn("Skipping {} during categorization: {}", id, t.toString());
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
        // Dual-purpose block breakers route to heavy when the toggle is on, pickaxe when off. Decided by id
        // here, not the PickaxeItem branch below, since cube_of_annihilation is a plain Item and would
        // otherwise miss the pickaxe case and drop through to the speed rule.
        if (DUAL_PURPOSE_PICKAXES.contains(id.toString())) {
            return ApothicCategoryCompatConfig.weaponPickaxesAsHeavy()
                    ? LootCategory.HEAVY_WEAPON
                    : LootCategory.PICKAXE;
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
        // Static damage precheck confirms a melee weapon before the live reads, which fire
        // ItemAttributeModifierEvent and reflect combat mod stats. Slow weapons read heavy, as do hard hitters.
        if (CompatImc.getAttackDamageGeneric(item) > 0) {
            LootCategory category;
            if (CompatImc.getAttackSpeed(stack) <= CompatImc.SLOW_SPEED_MAX) {
                category = LootCategory.HEAVY_WEAPON;
            } else if (CompatImc.getAttackDamage(stack) >= CompatImc.HEAVY_DAMAGE_THRESHOLD) {
                category = LootCategory.HEAVY_WEAPON;
            } else {
                category = LootCategory.SWORD;
            }
            // Name override (on by default): a heavy weapon name forces heavy, and a name ending in "sword"
            // forces sword, so the stat read can't misfile a fast greatsword as sword or a high-damage sword as
            // heavy. Heavy is checked first, so a greatsword (which also ends in "sword") stays heavy.
            if (ApothicCategoryCompatConfig.nameBasedHeavyOverride()) {
                if (isHeavyByName(id)) {
                    return LootCategory.HEAVY_WEAPON;
                }
                if (isSwordByName(id)) {
                    return LootCategory.SWORD;
                }
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

    private static boolean isSwordByName(ResourceLocation id) {
        return id.getPath().toLowerCase(Locale.ROOT).endsWith("sword");
    }
}
