package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Epic Fight Resurrection (cdmoveset) item IDs do not always insert an underscore
 * before the weapon-type token (e.g. s_irongreatsword vs s_iron_greatsword), so
 * suffixes are matched without a leading underscore. Items not matching any heavy
 * suffix are left for the SWORD fallback in UniversalCompat.
 */
public final class EpicFightResurrectionCompat {
    private static final String NAMESPACE = "cdmoveset";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String[] HEAVY_SUFFIXES = {
            "greatsword", "longsword"
    };

    private static final Set<String> EXPLICIT_HEAVIES = Set.of(
            "great_tachi");

    private EpicFightResurrectionCompat() {}

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
        if (EXPLICIT_HEAVIES.contains(path)) return LootCategory.HEAVY_WEAPON;
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        return null;
    }
}
