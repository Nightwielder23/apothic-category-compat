package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Forbidden and Arcanus melee weapons extend SwordItem and route through UniversalCompat by speed.
// The draco_arcanus_scepter extends plain Item with no melee attack damage attribute, so the speed path
// never sees it; this explicit override maps it to sword.
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
