package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WeaponsOfMiraclesCompat {
    private static final String NAMESPACE = "wom";
    private static final String IMC_METHOD = "loot_category_override";
    private static final Map<String, LootCategory> OVERRIDES = new LinkedHashMap<>();

    static {
        put(LootCategory.SWORD,
                "antitheus", "claw", "evil_tachi", "gesetz",
                "hollow_longsword", "jabberwocky", "moonless", "napoleon",
                "netherite_tachi", "nova", "orbit", "ruine", "satsujin",
                "solar_obscuridad", "tormented_mind",
                "wooden_staff", "stone_staff", "iron_staff", "golden_staff",
                "diamond_staff", "netherite_staff");
        put(LootCategory.HEAVY_WEAPON,
                "agony", "blackstar", "herrscher", "solar",
                "iron_greataxe", "golden_greataxe", "diamond_greataxe",
                "netherite_greataxe");
        put(LootCategory.SHIELD, "overly_large_cylinder");
        put(LootCategory.HELMET,
                "cursed_mask", "unholy_cursed_mask", "netherite_mask",
                "diamond_crown", "golden_monocle");
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
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            if (OVERRIDES.containsKey(id.getPath())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            LootCategory cat = categorize(item);
            if (cat == null) continue;
            String name = cat.getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
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
