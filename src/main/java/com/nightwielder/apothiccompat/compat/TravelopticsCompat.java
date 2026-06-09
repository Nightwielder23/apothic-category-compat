package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// The sword shaped boss weapons extend a GeoMagicSword with attack stats, so UniversalCompat splits them by
// speed. The rest need overrides: galenic_polarizer is a GeoMagicSword used as a heavy launcher,
// trident_of_the_eternal_maelstrom is a GeoMagicSpear (not a vanilla TridentItem), and the staffs read as
// heavy under speed while FG&A never claims GeoStaffItem, so route them to FG&A staffs when loaded, else sword.
public final class TravelopticsCompat {
    private static final String NAMESPACE = "traveloptics";

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
        addBases(overrides, HEAVY_BASES, LootCategory.HEAVY_WEAPON.getName());
        addBases(overrides, TRIDENT_BASES, LootCategory.TRIDENT.getName());

        String staffCategory = FallenGemsCompat.staffsOr(LootCategory.SWORD.getName());
        addBases(overrides, STAFF_BASES, staffCategory);

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
