# Apothic Compat

A small server-side NeoForge 1.21.1 mod that fills in Apotheosis loot categories for a few modded items that Apotheosis 8.x does not place on its own. No mixins, no patching.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. On 8.x it already categorizes most gear by itself: any melee weapon with attack damage becomes a melee weapon, bows and crossbows are read from the item class, tridents and shields from their class and behavior, and armor by slot. The items it still misses are ranged and thrown weapons that are not `BowItem` or `CrossbowItem`: gun style items and projectile throwers built on a plain `Item` or `ProjectileWeaponItem`.

Apothic Compat adds those overrides as a NeoForge data map (`data/apotheosis/data_maps/item/loot_category_overrides.json`). The `apotheosis` namespace in that path is Apotheosis's own, because Apotheosis owns the data map type; contributors write into it rather than registering their own. Apotheosis reads the file the same way it reads its own, so the categories apply with no extra code. To add your own overrides, drop a data map at the same path into a datapack.

## Items overridden

- Alex's Caves: Raygun and Dreadbow as bows
- Alex's Mobs: Hemolymph Blaster and Blood Sprayer as bows
- Born in Chaos: Pumpkin Pistol as a bow
- L_Ender's Cataclysm: Cursed Bow, Wrath of the Desert, the Void and Wither Assault Shoulder Weapons, and the Laser Gatling as bows
- The Undergarden: Slingshot as a bow
- Twilight Forest: Block and Chain and Cube of Annihilation as bows

Apotheosis has one melee category on 8.x, so "bow" is the right home for any ranged weapon, crossbows included. The Block and Chain and Cube of Annihilation deal their damage through a thrown projectile entity rather than an attack damage attribute, so Apotheosis leaves them uncategorized; bow fits because its affixes hook projectile hits. Everything else from these mods, the melee weapons, bows, shields, and armor, is already categorized by Apotheosis.

## What it does not do (changed from 1.20.1)

The 1.20.1 build read every item's attack speed at load time and split melee weapons into `sword` or `heavy_weapon`. Apotheosis 8.x removed that split (there is one `melee_weapon` category now) and replaced the old override channel with the data map. So this version drops the universal speed rule, the per item and per tag TOML overrides, and the categorization toggles. Apotheosis 8.x's own melee detection covers what the universal rule used to.

## Affix blacklist

Stops specific affixes from rolling on newly generated gear (loot drops, reforging, trades, and gem application) without editing datapacks. List the affix ids in the `affix_blacklist` array in `config/apothic_compat-common.toml`:

```toml
affix_blacklist = ["apotheosis:attribute/sword/vampiric"]
```

To find ids: hover an affixed item with JEI open, or browse the affix files under `data/<namespace>/affixes/` inside a mod's jar (Apotheosis's own affixes live in `data/apotheosis/affixes/`).

Notes:

- This blocks future rolls only. Items that already carry a blacklisted affix keep it.
- Apotheosis's own datapack affix overrides still take precedence.
- The blacklist reapplies on server start and after `/reload`. Edit the list and run `/ac reload` to apply it without a restart.

## Reload command

`/apothiccompat reload` or `/ac reload` (op level 2) rereads the affix blacklist and reapplies it. The data map is loaded by the game, so it follows a normal datapack `/reload`, not this command.

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
