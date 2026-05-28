package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

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
    private static final String IMC_METHOD = "loot_category_override";
    private static final String STAFFS_CATEGORY = "staffs";
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
        for (Map.Entry<String, LootCategory> e : OVERRIDES.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, e.getKey());
            // Items absent from the installed Malum version (e.g. sundering_anchor on
            // 1.6.7) skip quietly; containsKey avoids the RegistryLookup warn log.
            if (!ForgeRegistries.ITEMS.containsKey(id)) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            LootCategory cat = e.getValue();
            String name = (deferScythes && cat == LootCategory.HEAVY_WEAPON) ? STAFFS_CATEGORY : cat.getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
