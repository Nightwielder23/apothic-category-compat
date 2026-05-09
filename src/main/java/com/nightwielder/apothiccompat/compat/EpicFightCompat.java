package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Base Epic Fight has five material-suffixed weapon families (greatsword,
 * longsword, dagger, spear, tachi) plus three named uniques (bokken,
 * uchigatana, glove). Greatswords are HEAVY; everything else is SWORD per
 * project convention (spears, tachis, gloves all light melee).
 */
public final class EpicFightCompat {
    private static final String NAMESPACE = "epicfight";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> HEAVY_PATHS = Set.of();

    private static final Set<String> SWORD_PATHS = Set.of(
            "bokken", "glove", "uchigatana");

    private static final String[] HEAVY_SUFFIXES = {"_greatsword"};

    private static final String[] SWORD_SUFFIXES = {
            "_dagger", "_longsword", "_spear", "_tachi"
    };

    private EpicFightCompat() {}

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
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        return null;
    }
}
