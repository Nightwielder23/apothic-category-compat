package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Integrated Simply Swords adds Simply Swords-style weapons under cross-mod
 * material variants. Item paths use slash separators in the form
 * {sourcemod}/{material}/{weapontype}, so suffixes match "/{type}". Same
 * weapon-to-category mapping as SimplySwordsCompat.
 */
public final class IntegratedSimplySwordsCompat {
    private static final String NAMESPACE = "integrated_simply_swords";
    private static final String IMC_METHOD = "loot_category_override";

    // Galenic Polarizer extends a SwordItem subclass but plays as a heavy
    // ranged launcher; TravelopticsCompat treats galenic_polarizer as HEAVY,
    // so the cross-integrated copy follows suit.
    private static final Set<String> HEAVY_PATHS = Set.of(
            "alexscaves/polarizer");

    private static final Set<String> SWORD_PATHS = Set.of();

    private static final String[] HEAVY_SUFFIXES = {
            "/glaive", "/greataxe", "/greathammer", "/halberd"
    };

    private static final String[] SWORD_SUFFIXES = {
            "/chakram", "/claymore", "/cutlass", "/katana", "/longsword", "/rapier",
            "/sai", "/scythe", "/spear", "/twinblade", "/warglaive"
    };

    private IntegratedSimplySwordsCompat() {}

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
