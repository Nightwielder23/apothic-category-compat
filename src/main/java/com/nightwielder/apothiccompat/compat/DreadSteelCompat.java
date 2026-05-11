package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;

import java.util.Map;

public final class DreadSteelCompat {
    private static final String NAMESPACE = "dreadsteel";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.of(
            "dreadsteel_scythe", LootCategory.HEAVY_WEAPON,
            "dreadsteel_shield", LootCategory.SHIELD
    );

    private DreadSteelCompat() {}

    public static void send() {
        for (var e : OVERRIDES.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, e.getKey());
            Item item = RegistryLookup.item(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
