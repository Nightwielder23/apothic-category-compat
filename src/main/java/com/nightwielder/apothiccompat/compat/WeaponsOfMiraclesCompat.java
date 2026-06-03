package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Weapons of Miracles is an Epic Fight addon: its weapons extend Epic Fight's WeaponItem and expose their
// real attack damage and speed through getAttributeModifiers, so UniversalCompat's live read splits them by
// speed. Gesetz extends ShieldItem and the armor extends ArmorItem, so those go through the universal
// class branches too. The only gap is Overly Large Cylindre, a plain Item with no attack damage attribute
// the class checks can't place, so it keeps an explicit shield override.
public final class WeaponsOfMiraclesCompat {
    private static final String NAMESPACE = "wom";

    private static final Map<String, String> OVERRIDES = Map.of(
            "overly_large_cylinder", LootCategory.SHIELD.getName()
    );

    private WeaponsOfMiraclesCompat() {}

    public static void send() {
        CompatImc.sendOverrides(NAMESPACE, OVERRIDES, CompatImc.SkipMode.SILENT);
    }
}
