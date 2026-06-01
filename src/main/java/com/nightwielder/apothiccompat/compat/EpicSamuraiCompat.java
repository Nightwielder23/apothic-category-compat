package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

public final class EpicSamuraiCompat {
    private static final String NAMESPACE = "epicsamurai";

    private static final Map<String, String> OVERRIDES = Map.of(
            "katana", LootCategory.SWORD.getName(),
            "kama", LootCategory.SWORD.getName(),
            "sai", LootCategory.SWORD.getName(),
            "nagamaki", LootCategory.SWORD.getName(),
            "naginata", LootCategory.SWORD.getName(),
            "kunai", LootCategory.NONE.getName(),
            "shuriken", LootCategory.NONE.getName()
    );

    private EpicSamuraiCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
