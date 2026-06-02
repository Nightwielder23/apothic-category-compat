package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// TF's normal gear extends vanilla item classes so UniversalCompat already handles it (swords and axes by
// attack speed, mazebreaker_pickaxe as a pickaxe). The four scepters extend plain Item with no attack
// damage attribute, so the speed path never categorizes them; per the user's request they are mapped to
// sword so they can roll melee affixes. ice_bomb is a thrown utility and stays none.
public final class TwilightForestCompat {
    private static final String NAMESPACE = "twilightforest";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("lifedrain_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("fortification_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("twilight_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("zombie_scepter", LootCategory.SWORD.getName());
        // block_and_chain and cube_of_annihilation stay unmapped. their projectile entity deals the
        // damage, not a melee swing, so melee affixes can't proc, and bare Item falls back to none.
        OVERRIDES.put("ice_bomb", LootCategory.NONE.getName());
    }

    private TwilightForestCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
