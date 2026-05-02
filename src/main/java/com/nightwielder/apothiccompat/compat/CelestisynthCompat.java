package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * Celestisynth weapons use one-off legendary names (crescentia, frostbound,
 * keres, etc.) with no shared suffix, so exact-name matching is the only option.
 * Most extend SkilledSwordItem (which extends SwordItem) and would already be
 * picked up by Apotheosis's builtin SwordItem match, but we send explicit
 * overrides so poltergeist (SkilledAxeItem, under UniversalCompat's 8.0 HEAVY
 * threshold) and rainfall_serenity (BowItem) land in the right bucket.
 */
public final class CelestisynthCompat {
    private static final String NAMESPACE = "celestisynth";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Set<String> SWORD_PATHS = Set.of(
            "aquaflora", "breezebreaker", "crescentia", "frostbound",
            "keres", "solaris");

    private static final Set<String> HEAVY_PATHS = Set.of(
            "poltergeist");

    private static final Set<String> BOW_PATHS = Set.of(
            "rainfall_serenity");

    private CelestisynthCompat() {}

    public static void send() {
        // FG&A registers Celestisynth weapons under its own Celestial Melee/Ranged
        // categories. Skip the whole module when it's present to avoid clashing IMC.
        if (FallenGemsCompat.isLoaded()) return;
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
        return null;
    }
}
