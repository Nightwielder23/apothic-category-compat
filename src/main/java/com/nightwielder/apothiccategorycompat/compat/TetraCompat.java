package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Tetra's modular items all extend ModularItem (ultimately plain Item), so they never match the vanilla
// class checks: the bow, crossbow, and shield need explicit ids. The melee builds (modular_sword,
// modular_double, modular_single) carry attack damage, so UniversalCompat splits them by attack speed.
public final class TetraCompat {
    private static final String NAMESPACE = "tetra";

    private static final Map<String, String> OVERRIDES = Map.of(
            "modular_bow", LootCategory.BOW.getName(),
            "modular_crossbow", LootCategory.CROSSBOW.getName(),
            "modular_shield", LootCategory.SHIELD.getName(),
            "modular_shield_blocking", LootCategory.SHIELD.getName()
    );

    private TetraCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
