package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.Map;

// Malum's scythes extend Lodestone combat classes the fallback reads as swords, so they get explicit
// heavy_weapon overrides that defer to FG&A's staffs category when it's registered. Tyrving and
// sundering_anchor stay sword, and the AbstractStaffItem staves/scepters are left uncovered on purpose.
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
        // every heavy_weapon entry here is a scythe. those defer to FG&A's staffs category when FG&A and
        // Iron's Spellbooks are both loaded and stay heavy_weapon otherwise. swords never defer.
        boolean deferScythes = FallenGemsCompat.hasStaffsCategory();
        Map<String, String> resolved = new LinkedHashMap<>();
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            LootCategory cat = e.getValue();
            boolean defer = deferScythes && cat == LootCategory.HEAVY_WEAPON;
            resolved.put(e.getKey(), defer ? FallenGemsCompat.STAFFS_CATEGORY : cat.getName());
        }
        // items missing from the installed Malum version (e.g. sundering_anchor on 1.6.7) skip quietly
        // with SILENT instead of hitting the RegistryLookup warn.
        CompatImc.sendOverrides(NAMESPACE, resolved, CompatImc.SkipMode.SILENT);
    }
}
