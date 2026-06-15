package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Most Aquamirae weapons extend SwordItem, which UniversalCompat handles by speed. poisoned_chakra and
// maze_rose extend TieredItem directly, so the class and speed checks miss them; override them to sword.
public final class AquamiraeCompat {
    private static final String NAMESPACE = "aquamirae";

    private static final Map<String, String> OVERRIDES = Map.of(
            "poisoned_chakra", LootCategory.SWORD.getName(),
            "maze_rose", LootCategory.SWORD.getName()
    );

    private AquamiraeCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
