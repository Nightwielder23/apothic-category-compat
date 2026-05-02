package com.nightwielder.apothiccompat.compat;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Iron's Spellbooks contains mostly magic items (scrolls, spellbooks, staves that
 * trigger spells) that shouldn't be categorized; gem/affix rolls don't make sense
 * for them. We only categorize the handful of straight-melee weapons.
 */
public final class IronsSpellbooksCompat {
    private static final String NAMESPACE = "irons_spellbooks";
    private static final String IMC_METHOD = "loot_category_override";

    private static final Map<String, LootCategory> OVERRIDES = Map.ofEntries(
            Map.entry("amethyst_rapier", LootCategory.SWORD),
            Map.entry("boreal_blade", LootCategory.SWORD),
            Map.entry("claymore", LootCategory.SWORD),
            Map.entry("decrepit_scythe", LootCategory.SWORD),
            Map.entry("dreadsword", LootCategory.SWORD),
            Map.entry("fiery_dagger", LootCategory.SWORD),
            Map.entry("firebrand", LootCategory.SWORD),
            Map.entry("hellrazor", LootCategory.SWORD),
            Map.entry("keeper_flamberge", LootCategory.SWORD),
            Map.entry("legionnaire_flamberge", LootCategory.SWORD),
            Map.entry("magehunter", LootCategory.SWORD),
            Map.entry("misery", LootCategory.SWORD),
            Map.entry("obsidian_katana", LootCategory.SWORD),
            Map.entry("spellbreaker", LootCategory.SWORD),
            Map.entry("truthseeker", LootCategory.SWORD),
            Map.entry("twilight_gale", LootCategory.SWORD),
            Map.entry("autoloader_crossbow", LootCategory.CROSSBOW)
    );

    private IronsSpellbooksCompat() {}

    public static void send() {
        // FG&A registers Iron's staffs under its own Staffs category. Skip the whole
        // module when it's present so our IMC overrides don't fight that registration.
        if (FallenGemsCompat.isLoaded()) return;
        for (var e : OVERRIDES.entrySet()) {
            ResourceLocation id = new ResourceLocation(NAMESPACE, e.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = e.getValue().getName();
            InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, name));
        }
    }
}
