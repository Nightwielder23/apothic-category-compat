package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Meet Your Fight boss drops — pickaxe/axe/shovel/hoe variants are vanilla-class
 * tools that UniversalCompat handles, so only the combat weapons need overrides.
 * Suffix fallback covers any additional weapon-shaped items added in later
 * versions without needing an exact-name update.
 */
public final class MeetYourFightCompat {
    private static final String NAMESPACE = "meetyourfight";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> SWORD_PATHS = Set.of(
            "mossy_sword", "shoulder_revolver", "dusk_blade");

    private static final Set<String> HEAVY_PATHS = Set.of("dusk_greatsword");

    private static final Set<String> CROSSBOW_PATHS = Set.of("bell_crossbow");

    private static final String[] HEAVY_SUFFIXES = {"_greatsword"};

    private static final String[] SWORD_SUFFIXES = {"_sword"};

    private static final String[] CROSSBOW_SUFFIXES = {"_crossbow"};

    private MeetYourFightCompat() {}

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
        if (CROSSBOW_PATHS.contains(path)) return LootCategory.CROSSBOW;
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        for (String s : CROSSBOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.CROSSBOW;
        return null;
    }
}
