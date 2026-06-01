package com.nightwielder.apothiccompat.compat;

import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.loot.LootCategory;
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

// Dungeons and Combat adds a large, growing set of weapons and armor. Categorize by item class plus a
// name-based override for weapons whose class inference is wrong (SwordItem-subclass polearms that
// should roll heavy-weapon affixes).
public final class DungeonsAndCombatCompat {
    private static final String NAMESPACE = "dungeons_and_combat";

    private static final String[] HEAVY_SUFFIXES = {
            "_halberd", "_glaive", "_whirlwind", "_hammer", "_maul",
            "_greataxe", "_greathammer"
    };

    private DungeonsAndCombatCompat() {}

    public static void send() {
        for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
            if (!NAMESPACE.equals(id.getNamespace())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            LootCategory cat = categorize(id.getPath(), item);
            if (cat == null) continue;
            CompatImc.send(item, cat.getName());
        }
    }

    private static LootCategory categorize(String path, Item item) {
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
            return switch (armor.getSlot()) {
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
