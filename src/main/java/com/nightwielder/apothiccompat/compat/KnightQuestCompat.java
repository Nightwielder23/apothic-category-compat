package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Two unrelated mods ship under the "Knight Quest" name. Namespace
 * "knightquest" is the GPL mod whose items follow {shape}_{suffix}
 * (paladin_sword, uchigatana_katana). Namespace "knight_quest" is
 * Count Grimhart's mod whose items follow kq_{kind}_{name}
 * (kq_sword_paladin). Armor and mining axes fall through to
 * UniversalCompat's vanilla-class checks in both.
 */
public final class KnightQuestCompat {
    private static final String NAMESPACE_GPL = "knightquest";
    private static final String NAMESPACE_GRIMHART = "knight_quest";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> GPL_SWORD_PATHS = Set.of(
            "cleaver", "uchigatana_katana", "nail_glaive", "kukri_dagger");

    private static final Set<String> GPL_HEAVY_PATHS = Set.of("paladin_sword");

    private static final String[] GPL_HEAVY_SUFFIXES = {};

    private static final String[] GPL_SWORD_SUFFIXES = {"_sword", "_spear"};

    private static final Set<String> GRIMHART_SWORD_PATHS = Set.of(
            "kq_sword_cleaver", "kq_sword_crimson", "kq_sword_hollow",
            "kq_sword_khopesh", "kq_sword_kukri", "kq_sword_steel",
            "kq_sword_uchigatana", "kq_sword_water");

    private static final Set<String> GRIMHART_HEAVY_PATHS = Set.of("kq_sword_paladin");

    private KnightQuestCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            LootCategory cat = categorize(id.getNamespace(), id.getPath());
            if (cat == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = cat.getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }

    private static LootCategory categorize(String namespace, String path) {
        if (NAMESPACE_GPL.equals(namespace)) {
            if (GPL_HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON;
            if (GPL_SWORD_PATHS.contains(path)) return LootCategory.SWORD;
            for (String s : GPL_HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
            for (String s : GPL_SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
            return null;
        }
        if (NAMESPACE_GRIMHART.equals(namespace)) {
            if (GRIMHART_HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON;
            if (GRIMHART_SWORD_PATHS.contains(path)) return LootCategory.SWORD;
            return null;
        }
        return null;
    }
}
