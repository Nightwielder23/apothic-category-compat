package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

// Dungeons and Combat adds a big, growing set of weapons and armor, so it categorizes by item class
// instead of hardcoding hundreds of ids, with name-based overrides where the class misleads (SwordItem
// polearms that should roll heavy affixes). The class step needs the item so it keeps its own scan
// instead of CompatScan.byPath.
public final class DungeonsAndCombatCompat {
    private static final String NAMESPACE = "dungeons_and_combat";

    private static final String[] HEAVY_SUFFIXES = {
            "_claymore", "_glaive", "_greataxe", "_greathammer", "_greatsword",
            "_halberd", "_hammer", "_maul", "_whirlwind"
    };

    // dragon greatswords have an elemental suffix after _greatsword so the suffix matcher misses them.
    // listed explicitly
    private static final Set<String> HEAVY_PATHS = Set.of(
            "dragon_greatsword_bone",
            "dragon_greatsword_fire",
            "dragon_greatsword_ice",
            "dragon_greatsword_lightning"
    );

    // magic-caster scepters. PyromancerScepterItem extends SwordItem, and the other three extend plain
    // Item, so class inference gets them wrong. send FG&A's staffs category when present, otherwise SWORD.
    // FG&A only auto-detects Iron's StaffItem, so this one has to send the IMC explicitly unlike
    // IronsSpellbooks and Traveloptics which just skip their overrides.
    private static final Set<String> SCEPTERS = Set.of(
            "pyromancer_scepter",
            "sanguine_scepter",
            "fairy_scepter",
            "scepter_of_compensation"
    );

    private DungeonsAndCombatCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            String name = explicitOverride(id.getPath());
            if (name == null) {
                LootCategory cat = categorize(id.getPath(), item);
                if (cat == null) continue;
                name = cat.getName();
            }
            CompatImc.send(item, name);
        }
    }

    private static String explicitOverride(String path) {
        if (SCEPTERS.contains(path)) {
            return FallenGemsCompat.hasStaffsCategory() ? FallenGemsCompat.STAFFS_CATEGORY : LootCategory.SWORD.getName();
        }
        return null;
    }

    private static LootCategory categorize(String path, Item item) {
        if (HEAVY_PATHS.contains(path)) return LootCategory.HEAVY_WEAPON;
        for (String s : HEAVY_SUFFIXES) if (path.endsWith(s)) return LootCategory.HEAVY_WEAPON;
        if (item instanceof SwordItem) return LootCategory.SWORD;
        if (item instanceof AxeItem) {
            return CompatImc.getAttackDamage(item) > CompatImc.HEAVY_WEAPON_THRESHOLD ? LootCategory.HEAVY_WEAPON : LootCategory.SWORD;
        }
        if (item instanceof BowItem) return LootCategory.BOW;
        if (item instanceof CrossbowItem) return LootCategory.CROSSBOW;
        if (item instanceof TridentItem) return LootCategory.HEAVY_WEAPON;
        if (item instanceof ShieldItem) return LootCategory.SHIELD;
        if (item instanceof ArmorItem armor) {
            return switch (armor.getEquipmentSlot()) {
                case HEAD -> LootCategory.HELMET;
                case CHEST -> LootCategory.CHESTPLATE;
                case LEGS -> LootCategory.LEGGINGS;
                case FEET -> LootCategory.BOOTS;
                default -> null;
            };
        }
        return null;
    }
}
