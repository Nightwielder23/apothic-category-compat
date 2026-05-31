package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TF's normal gear extends vanilla item classes so UniversalCompat already handles it. These overrides
// cover the four scepters, mazebreaker_pickaxe, and ice_bomb.
public final class TwilightForestCompat {
    private static final String NAMESPACE = "twilightforest";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    // magic-caster scepters. they extend plain Item so class inference can't place them. send FG&A's
    // staffs category when present, otherwise SWORD. resolved at send time so the deferral sees the
    // final mod list, mirroring DungeonsAndCombatCompat.
    private static final List<String> SCEPTERS = List.of(
            "lifedrain_scepter",
            "twilight_scepter",
            "zombie_scepter",
            "fortification_scepter"
    );

    static {
        // block_and_chain and cube_of_annihilation stay unmapped. their projectile entity deals the
        // damage, not a melee swing, so melee affixes can't proc, and bare Item falls back to NONE.
        OVERRIDES.put("ice_bomb", LootCategory.NONE.getName());
        OVERRIDES.put("mazebreaker_pickaxe", LootCategory.PICKAXE.getName());
    }

    private TwilightForestCompat() {}

    public static void send() {
        Map<String, String> overrides = new LinkedHashMap<>(OVERRIDES);
        String scepterCategory = FallenGemsCompat.hasStaffsCategory()
                ? FallenGemsCompat.STAFFS_CATEGORY
                : LootCategory.SWORD.getName();
        for (String scepter : SCEPTERS) {
            overrides.put(scepter, scepterCategory);
        }
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.SILENT);
    }
}
