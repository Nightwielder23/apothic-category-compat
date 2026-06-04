# Apothic Compat

A small server side 1.19.2 Forge mod that fills in Apotheosis loot category assignments for weapon/armor mods that don't set them. Uses Apotheosis's own IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. Apothic Compat sends the right categories at load time over Apotheosis's IMC override API.

The core rule is universal: every registered item is categorized by what it actually is, not by a hardcoded list. Any item that deals melee attack damage is split into `sword` or `heavy_weapon` by its attack speed, using the same thresholds Obscure API shows in its weapon tooltips: a weapon at or below 1.0 attack speed reads as heavy, anything faster reads as a sword, and a fast weapon that still hits very hard (10.0 or more effective damage) is bumped to heavy. This works for any mod's weapons no matter which Java class they extend, including plain `Item`, `TieredItem`, `DiggerItem`, and modular subclasses. Bows, crossbows, tridents, pickaxes, shovels, shields, and armor are read straight from the vanilla class hierarchy.

A second pass runs at the end of mod loading, so weapons whose attack stats are finalized during deferred setup (Mowzie's Mobs, for one) get recategorized once everything is settled, correcting anything that read stale on the first scan.

## Per mod modules

The universal rule covers almost everything on its own. A few mods register ranged weapons, shields, utility items, or melee weapons under custom classes that carry no usable hierarchy or readable attack stats, so those get small explicit overrides. A module only runs when both Apotheosis and the target mod are loaded.

- **L'Ender's Cataclysm**: cursed bow and wrath of the desert as bows, the assault shoulder weapons and laser gatling as crossbows, and bulwark of the flame as a shield (all plain `Item` or `ProjectileWeaponItem`; the black steel targe is a real `ShieldItem` so the universal rule covers it)
- **Tetra**: modular bow, crossbow and shield (all extend `ModularItem`, not the vanilla ranged or shield classes); modular melee builds go through the universal rule by attack speed
- **Meet Your Fight**: the Guns Without Roses compat shotgun and rifle as crossbows
- **Epic Fight**: greatswords as heavy weapons, the longswords, tachis, daggers, spears, glove, and uchigatana as swords. Epic Fight keeps each weapon's real combat power in its own attribute system and leaves the vanilla attributes at tier defaults, so the universal rule can't place these on its own; the module categorizes them by Epic Fight's own weapon type instead
- **Epic Samurai**: Shuriken as sword (thrown plain `Item` with no attack damage attribute)
- **Aquamirae**: Poisoned Chakra and Maze Rose as swords (extend `TieredItem` directly, so the universal rule won't class them)
- **Forbidden and Arcanus**: Draco Arcanus Scepter as sword (plain `Item` with no attack damage attribute)
- **Alex's Mobs**: Blood Sprayer as bow
- **Born in Chaos**: Trident Hayfork as a heavy weapon and the two Pumpkin Pistols as crossbows (all plain `Item`; the pistols are GeoItem guns that fire projectiles)
- **Celestisynth**: Poltergeist pinned to heavy weapon (a `SkilledAxeItem`; the override sets it heavy rather than letting the speed rule decide)
- **The Undergarden**: slingshot as bow (extends `ProjectileWeaponItem`)
- **Twilight Forest**: the lifedrain/fortification/twilight/zombie scepters as swords (plain `Item` with no attack damage, so the universal rule can't place them but they should still roll melee affixes), Ice Bomb as none
- **Everything else** (including all `ShieldItem` shields, e.g. Dread Steel, Spartan Shields, Epic Knights): categorized by the universal rule above

## Categorization settings

Two toggles in `apothic_compat-common.toml` adjust how the universal rule categorizes items. They apply during mod load, so edit the file and restart the server to change them.

- `name_based_heavy_override` (default `false`): when enabled, any item whose registry id contains a heavy weapon name (greatsword, claymore, zweihander, warhammer, halberd, bardiche, glaive, battleaxe, greataxe, lance, pike, maul, naginata, odachi, flamberge, scythe, and the like) is categorized as `heavy_weapon` regardless of its attack speed and damage. Leave it off to categorize purely by speed and damage.
- `weapon_pickaxes_as_heavy` (default `true`): the dual-purpose pickaxe list holds combat tools that extend `PickaxeItem` but are swung as weapons. When enabled they categorize as `heavy_weapon` instead of `pickaxe`. The list covers L'Ender's Cataclysm's Void Forge and Infernal Forge and Forbidden and Arcanus's Blacksmith Gavels (every material tier). Disable it for plain `PickaxeItem` behavior.

## Config

A config file shows up at `config/apothic_compat-common.toml` on first launch. Per item and per tag overrides go there:

```toml
[item_overrides]
"ruins:greatsword" = "heavy_weapon"

[tag_overrides]
"simplyswords:greathammers" = "heavy_weapon"
```

Valid category names: `sword`, `heavy_weapon`, `trident`, `bow`, `crossbow`, `shield`, `helmet`, `chestplate`, `leggings`, `boots`, `pickaxe`, `shovel`, `none`.

## Affix blacklist

Stops specific affixes from rolling on newly generated gear (loot drops, reforging, trades, and gem application) without editing datapacks. List the affix ids in the `affix_blacklist` array in `apothic_compat-common.toml`:

```toml
affix_blacklist = ["apotheosis:sword/attribute/vampiric", "apotheosis:heavy_weapon/attribute/berserking"]
```

Affix ids are paths matching the JSON file location, e.g. `data/apotheosis/affixes/sword/attribute/vampiric.json` corresponds to the id `apotheosis:sword/attribute/vampiric`. To find ids: hover an affixed item with JEI open, or browse the affix files under `data/<namespace>/affixes/` inside a mod's jar (Apotheosis's own affixes live in `data/apotheosis/affixes/`).

Notes:

- This blocks future rolls only. Items that already carry a blacklisted affix keep it.
- Apotheosis's own datapack affix overrides still take precedence.
- The blacklist reapplies automatically on server start and after `/reload`. Edit the list and run `/ac reload` (or `/apothiccompat reload`) to apply it without a restart.

## Reload command

`/apothiccompat reload` or `/ac reload` (op level 2) rereads the config and reapplies it without a restart, every time it's run.

## Requirements

Minecraft 1.19.2, Forge 43.x, Apotheosis 6.5.2. Everything else is optional.

## Installation

Drop the jar in `mods/`. Install on both client and server for correct affix attribute display and gem socket bonuses. Server only works for gameplay (affix stats apply correctly during combat), but client tooltips and gem stats display based on the client's category resolution, which needs the mod installed locally.

## License

MIT, Copyright 2026 Nightwielder23. https://github.com/Nightwielder23/apothic-compat/blob/main/LICENSE

## Source

https://github.com/Nightwielder23/apothic-compat

## Author

Nightwielder23, https://github.com/Nightwielder23
