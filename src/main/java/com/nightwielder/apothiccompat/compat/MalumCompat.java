package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// Malum's scythes extend Lodestone combat classes the fallback reads as swords. They swing at sword
// speed in this version so they map to sword rather than heavy_weapon. Tyrving is a sword too. The
// AbstractStaffItem staves are left uncovered on purpose.
public final class MalumCompat {
    private static final String NAMESPACE = "malum";
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("crude_scythe", LootCategory.SWORD.getName());
        OVERRIDES.put("soul_stained_steel_scythe", LootCategory.SWORD.getName());
        OVERRIDES.put("tyrving", LootCategory.SWORD.getName());
    }

    private MalumCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
