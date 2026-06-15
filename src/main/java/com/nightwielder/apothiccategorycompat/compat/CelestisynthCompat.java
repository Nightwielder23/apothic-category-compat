package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Melee weapons extend SkilledSwordItem and rainfall_serenity extends BowItem, so UniversalCompat handles
// those. Poltergeist extends SkilledAxeItem, so pin it to heavy_weapon instead of letting speed decide.
public final class CelestisynthCompat {
    private static final String NAMESPACE = "celestisynth";

    private static final Map<String, String> OVERRIDES = Map.of(
            "poltergeist", LootCategory.HEAVY_WEAPON.getName()
    );

    private CelestisynthCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
