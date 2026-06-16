package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// TF's normal gear extends vanilla item classes so UniversalCompat already handles it (swords and axes by
// attack speed, mazebreaker_pickaxe as a pickaxe). The four scepters extend plain Item with no attack
// damage attribute, so the speed path never categorizes them; these explicit overrides map them to
// sword so they can roll melee affixes. The ice_bomb is a thrown utility and stays none.
public final class TwilightForestCompat {
    private static final String NAMESPACE = "twilightforest";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("lifedrain_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("fortification_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("twilight_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("zombie_scepter", LootCategory.SWORD.getName());
        // block_and_chain is a slow, heavy single shot on a chain that returns, so crossbow fits its feel
        // better than bow. ice_bomb is a thrown utility, none. cube_of_annihilation breaks blocks and also
        // deals damage, so it routes through weapon_pickaxes_as_heavy in UniversalCompat instead.
        OVERRIDES.put("block_and_chain", LootCategory.CROSSBOW.getName());
        OVERRIDES.put("ice_bomb", LootCategory.NONE.getName());
    }

    private TwilightForestCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
