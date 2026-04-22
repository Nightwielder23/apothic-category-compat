package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Marium's Soulslike Weaponry registers most weapons as TieredItem, so neither
 * Apotheosis's builtin categorization nor UniversalCompat's class-based fallback
 * picks them up. Suffix matching handles the consistent {material}_{shape} names
 * (greatswords, scythes, spears, glaives, shortswords); the rest are one-off
 * legendary names that we override explicitly. Exact overrides are checked first
 * so entries like master_sword (HEAVY per design) beat the generic _sword suffix.
 */
public final class MariumsSoulslikeCompat {
    private static final String NAMESPACE = "soulsweapons";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> HEAVY_PATHS = Set.of(
            "darkin_blade", "darkin_scythe_pre", "glaive_of_hodir", "kirkhammer",
            "leviathan_axe", "master_sword", "mjolnir", "soul_reaper",
            "whirligig_sawblade");

    private static final Set<String> SWORD_PATHS = Set.of(
            "bloodthirster", "dawnbreaker", "dragonbane", "draugr",
            "empowered_dawnbreaker", "excalibur", "featherlight", "frostmourne",
            "lich_bane", "mehrunes_razor", "moonveil", "nightfall",
            "nights_edge_item", "rageblade", "simons_blade", "skofnung",
            "sting", "tonitrus");

    private static final Set<String> BOW_PATHS = Set.of("galeforce", "kraken_slayer");

    private static final String[] HEAVY_SUFFIXES = {
            "_greatsword", "_scythe", "_glaive"
    };

    private static final String[] SWORD_SUFFIXES = {
            "_shortsword", "_swordspear", "_spear", "_sword"
    };

    private static final String[] BOW_SUFFIXES = {
            "_longbow", "_bowblade"
    };

    private static final String[] CROSSBOW_SUFFIXES = {
            "_crossbow"
    };

    private MariumsSoulslikeCompat() {}

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
        if (BOW_PATHS.contains(path)) return LootCategory.BOW;
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        for (String s : BOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.BOW;
        for (String s : CROSSBOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.CROSSBOW;
        return null;
    }
}
