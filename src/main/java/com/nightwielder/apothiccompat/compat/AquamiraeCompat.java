package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Most Aquamirae weapons extend SwordItem and UniversalCompat splits them by speed. The lances are
// SwordItem too, and the armor extends ArmorItem. The poisoned_chakra and maze_rose extend TieredItem
// directly with no attack damage attribute, so the speed path never reaches them. The user wants both as
// swords, so they keep explicit overrides.
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
