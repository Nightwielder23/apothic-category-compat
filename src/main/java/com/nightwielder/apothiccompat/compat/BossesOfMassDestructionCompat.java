package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Bosses of Mass Destruction only has a couple of custom-class weapons that
 * Apotheosis and UniversalCompat don't categorize on their own.
 */
public final class BossesOfMassDestructionCompat {
    private static final String NAMESPACE = "bosses_of_mass_destruction";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> HEAVY_PATHS = Set.of();

    private static final Set<String> SWORD_PATHS = Set.of("nether_staff", "obsidian_spear");

    private BossesOfMassDestructionCompat() {}

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
