package com.nightwielder.apothiccompat.util;

import com.google.common.collect.Multimap;
import com.nightwielder.apothiccompat.compat.RegistryLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.function.BiConsumer;

// One spot for the Apotheosis loot-category IMC, so the target mod id, method name, and payload shape stay
// in one place.
public final class CompatImc {
    public static final String APOTHEOSIS_MOD_ID = "apotheosis";
    public static final String IMC_METHOD = "loot_category_override";

    // Obscure API labels a weapon by attack speed: at or below 0.6 very slow, 1.0 slow, 2.0 medium, 3.0
    // fast, otherwise very fast. Very slow and slow both read as heavy, so a speed at or below this maps
    // to HEAVY_WEAPON.
    public static final double SLOW_SPEED_MAX = 1.0;

    // A medium-or-faster weapon that still hits this hard plays as heavy, so it maps to HEAVY_WEAPON
    // regardless of speed. Tuned to catch greataxe and maul tier hitters that speed alone reads as swords.
    public static final double HEAVY_DAMAGE_THRESHOLD = 10.0;

    // WARN logs a registry miss via RegistryLookup, SILENT drops a missing id quietly.
    public enum SkipMode { WARN, SILENT }

    private CompatImc() {}

    // Where send() routes. The default is the startup IMC channel; the second pass swaps in a runtime sink
    // that writes Apotheosis's override maps directly, so the same module code re-runs unchanged after
    // deferred-init mods finalize their attributes.
    private static BiConsumer<Item, String> sink = CompatImc::sendImc;

    public static void setSink(BiConsumer<Item, String> newSink) {
        sink = newSink;
    }

    public static void resetSink() {
        sink = CompatImc::sendImc;
    }

    public static void send(Item item, String categoryName) {
        sink.accept(item, categoryName);
    }

    private static void sendImc(Item item, String categoryName) {
        InterModComms.sendTo(APOTHEOSIS_MOD_ID, IMC_METHOD, () -> Map.entry(item, categoryName));
    }

    public static void sendOverrides(String namespace, Map<String, String> overrides, SkipMode skipMode) {
        for (Map.Entry<String, String> e : overrides.entrySet()) {
            ResourceLocation id = new ResourceLocation(namespace, e.getKey());
            Item item = resolve(id, skipMode);
            if (item == null) {
                continue;
            }
            send(item, e.getValue());
        }
    }

    private static Item resolve(ResourceLocation id, SkipMode skipMode) {
        if (skipMode == SkipMode.WARN) {
            return RegistryLookup.item(id);
        }
        if (!ForgeRegistries.ITEMS.containsKey(id)) {
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(id);
    }

    // Cheap static precheck reading the item's own defaults (no event fires): a positive ADDITION on
    // attack damage means the item swings as a melee weapon, so callers confirm that before paying for the
    // live read below.
    public static double getAttackDamageGeneric(Item item) {
        Multimap<Attribute, AttributeModifier> mods = item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
        double damage = 0;
        for (AttributeModifier m : mods.get(Attributes.ATTACK_DAMAGE)) {
            if (m.getOperation() == AttributeModifier.Operation.ADDITION) {
                damage += m.getAmount();
            }
        }
        return damage;
    }

    // Live main-hand attack speed, the value vanilla and Obscure show in the tooltip.
    public static double getAttackSpeed(ItemStack stack) {
        return liveValue(stack, Attributes.ATTACK_SPEED, 4.0);
    }

    // Live main-hand effective attack damage, the value vanilla shows in the tooltip.
    public static double getAttackDamage(ItemStack stack) {
        return liveValue(stack, Attributes.ATTACK_DAMAGE, 1.0);
    }

    // base + summed ADDITION + addition sum scaled by summed MULTIPLY_BASE, read through
    // stack.getAttributeModifiers rather than the item defaults. That fires Forge's
    // ItemAttributeModifierEvent, so mods that adjust a weapon's attributes at runtime are reflected.
    private static double liveValue(ItemStack stack, Attribute attribute, double base) {
        Multimap<Attribute, AttributeModifier> mods = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        double addition = 0;
        double multiplyBase = 0;
        for (AttributeModifier m : mods.get(attribute)) {
            if (m.getOperation() == AttributeModifier.Operation.ADDITION) {
                addition += m.getAmount();
            } else if (m.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                multiplyBase += m.getAmount();
            }
        }
        return base + addition + addition * multiplyBase;
    }
}
