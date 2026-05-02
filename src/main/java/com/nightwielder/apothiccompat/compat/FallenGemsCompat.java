package com.nightwielder.apothiccompat.compat;

import net.minecraftforge.fml.ModList;

/**
 * Helper for detecting Fallen Gems and Affixes. FG&A registers its own Staffs and
 * Celestial Melee/Ranged categories through the Apotheosis API, so other modules
 * use this check to skip overrides that would conflict with those registrations.
 */
public final class FallenGemsCompat {
    private static final String MOD_ID = "fallen_gems_affixes";

    private static Boolean cached;

    private FallenGemsCompat() {}

    public static boolean isLoaded() {
        if (cached == null) {
            cached = ModList.get().isLoaded(MOD_ID);
        }
        return cached;
    }
}
