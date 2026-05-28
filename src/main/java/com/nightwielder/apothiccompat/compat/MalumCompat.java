package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Malum's scythes extend Lodestone combat classes that UniversalCompat would read
 * as swords, so they get explicit heavy_weapon overrides and defer to FG&A's staffs
 * category when it is registered. Tyrving and sundering_anchor stay sword.
 * Malum's AbstractStaffItem staves and scepters are intentionally left uncovered.
 */
public final class MalumCompat {
    private static final String NAMESPACE = "malum";
    private static final Map<String, LootCategory> OVERRIDES = new LinkedHashMap<>();

    static {
        OVERRIDES.put("crude_scythe", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("soul_stained_steel_scythe", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("edge_of_deliverance", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("weight_of_worlds", LootCategory.HEAVY_WEAPON);
        OVERRIDES.put("tyrving", LootCategory.SWORD);
        OVERRIDES.put("sundering_anchor", LootCategory.SWORD);
    }

    private MalumCompat() {}

    public static void send() {
        // Every heavy_weapon entry here is a scythe; route those to FG&A's runtime
        // staffs category when both FG&A and Iron's Spellbooks are present, otherwise
        // leave them as heavy_weapon. Sword entries are never deferred.
        boolean deferScythes = FallenGemsCompat.hasStaffsCategory();
        Map<String, String> resolved = new LinkedHashMap<>();
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            LootCategory cat = e.getValue();
            boolean defer = deferScythes && cat == LootCategory.HEAVY_WEAPON;
            resolved.put(e.getKey(), defer ? FallenGemsCompat.STAFFS_CATEGORY : cat.getName());
        }
        // Items absent from the installed Malum version (e.g. sundering_anchor on
        // 1.6.7) skip quietly via SILENT, avoiding the RegistryLookup warn log.
        CompatImc.sendOverrides(NAMESPACE, resolved, CompatImc.SkipMode.SILENT);
    }
}
