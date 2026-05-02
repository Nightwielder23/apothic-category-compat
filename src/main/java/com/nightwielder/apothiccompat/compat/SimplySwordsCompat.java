package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Simply Swords has no per-weapon-type item tags in 1.20.1, only material-based
 * tags (iron_gear, gold_gear, etc.) plus a catch-all "swords" tag. The mod's item IDs
 * follow a consistent {material}_{weapontype} convention, so we match on suffix.
 * Uniques that don't follow the convention fall through to UniversalCompat.
 */
public final class SimplySwordsCompat {
    private static final String NAMESPACE = "simplyswords";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String[] SWORD_SUFFIXES = {
            "_longsword", "_claymore", "_rapier", "_katana", "_chakram",
            "_cutlass", "_twinblade", "_scythe", "_sai", "_warglaive", "_spear"
    };

    private static final String[] HEAVY_SUFFIXES = {
            "_greathammer", "_greataxe", "_glaive", "_halberd"
    };

    private SimplySwordsCompat() {}

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
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        return null;
    }
}
