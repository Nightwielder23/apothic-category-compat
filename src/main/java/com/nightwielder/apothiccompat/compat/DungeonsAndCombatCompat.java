package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Dungeons and Combat's weapons extend SwordItem/AxeItem and its armor and shields extend the vanilla
// classes, so UniversalCompat handles all of them by speed or class. The only gap is the magic caster
// scepters: PyromancerScepterItem extends SwordItem and the other three extend plain Item, but the user
// wants all four under Fallen Gems & Affixes' staffs category when it's loaded (sword otherwise), which
// the automatic rule can't express, so they keep explicit overrides.
public final class DungeonsAndCombatCompat {
    private static final String NAMESPACE = "dungeons_and_combat";

    private static final List<String> SCEPTERS = List.of(
            "pyromancer_scepter",
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
