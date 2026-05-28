package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Twilight Forest's standard gear (swords, bows, axes, pickaxes, shields, armor)
 * extends the vanilla item classes, so UniversalCompat already categorizes it.
 * These overrides cover items where class-based inference is wrong: the chain
 * weapon and the four scepters all extend plain Item, and the scepters should
 * still roll sword affixes. ice_bomb and mazebreaker_pickaxe match what the
 * fallback already picks; they are listed to make the intended category explicit
 * rather than to correct it.
 */
public final class TwilightForestCompat {
    private static final String NAMESPACE = "twilightforest";
    private static final String IMC_METHOD = "loot_category_override";
    private static final Map<String, LootCategory> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("block_and_chain", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("ice_bomb", LootCategory.NONE);
        OVERRIDES.put("lifedrain_scepter", LootCategory.SWORD);
        OVERRIDES.put("fortification_scepter", LootCategory.SWORD);
        OVERRIDES.put("twilight_scepter", LootCategory.SWORD);
        OVERRIDES.put("zombie_scepter", LootCategory.SWORD);
        OVERRIDES.put("mazebreaker_pickaxe", LootCategory.PICKAXE);
    }

    private TwilightForestCompat() {}

    public static void send() {
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, e.getKey());
            // Skip items missing from the installed TF version quietly. containsKey
            // avoids the warn that RegistryLookup would log on a version-skew miss.
            if (!ForgeRegistries.ITEMS.containsKey(id)) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
