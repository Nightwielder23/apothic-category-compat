package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Twilight Forest's normal gear extends vanilla item classes, so UniversalCompat handles it (swords and
// axes by speed, mazebreaker_pickaxe as a pickaxe through the PickaxeItem branch). The four scepters extend
// plain Item with no attack damage attribute, so the speed path never categorizes them. They go to FG&A's
// staffs category when present (sword otherwise). The ice_bomb is a thrown utility pinned to none.
public final class TwilightForestCompat {
    private static final String NAMESPACE = "twilightforest";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    private static final List<String> SCEPTERS = List.of(
            "lifedrain_scepter",
            "twilight_scepter",
            "zombie_scepter",
            "fortification_scepter"
    );

    static {
        // block_and_chain and cube_of_annihilation stay unmapped. Their projectile entity deals the
        // damage, not a melee swing, so melee affixes can't proc, and bare Item falls back to none.
        OVERRIDES.put("ice_bomb", LootCategory.NONE.getName());
    }

    private TwilightForestCompat() {}

    public static void send() {
        Map<String, String> overrides = new LinkedHashMap<>(OVERRIDES);
        String scepterCategory = FallenGemsCompat.staffsOr(LootCategory.SWORD.getName());
        for (String scepter : SCEPTERS) {
            overrides.put(scepter, scepterCategory);
        }
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.SILENT);
    }
}
