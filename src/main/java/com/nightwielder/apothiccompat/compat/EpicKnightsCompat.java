package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.ApothicCompat;
import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

// UniversalCompat handles Epic Knights' vanilla-class gear. The gaps are shields and the SwordItem-based
// polearms/mauls that should roll heavy affixes, matched by substring on the registry path since the tag
// tree isn't documented.
public final class EpicKnightsCompat {
    private static final String NAMESPACE = "magistuarmory";

    private static final String[] HEAVY_TOKENS = {
            "claymore", "glaive", "halberd", "hammer", "lance",
            "mace", "maul", "pike", "warhammer", "zweihander"
    };

    private EpicKnightsCompat() {}

    public static void send() {
        // own scan instead of CompatScan.byPath, wrapped so one bad item id can't stop the rest
        try {
            for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
                ResourceLocation id = entry.getKey().location();
                if (!NAMESPACE.equals(id.getNamespace())) continue;

                LootCategory cat = categorize(id.getPath());
                if (cat == null) continue;

                CompatImc.send(entry.getValue(), cat.getName());
            }
        } catch (Exception e) {
            ApothicCompat.LOGGER.warn("[EpicKnights] iteration failed; some items may not be categorized", e);
        }
    }

    private static LootCategory categorize(String path) {
        if (path.contains("shield")) return LootCategory.SHIELD;
        for (String token : HEAVY_TOKENS) {
            if (path.contains(token)) return LootCategory.HEAVY_WEAPON;
        }
        return null;
    }
}
