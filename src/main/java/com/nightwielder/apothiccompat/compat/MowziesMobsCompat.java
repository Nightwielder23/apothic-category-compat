package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

public final class MowziesMobsCompat {
    private static final String NAMESPACE = "mowziesmobs";

    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("spear", LootCategory.SWORD.getName()),
            Map.entry("wrought_axe", LootCategory.HEAVY_WEAPON.getName()),
            Map.entry("naga_fang_dagger", LootCategory.SWORD.getName()),
            Map.entry("earthrend_gauntlet", LootCategory.SWORD.getName()),
            Map.entry("blowgun", LootCategory.BOW.getName()),
            Map.entry("sol_visage", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask_bliss", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask_faith", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask_fear", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask_fury", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask_misery", LootCategory.HELMET.getName()),
            Map.entry("umvuthana_mask_rage", LootCategory.HELMET.getName()),
            Map.entry("wrought_helmet", LootCategory.HELMET.getName()),
            Map.entry("geomancer_robe", LootCategory.CHESTPLATE.getName()),
            Map.entry("geomancer_sandals", LootCategory.BOOTS.getName())
    );

    private MowziesMobsCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.WARN);
    }
}
