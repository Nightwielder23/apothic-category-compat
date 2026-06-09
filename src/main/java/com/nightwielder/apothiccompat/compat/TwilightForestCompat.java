package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
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
        // block_and_chain and cube_of_annihilation deal their damage through a projectile entity, not a
        // melee swing, so they go to bow rather than a melee category. Apoth's bow affixes hook projectile
        // entity hits, which is how these items land damage.
        OVERRIDES.put("block_and_chain", LootCategory.BOW.getName());
        OVERRIDES.put("cube_of_annihilation", LootCategory.BOW.getName());
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
