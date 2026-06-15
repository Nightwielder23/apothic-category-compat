package com.nightwielder.apothiccategorycompat.compat;

import com.nightwielder.apothiccategorycompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;

import java.util.Map;

// Forbidden and Arcanus melee weapons extend SwordItem/AxeItem and route through the speed split, and the
// blacksmith gavels extend PickaxeItem so UniversalCompat files them as pickaxes. The draco_arcanus_scepter
// is the exception: it extends plain Item with no attack damage attribute, so the speed path never sees it.
// It goes to FG&A's staffs category when present, sword otherwise.
public final class ForbiddenArcanusCompat {
    private static final String NAMESPACE = "forbidden_arcanus";

    private ForbiddenArcanusCompat() {}

    public static void send() {
        String category = FallenGemsCompat.staffsOr(LootCategory.SWORD.getName());
        Map<String, String> overrides = Map.of("draco_arcanus_scepter", category);
        CompatImc.sendOverrides(NAMESPACE, overrides, CompatImc.SkipMode.SILENT);
    }
}
