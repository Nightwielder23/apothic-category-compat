package com.nightwielder.apothiccompat.compat;

import net.minecraftforge.fml.ModList;

// Detects Fallen Gems and Affixes. FG&A registers its own Staffs and Celestial Melee/Ranged categories
// through the Apotheosis API, so other modules check here before sending overrides that would clash.
public final class FallenGemsCompat {
    public static final String STAFFS_CATEGORY = "staffs";

    private static final String MOD_ID = "fallen_gems_affixes";

    private static Boolean cached;

    private FallenGemsCompat() {}

    public static boolean isLoaded() {
        if (cached == null) {
            cached = ModList.get().isLoaded(MOD_ID);
        }
        return cached;
    }

    // FG&A registers its "staffs" category only when Iron's Spellbooks is loaded too
    // (StaffLootCategory.<clinit> is gated on irons_spellbooks). sending "staffs" via IMC without
    // that registration NPEs Apotheosis's IMC handler.
    public static boolean hasStaffsCategory() {
        return isLoaded() && ModList.get().isLoaded("irons_spellbooks");
    }
}
