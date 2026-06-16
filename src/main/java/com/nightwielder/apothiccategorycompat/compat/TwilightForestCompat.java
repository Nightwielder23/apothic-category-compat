package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Normal gear extends vanilla classes, so UniversalCompat handles it. The four scepters are plain Item, so
// route them to FG&A's staffs category when present, sword otherwise. ice_bomb is a thrown utility, none.
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
        // block_and_chain is a slow, heavy single shot on a chain that returns, so crossbow fits its feel
        // better than bow. ice_bomb is a thrown utility, none. cube_of_annihilation breaks blocks and also
        // deals damage, so it routes through weapon_pickaxes_as_heavy in UniversalCompat instead.
        OVERRIDES.put("block_and_chain", LootCategory.CROSSBOW.getName());
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
