package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Enigmatic Legacy combat items. The Voracious Pan (eldritch_pan) extends ShieldItem but swings as a
// melee weapon, and Astral Breaker extends PickaxeItem but is a two-hander, so both would be
// miscategorized by the vanilla-class fallback. Curios, rings, scrolls, and armor fall through.
public final class EnigmaticLegacyCompat {
    private static final String NAMESPACE = "enigmaticlegacy";

    private static final Set<String> SWORD_PATHS = Set.of(
            "eldritch_pan", "etherium_sword");

    private static final Set<String> HEAVY_PATHS = Set.of(
            "forbidden_axe", "astral_breaker", "etherium_scythe",
            "etherium_waraxe");

    private EnigmaticLegacyCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EnigmaticLegacyCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        return null;
    }
}
