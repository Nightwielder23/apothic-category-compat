package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Suffix-matched rather than tag-matched because item tags are not bound at
 * InterModEnqueueEvent time, so the tag-lookup path would send zero overrides.
 * SW 3.2.1 item IDs follow a {material}_{weapontype} convention. Throwing
 * weapons have no Apotheosis category and are skipped.
 */
public final class SpartanWeaponryCompat {
    private static final String NAMESPACE = "spartanweaponry";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String[] SWORD_SUFFIXES = {
            "_parrying_dagger", "_dagger", "_longsword", "_katana", "_saber", "_rapier",
            "_spear", "_javelin"
    };

    private static final String[] HEAVY_SUFFIXES = {
            "_greatsword", "_battleaxe", "_battle_hammer", "_warhammer", "_flanged_mace",
            "_club", "_quarterstaff", "_glaive", "_halberd", "_lance", "_pike", "_scythe"
    };

    private static final String[] BOW_SUFFIXES = {
            "_longbow"
    };

    private static final String[] CROSSBOW_SUFFIXES = {
            "_heavy_crossbow"
    };

    private SpartanWeaponryCompat() {}

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
        if (path.equals("cestus") || path.endsWith("_cestus")) return LootCategory.HEAVY_WEAPON;
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD;
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        for (String s : BOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.BOW;
        for (String s : CROSSBOW_SUFFIXES) if (path.endsWith(s)) return LootCategory.CROSSBOW;
        return null;
    }
}
