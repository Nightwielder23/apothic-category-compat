package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

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
        // FG&A registers Celestisynth weapons under its own Celestial Melee/Ranged categories, so skip the
        // whole module when it's present to avoid clashing IMC.
        if (FallenGemsCompat.isLoaded()) {
            return;
        }
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
