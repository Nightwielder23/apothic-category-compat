package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

/**
 * Celestisynth weapons use one-off legendary names (crescentia, frostbound,
 * keres, etc.) with no shared suffix, so exact-name matching is the only option.
 * Most extend SkilledSwordItem (which extends SwordItem) and would already be
 * picked up by Apotheosis's builtin SwordItem match, but we send explicit
 * overrides so poltergeist (SkilledAxeItem, under UniversalCompat's 8.0 HEAVY
 * threshold) and rainfall_serenity (BowItem) land in the right bucket.
 */
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
        // FG&A registers Celestisynth weapons under its own Celestial Melee/Ranged
        // categories. Skip the whole module when it's present to avoid clashing IMC.
        if (FallenGemsCompat.isLoaded()) return;
        CompatScan.byPath(NAMESPACE, CelestisynthCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        if (BOW_PATHS.contains(path)) return LootCategory.BOW.getName();
        return null;
    }
}
