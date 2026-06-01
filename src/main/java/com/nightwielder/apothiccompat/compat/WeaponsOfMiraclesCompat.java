package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WeaponsOfMiraclesCompat {
    private static final String NAMESPACE = "wom";
    private static final Map<String, LootCategory> OVERRIDES = new LinkedHashMap<>();

    static {
        put(LootCategory.SWORD,
                "antitheus", "gesetz", "moonless", "ruine", "satsujin",
                "tormented_mind",
                "wooden_staff", "stone_staff", "iron_staff", "golden_staff",
                "diamond_staff", "netherite_staff");
        put(LootCategory.HEAVY_WEAPON,
                "agony", "herrscher", "solar",
                "iron_greataxe", "golden_greataxe", "diamond_greataxe",
                "netherite_greataxe");
        put(LootCategory.HELMET,
                "netherite_mask", "diamond_crown", "golden_monocle");
        put(LootCategory.CHESTPLATE, "golden_kit", "netherite_manicle");
        put(LootCategory.LEGGINGS, "diamond_legtopseal", "emerald_tasset");
        put(LootCategory.BOOTS, "diamond_legbottomseal", "golden_mokassin");
    }

    private WeaponsOfMiraclesCompat() {}

    private static void put(LootCategory cat, String... ids) {
        for (String id : ids) OVERRIDES.put(id, cat);
    }

    public static void send() {
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            ResourceLocation id = new ResourceLocation(NAMESPACE, e.getKey());
            Item item = RegistryLookup.item(id);
            if (item == null) continue;
            CompatImc.send(item, e.getValue().getName());
        }
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            if (OVERRIDES.containsKey(id.getPath())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            LootCategory cat = categorize(item);
            if (cat == null) continue;
            CompatImc.send(item, cat.getName());
        }
    }

    private static LootCategory categorize(Item item) {
        if (item instanceof SwordItem) return LootCategory.SWORD;
        if (item instanceof AxeItem) return LootCategory.HEAVY_WEAPON;
        if (item instanceof BowItem) return LootCategory.BOW;
        if (item instanceof CrossbowItem) return LootCategory.CROSSBOW;
        return null;
    }
}
