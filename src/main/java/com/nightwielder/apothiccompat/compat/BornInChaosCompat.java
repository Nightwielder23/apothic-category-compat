package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Born in Chaos is MCreator-generated so weapons don't extend vanilla weapon
 * classes and would otherwise go uncategorized. Most follow a consistent
 * {name}_{shape} convention (_sword, _axe, _hammer, _scythe, _dagger, _mace,
 * _cutlass, _blade) so suffix matching covers them; the handful that don't —
 * darkwarblade (no underscore), trident_hayfork, soulbane — are listed exactly.
 */
public final class BornInChaosCompat {
    private static final String NAMESPACE = "born_in_chaos_v1";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> HEAVY_PATHS = Set.of(
            "darkwarblade", "trident_hayfork");

    private static final Set<String> SWORD_PATHS = Set.of(
            "soulbane");

    private static final String[] HEAVY_SUFFIXES = {
            "_scythe", "_axe", "_hammer", "_mace"
    };

    private static final String[] SWORD_SUFFIXES = {
            "_sword", "_dagger", "_cutlass", "_blade"
    };

    private BornInChaosCompat() {}

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
