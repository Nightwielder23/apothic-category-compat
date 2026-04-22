package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public final class SamuraiDynastyCompat {
    private static final String NAMESPACE = "samurai_dynasty";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.of(
            "katana", LootCategory.SWORD,
            "steel_katana", LootCategory.SWORD,
            "jade_katana", LootCategory.SWORD,
            "spear", LootCategory.SWORD,
            "steel_spear", LootCategory.SWORD,
            "kama", LootCategory.SWORD,
            "sai", LootCategory.SWORD
    );

    private SamuraiDynastyCompat() {}

    public static void send() {
        for (var e : OVERRIDES.entrySet()) {
            ResourceLocation id = new ResourceLocation(NAMESPACE, e.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
