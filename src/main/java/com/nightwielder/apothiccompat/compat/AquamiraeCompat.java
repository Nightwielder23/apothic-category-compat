package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Most Aquamirae weapons extend SwordItem and UniversalCompat handles them by speed. The poisoned_chakra
// and maze_rose extend TieredItem directly (not SwordItem), so Apotheosis would not class them and their
// effective speed/damage does not push them to heavy; the explicit sword override here makes them roll
// sword affixes.
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
