package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Iron's Spellbooks staffs carry a slow melee attack, so UniversalCompat's speed split files them as
// heavy_weapon before FG&A can claim them. When FG&A's staffs category is available (it registers only
// alongside Iron's Spellbooks), send the staffs there explicitly so this override lands after the universal
// pass and wins. Without that category there is no better fit, so leave them to UniversalCompat. The melee
// weapons (rapiers, claymores, flamberges) are SwordItem subclasses and route by speed on their own.
public final class IronsSpellbooksCompat {
    private static final String NAMESPACE = "irons_spellbooks";

    private static final List<String> STAFFS = List.of(
            "blood_staff",
            "improved_blood_staff",
            "pyrium_staff",
            "graybeard_staff",
            "ice_staff",
            "staff_of_the_nines"
    );

    private IronsSpellbooksCompat() {}

    public static void send() {
        if (!FallenGemsCompat.hasStaffsCategory()) {
            return;
        }
        Map<String, String> overrides = new LinkedHashMap<>();
        for (String staff : STAFFS) {
            overrides.put(staff, FallenGemsCompat.STAFFS_CATEGORY);
        }
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.SILENT);
    }
}
