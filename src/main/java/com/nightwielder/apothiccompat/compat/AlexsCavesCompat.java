package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// The melee weapons set attack damage, so UniversalCompat splits them by speed. The gaps: galena_gauntlet
// and the two staves are plain Item, dreadbow extends ProjectileWeaponItem (not BowItem), and raygun is a
// plain Item ranged weapon. The staves defer to FG&A's staffs category when present, sword otherwise.
public final class AlexsCavesCompat {
    private static final String NAMESPACE = "alexscaves";

    private static final List<String> STAVES = List.of("sea_staff", "sugar_staff");

    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("galena_gauntlet", LootCategory.SWORD.getName()),
            Map.entry("dreadbow", LootCategory.BOW.getName()),
            Map.entry("raygun", LootCategory.BOW.getName())
    );

    private AlexsCavesCompat() {}

    public static void send() {
        Map<String, String> overrides = new LinkedHashMap<>(OVERRIDES);
        String staffCategory = FallenGemsCompat.staffsOr(LootCategory.SWORD.getName());
        for (String staff : STAVES) {
            overrides.put(staff, staffCategory);
        }
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.SILENT);
    }
}
