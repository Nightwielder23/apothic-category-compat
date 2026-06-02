package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Epic Samurai's bladed weapons extend SwordItem, so UniversalCompat splits them by attack speed. The
// shuriken is a thrown plain Item with no melee attack-damage attribute, so the speed path never reaches
// it; this explicit override lets it roll sword affixes per the user's request.
public final class EpicSamuraiCompat {
    private static final String NAMESPACE = "epicsamurai";

    private static final Map<String, String> OVERRIDES = Map.of(
            "shuriken", LootCategory.SWORD.getName()
    );

    private EpicSamuraiCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
