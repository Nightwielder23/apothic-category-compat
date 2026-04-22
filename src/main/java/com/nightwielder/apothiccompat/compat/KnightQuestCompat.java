package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Knight Quest ships matched armor/weapon sets. Armor is covered by
 * UniversalCompat's vanilla-class fallback; the weapons use consistent shape
 * suffixes (_sword, _spear, _axe), and any set-named weapons without a shape
 * suffix are listed exactly. Knight Quest axes are battle axes, not tools, so
 * the _axe suffix is safe to treat as HEAVY_WEAPON within this namespace.
 */
public final class KnightQuestCompat {
    private static final String NAMESPACE = "knightquest";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> SWORD_PATHS = Set.of(
            "paladin_sword", "nightlord_sword", "fireforged_sword",
            "dawnbringer_sword");

    private static final Set<String> HEAVY_PATHS = Set.of("hoplite_spear");

    private static final String[] HEAVY_SUFFIXES = {"_spear", "_axe"};

    private static final String[] SWORD_SUFFIXES = {"_sword"};

    private KnightQuestCompat() {}

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
