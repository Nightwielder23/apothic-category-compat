package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Weapons, armor, and shields extend vanilla classes, so UniversalCompat handles them. pyromancer_scepter
// is a SwordItem already placed by name based staff detection or speed. The other three scepters are plain
// Item, so override them to FG&A's staffs category when loaded, sword otherwise.
public final class DungeonsAndCombatCompat {
    private static final String NAMESPACE = "dungeons_and_combat";

    private static final List<String> SCEPTERS = List.of(
            "sanguine_scepter",
            "fairy_scepter",
            "scepter_of_compensation"
    );

    private DungeonsAndCombatCompat() {}

    public static void send() {
        String category = FallenGemsCompat.staffsOr(LootCategory.SWORD.getName());
        Map<String, String> overrides = new LinkedHashMap<>();
        for (String scepter : SCEPTERS) {
            overrides.put(scepter, category);
        }
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.SILENT);
    }
}
