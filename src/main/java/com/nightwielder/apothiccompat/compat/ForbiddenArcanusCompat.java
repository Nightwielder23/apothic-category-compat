package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Forbidden Arcanus weapons. The draco_arcanus set plus mystical_dagger are explicit, and material
// gavels share a _blacksmith_gavel suffix. Trinkets, seeds, orbs, and amulets fall through.
public final class ForbiddenArcanusCompat {
    private static final String NAMESPACE = "forbidden_arcanus";

    private static final Set<String> SWORD_PATHS = Set.of(
            "mystical_dagger", "draco_arcanus_scepter", "draco_arcanus_sword");

    private static final Set<String> HEAVY_PATHS = Set.of("draco_arcanus_axe");

    private static final String[] SWORD_SUFFIXES = {"_blacksmith_gavel"};

    private ForbiddenArcanusCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, ForbiddenArcanusCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON.getName();
        if (SWORD_PATHS.contains(path)) return LootCategory.SWORD.getName();
        for (String s : SWORD_SUFFIXES) if (path.endsWith(s)) return LootCategory.SWORD.getName();
        return null;
    }
}
