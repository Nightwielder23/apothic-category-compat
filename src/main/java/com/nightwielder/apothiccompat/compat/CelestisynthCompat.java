package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Celestisynth's melee weapons extend SkilledSwordItem (a SwordItem) so UniversalCompat splits them by
// speed, and rainfall_serenity extends BowItem so the class branch maps it. Poltergeist is the exception:
// it extends SkilledAxeItem (an AxeItem), so an explicit override sets it to heavy_weapon rather than
// letting the speed rule decide its category.
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
