package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registered by item tag so every material variant is covered. Tag paths are
 * best-guesses at SW's pluralisation — a missing tag skips harmlessly; verify
 * against data/spartanweaponry/tags/items/ if affixes fail to roll.
 * Throwing weapons have no matching Apotheosis category and are left out.
 */
public final class SpartanWeaponryCompat {
    private static final String NAMESPACE = "spartanweaponry";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> TAG_CATEGORIES = new LinkedHashMap<>();
    static {
        TAG_CATEGORIES.put("daggers",         LootCategory.SWORD);          // light weapon
        TAG_CATEGORIES.put("longswords",      LootCategory.SWORD);
        TAG_CATEGORIES.put("katanas",         LootCategory.SWORD);
        TAG_CATEGORIES.put("sabers",          LootCategory.SWORD);
        TAG_CATEGORIES.put("rapiers",         LootCategory.SWORD);
        TAG_CATEGORIES.put("greatswords",     LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("battleaxes",      LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("warhammers",      LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("hammers",         LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("maces",           LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("clubs",           LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("cestuses",        LootCategory.SWORD);          // fist weapon
        TAG_CATEGORIES.put("quarterstaffs",   LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("glaives",         LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("halberds",        LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("lances",          LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("pikes",           LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("spears",          LootCategory.HEAVY_WEAPON);
        TAG_CATEGORIES.put("longbows",        LootCategory.BOW);
        TAG_CATEGORIES.put("heavy_crossbows", LootCategory.CROSSBOW);
    }

    private SpartanWeaponryCompat() {}

    public static void send() {
        for (Map.Entry<String, LootCategory> entry : TAG_CATEGORIES.entrySet()) {
            ResourceLocation tagId = new ResourceLocation(NAMESPACE, entry.getKey());
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
            if (tag.isEmpty()) continue;

            String name = entry.getValue().getName();
            for (Item item : tag) {
                InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
            }
        }
    }
}
