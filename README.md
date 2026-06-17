# Apothic Category Compat

A small server-side NeoForge 1.21.1 mod that fills in Apotheosis loot categories for a few modded items that Apotheosis 8.x does not place on its own. No mixins, no patching.

## Version differences

This is the NeoForge release for Minecraft 1.21.1 on Apotheosis 8.x. Apothic Category Compat works differently across Minecraft versions because Apotheosis itself does:

- **1.21.1 NeoForge: data map driven, with conditional entries that gate staff and scythe routing on Apothic Compats and Malum.**
- 1.20.1 Forge: a universal attack speed and damage rule, per mod compatibility modules, and a name based classifier.
- 1.19.2 Forge: the same approach as 1.20.1 with a smaller mod list.

See the other branches on GitHub for those releases.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. On 8.x it already categorizes most gear by itself: any melee weapon with attack damage becomes a melee weapon, bows and crossbows are read from the item class, tridents and shields from their class and behavior, and armor by slot. The items it still misses are ranged and thrown weapons that are not `BowItem` or `CrossbowItem`: gun style items and projectile throwers built on a plain `Item` or `ProjectileWeaponItem`.

Apothic Category Compat adds those overrides as a NeoForge data map (`data/apotheosis/data_maps/item/loot_category_overrides.json`). The `apotheosis` namespace in that path is Apotheosis's own, because Apotheosis owns the data map type; contributors write into it rather than registering their own. Apotheosis reads the file the same way it reads its own, so the categories apply with no extra code. To add your own overrides, drop a data map at the same path into a datapack.

## Items overridden

- Alex's Caves: Raygun and Dreadbow as bows
- Alex's Mobs: Hemolymph Blaster and Blood Sprayer as bows
- Born in Chaos: Pumpkin Pistol as a bow
- L_Ender's Cataclysm: Cursed Bow, Wrath of the Desert, the Void and Wither Assault Shoulder Weapons, and the Laser Gatling as bows
- The Undergarden: Slingshot as a bow
- Twilight Forest: Block and Chain and Cube of Annihilation as bows

Apotheosis has one melee category on 8.x, so "bow" is the right home for any ranged weapon, crossbows included. The Block and Chain and Cube of Annihilation deal their damage through a thrown projectile entity rather than an attack damage attribute, so Apotheosis leaves them uncategorized; bow fits because its affixes hook projectile hits. Everything else from these mods, the melee weapons, bows, shields, and armor, is already categorized by Apotheosis.

## Lands of Icaria

The seven bidents (chert, chalkos, kassiteros, orichalcum, vanadiumsteel, sideros, molybdenumsteel) become tridents. A bident throws like a trident but extends a plain tiered item, so Apotheosis reads it as a melee weapon on its own; trident is the closer home for a thrown weapon, and its affixes hook the throw.

The bident overrides load unconditionally. Icaria's scythes route to the Apothic Compats scythe category and load only under the staff routing conditions below.

## Weapon pickaxes

L_Ender's Cataclysm's Void Forge and Infernal Forge and Forbidden Arcanus's blacksmith gavels extend the pickaxe class. Apotheosis sorts pickaxe-class items into its breaker (mining) category before melee, so these weapons would roll mining gem bonuses and mining affixes. The override moves them to melee. The default list is the `apothic_category_compat:weapon_pickaxes` item tag (Void Forge, Infernal Forge, and the wooden through reinforced deorum gavels); add ids to that tag from a datapack to cover more dual-purpose pickaxes. The `weapon_pickaxes_as_melee` flag in `apothic_category_compat-common.toml` (default true) controls this; set it false to leave these items in Apotheosis's native breaker category. A change to the flag applies on a vanilla `/reload`, since the override is a data map condition; `/acc reload` only touches the affix blacklist.

## Staff routing

Needs Apothic Compats (by ianm1647) and Malum, both loaded. Apothic Compats registers a `staff` and a `scythe` loot category, but only when Malum is present, and its staff predicate matches Malum's own staff class alone. Staffs and scepters from other mods fall into no category, so they cannot socket gems or roll affixes. With both mods loaded, this routes staffs, scepters, and wands from Iron's Spellbooks, Twilight Forest, Ice and Fire, Ice and Fire Spellbooks, Forbidden Arcanus, Mahoutsukai, Deeper Darker, the Aether, Alex's Caves, Born in Chaos, and Traveloptics into `apothic_compats:staff`. Lands of Icaria's scythes go to `apothic_compats:scythe`.

Every routed entry carries a load condition for both `apothic_compats` and `malum`. Without either mod the entry is skipped and the rest of the file still loads. Staffs that already carry attack damage, and so already read as melee weapons, are left alone.

## What it does not do (changed from 1.20.1)

The 1.20.1 build read every item's attack speed at load time and split melee weapons into `sword` or `heavy_weapon`. Apotheosis 8.x removed that split (there is one `melee_weapon` category now) and replaced the old override channel with the data map. So this version drops the universal speed rule, its name based classifier, and the per item and per tag TOML overrides, keeping the dual purpose pickaxe routing as the `weapon_pickaxes_as_melee` toggle. Apotheosis 8.x's own melee detection covers what the universal rule used to.

## Affix blacklist

Stops specific affixes from rolling on newly generated gear (loot drops, reforging, trades, and gem application) without editing datapacks. List the affix ids in the `affix_blacklist` array in `config/apothic_category_compat-common.toml`:

```toml
affix_blacklist = ["apotheosis:attribute/sword/vampiric"]
```

To find ids: hover an affixed item with JEI open, or browse the affix files under `data/<namespace>/affixes/` inside a mod's jar (Apotheosis's own affixes live in `data/apotheosis/affixes/`).

Notes:

- This blocks future rolls only. Items that already carry a blacklisted affix keep it.
- Apotheosis's own datapack affix overrides still take precedence.
- The blacklist reapplies on server start and after `/reload`. Edit the list and run `/acc reload` to apply it without a restart.

## Reload command

`/apothiccategorycompat reload` or `/acc reload` (op level 2) rereads the affix blacklist and reapplies it. The data map is loaded by the game, so it follows a normal datapack `/reload`, not this command.

## Requirements

Minecraft 1.21.1, NeoForge 21.1.x, Apotheosis 8.5 or newer (8.x). Apotheosis is required.

## Installation

Drop the jar in `mods/` on the server. The data map and affix blacklist both run server-side, and Apotheosis syncs the categories to clients on its own, so the jar is not needed on the client.

## License

MIT, Copyright 2026 Nightwielder23. https://github.com/Nightwielder23/apothic-compat/blob/main/LICENSE

## Source

https://github.com/Nightwielder23/apothic-compat

## Author

Nightwielder23, https://github.com/Nightwielder23
