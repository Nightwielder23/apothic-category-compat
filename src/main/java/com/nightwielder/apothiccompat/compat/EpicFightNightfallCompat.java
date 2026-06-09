package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Nightfall ids don't follow a predictable suffix, so they're listed explicitly. Like base Epic Fight, it
// keeps combat stats in its own attribute system, leaving vanilla attributes at defaults, so the speed
// split reads these as swords. Only the heavy ones (greatsword, Ghiza's Wheel, scythe) are pinned here.
public final class EpicFightNightfallCompat {
    private static final String NAMESPACE = "efn";

    private static final Set<String> HEAVY_PATHS = Set.of(
            "ruinsgreatsword", "thornwheel", "scythe");

    private EpicFightNightfallCompat() {}

    public static void send() {
        CompatScan.byPath(NAMESPACE, EpicFightNightfallCompat::categorize);
    }

    private static String categorize(String path) {
        if (HEAVY_PATHS.contains(path)) {
            return LootCategory.HEAVY_WEAPON.getName();
        }
        return null;
    }
}
