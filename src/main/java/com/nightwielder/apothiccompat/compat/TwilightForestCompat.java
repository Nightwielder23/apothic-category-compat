package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Twilight Forest's standard gear (swords, bows, axes, pickaxes, shields, armor)
 * extends the vanilla item classes, so UniversalCompat already categorizes it.
 * These overrides cover items where class-based inference is wrong: the chain
 * weapon and the four scepters all extend plain Item, and the scepters should
 * still roll sword affixes. ice_bomb and mazebreaker_pickaxe match what the
 * fallback already picks; they are listed to make the intended category explicit
 * rather than to correct it.
 */
public final class TwilightForestCompat {
    private static final String NAMESPACE = "twilightforest";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("block_and_chain", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("ice_bomb", LootCategory.NONE.getName());
        OVERRIDES.put("lifedrain_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("fortification_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("twilight_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("zombie_scepter", LootCategory.SWORD.getName());
        OVERRIDES.put("mazebreaker_pickaxe", LootCategory.PICKAXE.getName());
    }

    private TwilightForestCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
