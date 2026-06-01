package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Celestisynth weapons use one-off legendary names with no shared suffix so exact-name matching is the
// only option. Most extend SkilledSwordItem and Apotheosis would catch them as swords, but explicit
// overrides guarantee poltergeist (a SkilledAxeItem under the 8.0 heavy threshold) and rainfall_serenity
// (a BowItem) land in the right bucket.
public final class CelestisynthCompat {
    private static final String NAMESPACE = "celestisynth";

    private static final Set<String> SWORD_PATHS = Set.of(
            "aquaflora", "breezebreaker", "crescentia", "frostbound",
            "keres", "solaris");

    private static final Set<String> HEAVY_PATHS = Set.of(
            "poltergeist");

    private static final Set<String> BOW_PATHS = Set.of(
            "rainfall_serenity");

    private CelestisynthCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, CelestisynthCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        if (BOW_PATHS.contains(path)) return LootCategory.BOW.getName();
        return null;
    }
}
