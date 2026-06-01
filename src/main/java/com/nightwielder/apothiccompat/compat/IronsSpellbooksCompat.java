package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Iron's Spellbooks is mostly magic items (scrolls, spellbooks, spell-triggering staves) that should
// not roll gems or affixes. Only the straight-melee weapons get categorized.
public final class IronsSpellbooksCompat {
    private static final String NAMESPACE = "irons_spellbooks";

    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("amethyst_rapier", LootCategory.SWORD.getName()),
            Map.entry("boreal_blade", LootCategory.SWORD.getName()),
            Map.entry("claymore", LootCategory.SWORD.getName()),
            Map.entry("decrepit_scythe", LootCategory.SWORD.getName()),
            Map.entry("dreadsword", LootCategory.SWORD.getName()),
            Map.entry("fiery_dagger", LootCategory.SWORD.getName()),
            Map.entry("firebrand", LootCategory.SWORD.getName()),
            Map.entry("hellrazor", LootCategory.SWORD.getName()),
            Map.entry("keeper_flamberge", LootCategory.SWORD.getName()),
            Map.entry("legionnaire_flamberge", LootCategory.SWORD.getName()),
            Map.entry("magehunter", LootCategory.SWORD.getName()),
            Map.entry("misery", LootCategory.SWORD.getName()),
            Map.entry("obsidian_katana", LootCategory.SWORD.getName()),
            Map.entry("spellbreaker", LootCategory.SWORD.getName()),
            Map.entry("truthseeker", LootCategory.SWORD.getName()),
            Map.entry("twilight_gale", LootCategory.SWORD.getName()),
            Map.entry("autoloader_crossbow", LootCategory.CROSSBOW.getName())
    );

    private IronsSpellbooksCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
