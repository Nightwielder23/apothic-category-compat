package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.ApothicCompat;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * UniversalCompat handles Epic Knights' vanilla-class weapons and armor. Two
 * gaps need explicit overrides: shields (miscategorized by default, flagged
 * in Soulrend's v7.2 patch notes) and polearms/mauls that extend SwordItem
 * but should roll heavy-weapon affixes. Substring-matched on registry path
 * because Epic Knights' tag tree isn't publicly documented.
 */
public final class EpicKnightsCompat {
    private static final String NAMESPACE = "magistuarmory";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String[] HEAVY_TOKENS = {
            "lance", "glaive", "halberd", "pike",
            "mace", "warhammer", "hammer", "maul"
    };

    private EpicKnightsCompat() {}

    public static void send() {
        try {
            for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
                ResourceLocation id = entry.getKey().location();
                if (!NAMESPACE.equals(id.getNamespace())) continue;

                LootCategory cat = categorize(id.getPath());
                if (cat == null) continue;

                Item item = entry.getValue();
                String name = cat.getName();
                InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
            }
        } catch (Exception e) {
            ApothicCompat.LOGGER.warn("[EpicKnights] iteration failed; some items may not be categorized", e);
        }
    }

    private static LootCategory categorize(String path) {
        if (path.contains("shield")) return LootCategory.SHIELD;
        for (String token : HEAVY_TOKENS) {
            if (path.contains(token)) return LootCategory.HEAVY_WEAPON;
        }
        return null;
    }
}
