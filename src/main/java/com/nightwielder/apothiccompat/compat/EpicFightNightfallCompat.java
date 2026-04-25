package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Epic Fight Nightfall (efn) ships only a few weapons whose IDs do not follow a
 * predictable suffix convention, so they are listed explicitly. Anything else is
 * left for the SWORD fallback in UniversalCompat.
 */
public final class EpicFightNightfallCompat {
    private static final String NAMESPACE = "efn";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> HEAVY_PATHS = Set.of(
            "ruinsgreatsword", "thornwheel");

    private EpicFightNightfallCompat() {}

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
        return null;
    }
}
