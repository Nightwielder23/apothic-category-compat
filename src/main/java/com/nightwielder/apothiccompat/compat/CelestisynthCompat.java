package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Celestisynth's melee weapons extend SkilledSwordItem (a SwordItem) so UniversalCompat splits them by
// speed, and rainfall_serenity extends BowItem so the class branch maps it. Poltergeist is the exception:
// it extends SkilledAxeItem (an AxeItem) but the user wants it heavy regardless of its speed, so it keeps
// an explicit override.
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
