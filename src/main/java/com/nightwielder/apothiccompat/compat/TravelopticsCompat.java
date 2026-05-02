package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

/**
 * T.O Magic 'n Extras boss weapons each ship with four upgrade tiers
 * (base + level_one/two/three). The tiers share class hierarchies that
 * don't extend vanilla weapon classes cleanly, so each line is listed
 * here and every level gets the same category.
 *
 * Staff lines are gated on FG&A: when Fallen Gems and Affixes is present
 * it registers its own Staffs category and we leave staffs alone, otherwise
 * staffs fall back to SWORD so they still pick up melee affixes.
 */
public final class TravelopticsCompat {
    private static final String NAMESPACE = "traveloptics";
    private static final String IMC_METHOD = "loot_category_override";

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
        registerAll(SWORD_BASES, LootCategory.SWORD);
        registerAll(HEAVY_BASES, LootCategory.HEAVY_WEAPON);
        registerAll(TRIDENT_BASES, LootCategory.TRIDENT);

        // FG&A owns the Staffs category; only categorize staffs ourselves when it's absent.
        if (!FallenGemsCompat.isLoaded()) {
            registerAll(STAFF_BASES, LootCategory.SWORD);
        }
    }

    private static void registerAll(List<String> bases, LootCategory category) {
        String name = category.getName();
        for (String base : bases) {
            for (String suffix : LEVEL_SUFFIXES) {
                ResourceLocation id = new ResourceLocation(NAMESPACE, base + suffix);
                Item item = ForgeRegistries.ITEMS.getValue(id);
                if (item == null) continue;
                InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
            }
        }
    }
}
