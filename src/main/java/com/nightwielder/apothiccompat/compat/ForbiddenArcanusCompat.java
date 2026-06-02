package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Forbidden and Arcanus melee weapons extend SwordItem and route through UniversalCompat by speed.
// draco_arcanus_scepter extends plain Item with no melee attack-damage attribute, so the speed path never
// sees it; this explicit override maps it to sword per the user's request.
public final class ForbiddenArcanusCompat {
    private static final String NAMESPACE = "forbidden_arcanus";

    private static final Map<String, String> OVERRIDES = Map.of(
            "draco_arcanus_scepter", LootCategory.SWORD.getName()
    );

    private ForbiddenArcanusCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
