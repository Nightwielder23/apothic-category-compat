package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Celestisynth weapons use one-off legendary names with no shared suffix, so they're matched by exact
// name. Most extend SkilledSwordItem so the builtin SwordItem match would catch them anyway, but
// poltergeist (an axe under the 8.0 HEAVY threshold) and rainfall_serenity (a bow) need explicit
// overrides to land right.
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
