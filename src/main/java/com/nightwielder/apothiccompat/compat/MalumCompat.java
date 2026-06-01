package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// Malum's scythes extend Lodestone combat classes the fallback reads as swords, so they get explicit
// heavy_weapon overrides. Tyrving and sundering_anchor stay sword. The AbstractStaffItem staves are
// left uncovered on purpose.
public final class MalumCompat {
    private static final String NAMESPACE = "malum";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("crude_scythe", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("soul_stained_steel_scythe", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("edge_of_deliverance", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("weight_of_worlds", LootCategory.HEAVY_WEAPON.getName());
        OVERRIDES.put("tyrving", LootCategory.SWORD.getName());
        OVERRIDES.put("sundering_anchor", LootCategory.SWORD.getName());
    }

    private MalumCompat() {}

    public static void send() {
        // items missing from the installed Malum version (e.g. sundering_anchor on older builds) skip
        // quietly with SILENT instead of hitting the RegistryLookup warn.
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
