package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

public final class SamuraiDynastyCompat {
    private static final String NAMESPACE = "samurai_dynasty";

    private static final Map<String, String> OVERRIDES = Map.of(
            "katana", LootCategory.SWORD.getName(),
            "steel_katana", LootCategory.SWORD.getName(),
            "jade_katana", LootCategory.SWORD.getName(),
            "spear", LootCategory.SWORD.getName(),
            "steel_spear", LootCategory.SWORD.getName(),
            "kama", LootCategory.SWORD.getName(),
            "sai", LootCategory.SWORD.getName()
    );

    private SamuraiDynastyCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.WARN);
    }
}
