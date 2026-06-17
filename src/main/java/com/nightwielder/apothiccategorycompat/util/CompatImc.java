package com.nightwielder.apothiccategorycompat.util;

import com.google.common.collect.Multimap;
import com.nightwielder.apothiccategorycompat.compat.RegistryLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.function.BiConsumer;

// One spot for the Apotheosis loot category IMC. Everything sends through here so the target mod id,
// method name, and payload shape stay in one place.
public final class CompatImc {
    public static final String APOTHEOSIS_MOD_ID = "apotheosis";
    public static final String IMC_METHOD = "loot_category_override";

    // Obscure labels weapons by attack speed (<=0.6 very slow, 1.0 slow, 2.0 medium, 3.0 fast). Very slow
    // and slow both read as heavy, so a speed at or below this maps to HEAVY_WEAPON.
    public static final double SLOW_SPEED_MAX = 1.0;

    // A medium or faster weapon that still hits this hard plays as heavy, so it maps to HEAVY_WEAPON
    // regardless of speed. Tuned to catch greataxe and maul tier hitters that speed alone reads as swords.
    public static final double HEAVY_DAMAGE_THRESHOLD = 10.0;

    // WARN logs a registry miss via RegistryLookup; SILENT skips a missing id quietly
    public enum SkipMode { WARN, SILENT }

    private CompatImc() {}

    // Where send() routes. Default is the startup IMC channel; the second pass swaps in a runtime sink that
    // writes Apoth's override maps directly, so module code reruns unchanged after deferred init.
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

    // Sends each path+category pair under one namespace.
    public static void sendOverrides(String namespace, Map<String, String> overrides, SkipMode skipMode) {
        for (Map.Entry<String, String> e : overrides.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, e.getKey());
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

    // Cheap static precheck on the item's defaults (no event fires): a positive ADDITION on attack damage
    // means it swings as a melee weapon, so the stack overload confirms that before paying for the live read.
    private static double getAttackDamageGeneric(Item item) {
        return sumAdditionAttackDamage(item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
    }

    // Same precheck, but falls back to the stack-level read when the item default reads zero. Epic Fight
    // weapons and its addons (Weapons of Miracles) leave the item default at zero and add their attack damage
    // through the stack-level getAttributeModifiers, so the item-only read would drop them before the speed
    // and name rules ever run. The stack read fires ItemAttributeModifierEvent, which a few mods throw out of
    // (Enigmatic Addons), so a failure counts as no attack damage, the same outcome as an item-level zero.
    public static double getAttackDamageGeneric(ItemStack stack) {
        double itemLevel = getAttackDamageGeneric(stack.getItem());
        if (itemLevel > 0) {
            return itemLevel;
        }
        // Gate the stack read on TieredItem so ItemAttributeModifierEvent only fires for weapon-class items,
        // not the BlockItems, food, and plain Items that make up most of the registry. SwordItem and
        // DiggerItem extend TieredItem, so Epic Fight's WeaponItem and the WoM greataxes still get read.
        if (!(stack.getItem() instanceof TieredItem)) {
            return 0;
        }
        try {
            return sumAdditionAttackDamage(stack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        } catch (Throwable t) {
            return 0;
        }
    }

    private static double sumAdditionAttackDamage(Multimap<Attribute, AttributeModifier> mods) {
        double damage = 0;
        for (AttributeModifier m : mods.get(Attributes.ATTACK_DAMAGE)) {
            if (m.getOperation() == AttributeModifier.Operation.ADDITION) {
                damage += m.getAmount();
            }
        }
        return damage;
    }

    public static double getAttackSpeed(ItemStack stack) {
        return liveValue(stack, Attributes.ATTACK_SPEED, 4.0);
    }

    public static double getAttackDamage(ItemStack stack) {
        return liveValue(stack, Attributes.ATTACK_DAMAGE, 1.0);
    }

    // Vanilla folds the base into the additions before the MULTIPLY_BASE scale, so scaling only the
    // additions would underreport. Read through stack.getAttributeModifiers, not item defaults, so Forge's
    // ItemAttributeModifierEvent fires and runtime attribute changes from other mods are reflected.
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
        return (base + addition) * (1 + multiplyBase);
    }
}
