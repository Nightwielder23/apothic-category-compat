package com.nightwielder.apothiccompat.util;

import com.google.common.collect.Multimap;
import com.nightwielder.apothiccompat.compat.RegistryLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Single entry point for the Apotheosis loot-category IMC. Every module sends through
 * here so the target mod id, method name, and payload shape stay in one place.
 */
public final class CompatImc {
    public static final String APOTHEOSIS_MOD_ID = "apotheosis";
    public static final String IMC_METHOD = "loot_category_override";
    public static final double HEAVY_WEAPON_THRESHOLD = 8.0;

    /** WARN logs a registry miss via RegistryLookup; SILENT skips a missing id quietly. */
    public enum SkipMode { WARN, SILENT }

    private CompatImc() {}

    public static void send(Item item, String categoryName) {
        InterModComms.sendTo(APOTHEOSIS_MOD_ID, IMC_METHOD, () -> Map.entry(item, categoryName));
    }

    /**
     * Sends every path-to-category entry under one namespace. WARN logs a miss when an
     * item id is absent from the registry; SILENT drops it quietly, which suits mods
     * whose item set changes between versions.
     */
    public static void sendOverrides(String namespace, Map<String, String> overrides, SkipMode skipMode) {
        for (Map.Entry<String, String> e : overrides.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, e.getKey());
            Item item = resolve(id, skipMode);
            if (item == null) continue;
            send(item, e.getValue());
        }
    }

    private static Item resolve(ResourceLocation id, SkipMode skipMode) {
        if (skipMode == SkipMode.WARN) return RegistryLookup.item(id);
        if (!ForgeRegistries.ITEMS.containsKey(id)) return null;
        return ForgeRegistries.ITEMS.getValue(id);
    }

    /** First flat ADDITION modifier on the item's main-hand attack damage, or 0 if none. */
    public static double getAttackDamage(Item item) {
        Multimap<Attribute, AttributeModifier> mods = item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
        for (AttributeModifier m : mods.get(Attributes.ATTACK_DAMAGE)) {
            if (m.getOperation() == AttributeModifier.Operation.ADDITION) return m.getAmount();
        }
        return 0;
    }
}
