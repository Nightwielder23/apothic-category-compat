package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

public final class AquamiraeCompat {
    private static final String NAMESPACE = "aquamirae";

    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("coral_lance", LootCategory.HEAVY_WEAPON.getName()),
            Map.entry("sweet_lance", LootCategory.HEAVY_WEAPON.getName()),
            Map.entry("anglers_fang", LootCategory.SWORD.getName()),
            Map.entry("dagger_of_greed", LootCategory.SWORD.getName()),
            Map.entry("fin_cutter", LootCategory.SWORD.getName()),
            Map.entry("poisoned_blade", LootCategory.SWORD.getName()),
            Map.entry("poisoned_chakra", LootCategory.SWORD.getName()),
            Map.entry("remnants_saber", LootCategory.SWORD.getName()),
            Map.entry("terrible_sword", LootCategory.SWORD.getName()),
            Map.entry("whisper_of_the_abyss", LootCategory.SWORD.getName()),
            Map.entry("abyssal_heaume", LootCategory.HELMET.getName()),
            Map.entry("abyssal_tiara", LootCategory.HELMET.getName()),
            Map.entry("terrible_helmet", LootCategory.HELMET.getName()),
            Map.entry("three_bolt_helmet", LootCategory.HELMET.getName()),
            Map.entry("abyssal_brigantine", LootCategory.CHESTPLATE.getName()),
            Map.entry("terrible_chestplate", LootCategory.CHESTPLATE.getName()),
            Map.entry("three_bolt_suit", LootCategory.CHESTPLATE.getName()),
            Map.entry("abyssal_leggings", LootCategory.LEGGINGS.getName()),
            Map.entry("terrible_leggings", LootCategory.LEGGINGS.getName()),
            Map.entry("three_bolt_leggings", LootCategory.LEGGINGS.getName()),
            Map.entry("abyssal_boots", LootCategory.BOOTS.getName()),
            Map.entry("terrible_boots", LootCategory.BOOTS.getName()),
            Map.entry("three_bolt_boots", LootCategory.BOOTS.getName())
    );

    private AquamiraeCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.WARN);
    }
}
