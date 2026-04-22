package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Forbidden Arcanus weapons: the draco_arcanus set plus mystical_dagger are
 * explicit, and material-gavels follow a consistent _blacksmith_gavel suffix
 * so they're swept up via suffix fallback. Trinkets, seeds, orbs, amulets,
 * etc. fall through to UniversalCompat.
 */
public final class ForbiddenArcanusCompat {
    private static final String NAMESPACE = "forbidden_arcanus";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> SWORD_PATHS = Set.of(
            "mystical_dagger", "draco_arcanus_scepter", "draco_arcanus_sword");

    private static final Set<String> HEAVY_PATHS = Set.of("draco_arcanus_axe");

    private static final String[] SWORD_SUFFIXES = {"_blacksmith_gavel"};

    private ForbiddenArcanusCompat() {}

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
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        return null;
    }
}
