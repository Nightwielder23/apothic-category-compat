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
        // block_and_chain and cube_of_annihilation deal their damage through a projectile entity, not a
        // melee swing, so they go to bow rather than a melee category. Apoth's bow affixes hook projectile
        // entity hits, which is how these items land damage.
        OVERRIDES.put("block_and_chain", LootCategory.BOW.getName());
        OVERRIDES.put("cube_of_annihilation", LootCategory.BOW.getName());
        OVERRIDES.put("ice_bomb", LootCategory.NONE.getName());
    }

    private TwilightForestCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
