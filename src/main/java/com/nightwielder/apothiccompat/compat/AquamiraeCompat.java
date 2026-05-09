package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public final class AquamiraeCompat {
    private static final String NAMESPACE = "aquamirae";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.ofEntries(
            Map.entry("coral_lance", LootCategory.HEAVY_WEAPON),
            Map.entry("sweet_lance", LootCategory.HEAVY_WEAPON),
            Map.entry("anglers_fang", LootCategory.SWORD),
            Map.entry("dagger_of_greed", LootCategory.SWORD),
            Map.entry("fin_cutter", LootCategory.SWORD),
            Map.entry("poisoned_blade", LootCategory.SWORD),
            Map.entry("poisoned_chakra", LootCategory.SWORD),
            Map.entry("remnants_saber", LootCategory.SWORD),
            Map.entry("terrible_sword", LootCategory.SWORD),
            Map.entry("whisper_of_the_abyss", LootCategory.SWORD),
            Map.entry("abyssal_heaume", LootCategory.HELMET),
            Map.entry("abyssal_tiara", LootCategory.HELMET),
            Map.entry("terrible_helmet", LootCategory.HELMET),
            Map.entry("three_bolt_helmet", LootCategory.HELMET),
            Map.entry("abyssal_brigantine", LootCategory.CHESTPLATE),
            Map.entry("terrible_chestplate", LootCategory.CHESTPLATE),
            Map.entry("three_bolt_suit", LootCategory.CHESTPLATE),
            Map.entry("abyssal_leggings", LootCategory.LEGGINGS),
            Map.entry("terrible_leggings", LootCategory.LEGGINGS),
            Map.entry("three_bolt_leggings", LootCategory.LEGGINGS),
            Map.entry("abyssal_boots", LootCategory.BOOTS),
            Map.entry("terrible_boots", LootCategory.BOOTS),
            Map.entry("three_bolt_boots", LootCategory.BOOTS)
    );

    private AquamiraeCompat() {}

    public static void send() {
        for (var e : OVERRIDES.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, e.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
