package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Enigmatic Legacy combat items. The Voracious Pan (eldritch_pan) extends
 * ShieldItem but is used as a melee weapon, and Astral Breaker extends
 * PickaxeItem but is used as a two-handed weapon; both would be miscategorized
 * by the vanilla-class fallback. Curios, rings, scrolls, and armor fall
 * through to Apotheosis/Apothic Curios.
 */
public final class EnigmaticLegacyCompat {
    private static final String NAMESPACE = "enigmaticlegacy";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> SWORD_PATHS = Set.of(
            "eldritch_pan", "etherium_sword");

    private static final Set<String> HEAVY_PATHS = Set.of(
            "forbidden_axe", "astral_breaker", "etherium_scythe",
            "etherium_waraxe");

    private EnigmaticLegacyCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            LootCategory cat = categorize(id.getPath());
            if (cat == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = cat.getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }

    private static LootCategory categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON;
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD;
        return null;
    }
}
