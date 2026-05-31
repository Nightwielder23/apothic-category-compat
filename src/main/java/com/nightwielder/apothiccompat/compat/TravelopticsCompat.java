package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// T.O Magic 'n Extras boss weapons each have four upgrade tiers and don't extend vanilla classes
// cleanly, so every tier gets listed with the same category. Staffs defer to FG&A's Staffs category when
// it's loaded and fall back to SWORD otherwise.
public final class TravelopticsCompat {
    private static final String NAMESPACE = "traveloptics";

    private static final List<String> SWORD_BASES = List.of(
            "flames_of_eldritch",
            "cursed_wraithblade",
            "the_obliterator",
            "abyssal_tidecaller",
            "voidstrike_reaper",
            "mechanized_wraithblade",
            "charged_sands",
            "thorns_of_oblivion",
            "stellothorn",
            "scourge_of_the_sands",
            "harbingers_wrath",
            "infernal_devastator",
            "gauntlet_of_extinction"
    );

    private static final List<String> HEAVY_BASES = List.of(
            "galenic_polarizer"
    );

    private static final List<String> TRIDENT_BASES = List.of(
            "trident_of_the_eternal_maelstrom"
    );

    private static final List<String> STAFF_BASES = List.of(
            "titanlord_scepter",
            "titanlord_scepter_retro",
            "titanlord_scepter_tectonic",
            "wand_of_final_light",
            "staff_of_the_storm_empress"
    );

    private static final String[] LEVEL_SUFFIXES = {
            "", "_level_one", "_level_two", "_level_three"
    };

    private TravelopticsCompat() {}

    public static void send() {
        Map<String, String> overrides = new LinkedHashMap<>();
        addBases(overrides, SWORD_BASES, LootCategory.SWORD.getName());
        addBases(overrides, HEAVY_BASES, LootCategory.HEAVY_WEAPON.getName());
        addBases(overrides, TRIDENT_BASES, LootCategory.TRIDENT.getName());

        // FG&A owns the Staffs category, so only categorize staffs here when it's absent
        if (!FallenGemsCompat.isLoaded()) {
            addBases(overrides, STAFF_BASES, LootCategory.SWORD.getName());
        }
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.WARN);
    }

    private static void addBases(Map<String, String> overrides, List<String> bases, String name) {
        for (String base : bases) {
            for (String suffix : LEVEL_SUFFIXES) {
                overrides.put(base + suffix, name);
            }
        }
    }
}
