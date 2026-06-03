package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatScan;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

// Epic Fight Nightfall (efn) adds a handful of weapons whose ids don't follow a predictable suffix, so
// they're listed explicitly. Like base Epic Fight, efn keeps its real combat stats in Epic Fight's own
// attribute system and leaves the vanilla attributes at tier defaults, so the speed split reads these as
// swords. The heavy ones (the greatsword, Ghiza's Wheel, and the scythe) are pinned here. Anything else
// falls through to UniversalCompat's SWORD class match.
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
