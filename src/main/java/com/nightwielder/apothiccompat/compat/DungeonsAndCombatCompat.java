package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Dungeons and Combat's weapons extend SwordItem/AxeItem and its armor and shields extend the vanilla
// classes, so UniversalCompat handles those by speed or class. pyromancer_scepter is a SwordItem whose id
// names a scepter, so name based staff detection (with FG&A) or the speed rule already places it. The other
// three scepters extend plain Item with no attack damage, so the speed path never reaches them; they keep
// explicit overrides routing to Fallen Gems & Affixes' staffs category when it's loaded, sword otherwise.
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
