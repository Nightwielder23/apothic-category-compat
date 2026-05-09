package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * RPG Style More Weapons R adds a small set of class-themed weapons and armor.
 * Armor extends ArmorItem and is handled by UniversalCompat. Only the three
 * weapon families need explicit overrides.
 */
public final class RpgStyleMoreWeaponsCompat {
    private static final String NAMESPACE = "rpg_style_more_weapons_r";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String[] HEAVY_SUFFIXES = {
            "_battle_axe", "_greatsword"
    };

    private static final String[] SWORD_SUFFIXES = {
            "_knife"
    };

    private RpgStyleMoreWeaponsCompat() {}

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
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        return null;
    }
}
