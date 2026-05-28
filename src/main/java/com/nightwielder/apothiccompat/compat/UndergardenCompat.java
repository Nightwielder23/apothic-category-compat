package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Undergarden weapons mostly extend vanilla classes that UniversalCompat already
 * categorizes. Two cases need correcting: the battleaxes extend SwordItem, so the
 * fallback would file them as swords instead of heavy weapons, and the slingshot
 * extends ProjectileWeaponItem directly rather than BowItem, so the fallback skips
 * it. spear and utherium_battleaxe are absent from UG 0.8.14 but may return in a
 * later version, so they stay listed and skip silently when missing.
 */
public final class UndergardenCompat {
    private static final String NAMESPACE = "undergarden";
    private static final String IMC_METHOD = "loot_category_override";
    private static final Map<String, LootCategory> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("cloggrum_battleaxe", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("forgotten_battleaxe", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("froststeel_battleaxe", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("utherium_battleaxe", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("spear", LootCategory.SWORD);
        OVERRIDES.put("slingshot", LootCategory.BOW);
    }

    private UndergardenCompat() {}

    public static void send() {
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, e.getKey());
            // Skip items missing from the installed UG version quietly. containsKey
            // avoids the warn that RegistryLookup would log on a version-skew miss.
            if (!ForgeRegistries.ITEMS.containsKey(id)) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
