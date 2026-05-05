package com.nightwielder.apothiccompat.compat;

import com.google.common.collect.Multimap;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Dungeons and Combat adds a large, growing set of Dungeons-inspired weapons and
 * armor. Rather than hardcoding hundreds of IDs, categorize by item class plus a
 * name-based override for weapons whose class-based inference is wrong
 * (e.g. SwordItem-subclass polearms that should roll heavy-weapon affixes).
 */
public final class DungeonsAndCombatCompat {
    private static final String NAMESPACE = "dungeons_and_combat";
    private static final String IMC_METHOD = "loot_category_override";
    private static final double HEAVY_WEAPON_THRESHOLD = 8.0;

    private static final String[] HEAVY_SUFFIXES = {
            "_halberd", "_glaive", "_whirlwind", "_hammer", "_maul",
            "_greataxe", "_greathammer"
    };

    // Magic-caster scepters. Class-based inference routes these wrong:
    // PyromancerScepterItem extends SwordItem (would land in SWORD), and the
    // other three extend plain Item (would land nowhere). Route to FG&A's
    // runtime "staffs" category when present, otherwise SWORD. FG&A's own
    // predicate matches only Iron's StaffItem, so the IMC has to be explicit
    // here, unlike IronsSpellbooksCompat and TravelopticsCompat which can
    // defer to FG&A's auto-detection by skipping their own overrides.
    private static final Set<String> SCEPTERS = Set.of(
            "pyromancer_scepter",
            "sanguine_scepter",
            "fairy_scepter",
            "scepter_of_compensation"
    );
    private static final String STAFFS_CATEGORY = "staffs";

    private DungeonsAndCombatCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = explicitOverride(id.getPath());
            if (name == null) {
                LootCategory cat = categorize(id.getPath(), item);
                if (cat == null) continue;
                name = cat.getName();
            }
            sendOverride(item, name);
        }
    }

    private static void sendOverride(Item item, String categoryName) {
        InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, categoryName));
    }

    private static String explicitOverride(String path) {
        if (SCEPTERS.contains(path)) {
            return FallenGemsCompat.isLoaded() ? STAFFS_CATEGORY : LootCategory.SWORD.getName();
        }
        return null;
    }

    private static LootCategory categorize(String path, Item item) {
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        if (item instanceof SwordItem) return LootCategory.SWORD;
        if (item instanceof AxeItem) {
            return getAttackDamage(item) > HEAVY_WEAPON_THRESHOLD ? LootCategory.HEAVY_WEAPON : LootCategory.SWORD;
        }
        if (item instanceof BowItem) return LootCategory.BOW;
        if (item instanceof CrossbowItem) return LootCategory.CROSSBOW;
        if (item instanceof TridentItem) return LootCategory.HEAVY_WEAPON;
        if (item instanceof ShieldItem) return LootCategory.SHIELD;
        if (item instanceof ArmorItem armor) {
            return switch (armor.getEquipmentSlot()) {
                case HEAD -> LootCategory.HELMET;
                case CHEST -> LootCategory.CHESTPLATE;
                case LEGS -> LootCategory.LEGGINGS;
                case FEET -> LootCategory.BOOTS;
                default -> null;
            };
        }
        return null;
    }

    private static double getAttackDamage(Item item) {
        Multimap<Attribute, AttributeModifier> mods = item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
        for (AttributeModifier m : mods.get(Attributes.ATTACK_DAMAGE)) {
            if (m.getOperation() == AttributeModifier.Operation.ADDITION) return m.getAmount();
        }
        return 0;
    }
}
