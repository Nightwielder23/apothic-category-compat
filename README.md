# Apothic Compat

A small server side 1.20.1 Forge mod that fills in Apotheosis loot category assignments for weapon/armor mods that don't set them. Uses the Apotheosis IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. Apothic Compat sends the right categories at load time.

The core rule is universal: every registered item is categorized by what it actually is, not by a hardcoded list. Any item that deals melee attack damage is split into `sword` or `heavy_weapon` by its attack speed, using the same thresholds Obscure API shows in its weapon tooltips: a weapon at or below 1.0 attack speed reads as heavy, anything faster reads as a sword, and a fast weapon that still hits very hard (10.0 or more effective damage) is bumped to heavy. The attack speed and damage are read live from the item stack, so combat mods that adjust a weapon's stats at runtime are reflected. This works for any mod's weapons no matter which Java class they extend, including plain `Item`, `TieredItem`, `DiggerItem`, and modular subclasses. Bows, crossbows, tridents, pickaxes, shovels, shields, and armor are read straight from the vanilla class hierarchy.

A second pass runs at the end of mod loading. Some mods finalize a weapon's attack stats during deferred setup that completes after the first scan, so the second pass reruns the same categorization once everything is settled and corrects anything that read stale.

## Per mod modules

The universal rule covers almost everything on its own. A few mods register ranged weapons, shields, scepters, or other items under custom classes that carry no usable hierarchy or attack damage attribute, so those get small explicit overrides. A module only runs when both Apotheosis and the target mod are loaded.

- **L'Ender's Cataclysm**: Cursed Bow and Wrath of the Desert as bows, the assault shoulder weapons and Laser Gatling as crossbows (plain `Item` or `ProjectileWeaponItem`). Every melee weapon and every shield is read by the universal rule; Void Forge and Infernal Forge are `PickaxeItem` combat tools handled by the dual-purpose pickaxe setting
- **Tetra**: modular bow, crossbow, and shield (all extend `ModularItem`, not the vanilla ranged or shield classes); modular melee builds go through the universal rule by attack speed
- **Weapons of Miracles**: Overly Large Cylindre as a shield (plain `Item`). The rest of its Epic Fight weapons expose real attack stats, so the universal rule handles them
- **Aquamirae**: Poisoned Chakra and Maze Rose as swords (extend `TieredItem` with no attack damage attribute)
- **Dungeons and Combat**: the sanguine, fairy, and compensation scepters (plain `Item`) as staffs (Fallen Gems & Affixes) or sword. The Pyromancer Scepter is a `SwordItem`, so the name based staff routing or the speed rule already places it
- **Marium's Soulslike Weaponry**: the custom bows (Galeforce, Kraken Slayer, longbows, bowblades) and crossbows, which extend a Ranged Weapon API class rather than the vanilla bow/crossbow classes
- **Born in Chaos**: Pumpkin Pistol as a crossbow (a plain `Item` gun that fires projectiles). The Trident Hayfork is a `SwordItem` the universal rule already handles
- **Celestisynth**: Poltergeist pinned to heavy (an axe; the override sets it heavy rather than letting the speed rule decide). Defers to Fallen Gems & Affixes when loaded
- **Alex's Mobs**: Blood Sprayer as a bow
- **Alex's Caves**: Galena Gauntlet as a sword and the sea and sugar staves as staffs (Fallen Gems & Affixes) or sword (all plain `Item`), Dreadbow and Raygun as bows
- **Forbidden and Arcanus**: Draco Arcanus Scepter as staffs (Fallen Gems & Affixes) or sword (plain `Item`)
- **Meet Your Fight**: the Guns Without Roses compat guns (Jägershot, Phantasmal Rifle, Dredged Cannonade) as crossbows
- **Epic Fight**: greatswords as heavy weapons, the other weapon types as swords. Epic Fight keeps each weapon's real combat power in its own attribute system and leaves the vanilla attributes at tier defaults, so the speed rule can't place these on its own
- **EpicFight Resurrection / Nightfall**: greatswords, the great tachi, the scythe, and Ghiza's Wheel as heavy weapons
- **T.O Magic 'n Extras**: Galenic Polarizer as a heavy weapon, Trident of the Eternal Maelstrom as a trident, the staffs as Fallen Gems & Affixes staffs or sword. The sword shaped boss weapons go through the universal rule
- **Twilight Forest**: the lifedrain/fortification/twilight/zombie scepters as staffs or sword, Ice Bomb as none (utility item with no attack damage)
- **The Undergarden**: Slingshot as a bow (extends `ProjectileWeaponItem`)
- **Fallen Gems & Affixes**: when present, any item whose registry id names a staff, scepter, or wand routes to its Staffs category, and Celestisynth defers to its Celestial Melee/Ranged categories

## Handled by the universal rule

These mods extend the right vanilla classes or carry real attack stats, so the universal rule categorizes them with no explicit module: Simply Swords, Integrated Simply Swords, Spartan Weaponry, Spartan Shields, Epic Knights, Samurai Dynasty, Dread Steel, Iron's Spellbooks, Mowzie's Mobs, Bosses of Mass Destruction, Deeper and Darker, Knight Quest, Enigmatic Legacy, Malum, RPG Style More Weapons, Farmer's Delight, Dungeons Delight, Cataclysm Weaponry, Armageddon, and anything else with vanilla class weapons or weapons that carry an attack damage attribute, plus armor.

## Categorization settings

Two toggles in `apothic_compat-common.toml` adjust how the universal rule categorizes items. They apply during mod load, so edit the file and restart the server to change them.

- `name_based_heavy_override` (default `false`): when enabled, any item whose registry id contains a heavy weapon name (greatsword, claymore, zweihander, warhammer, halberd, bardiche, glaive, battleaxe, greataxe, lance, pike, maul, naginata, odachi, flamberge, scythe, and the like) is categorized as `heavy_weapon` regardless of its attack speed and damage. Leave it off to categorize purely by speed and damage. Staff, scepter, and wand items are unaffected, since name based staff routing runs first.
- `weapon_pickaxes_as_heavy` (default `true`): the dual-purpose pickaxe list holds combat tools that extend `PickaxeItem` but are swung as weapons. When enabled they categorize as `heavy_weapon` instead of `pickaxe`. The list covers L'Ender's Cataclysm's Void Forge and Infernal Forge and Forbidden and Arcanus's Blacksmith Gavels (every material tier). Disable it for plain `PickaxeItem` behavior.

## Config

A config file shows up at `config/apothic_compat-common.toml` on first launch. Per item and per tag overrides go there:

```toml
[item_overrides]
"ruins:greatsword" = "heavy_weapon"

[tag_overrides]
"simplyswords:greathammers" = "heavy_weapon"
```

Valid category names: `sword`, `heavy_weapon`, `trident`, `bow`, `crossbow`, `shield`, `helmet`, `chestplate`, `leggings`, `boots`, `pickaxe`, `shovel`, `none`. Set an item to none to fully blacklist it from rolling any affixes. Categories registered by other mods (such as staffs from Fallen Gems & Affixes) are also accepted.

## Affix blacklist

Stops specific affixes from rolling on newly generated gear (loot drops, reforging, trades, and gem application) without editing datapacks. List the affix ids in the `affix_blacklist` array in `apothic_compat-common.toml`:

```toml
affix_blacklist = ["apotheosis:sword/attribute/vampiric", "apotheosis:heavy_weapon/attribute/berserking"]
```

Affix ids are paths matching the JSON file location, e.g. `data/apotheosis/affixes/sword/attribute/vampiric.json` corresponds to the id `apotheosis:sword/attribute/vampiric`. To find ids: hover an affixed item with JEI or REI open, or browse the affix files under `data/<namespace>/affixes/` inside a mod's jar (Apotheosis's own affixes live in `data/apotheosis/affixes/`).

Notes:

- This blocks future rolls only. Items that already carry a blacklisted affix keep it.
- Apotheosis's own datapack affix overrides still take precedence.
- The blacklist reapplies automatically on server start and after `/reload`. Edit the list and run `/ac reload` (or `/apothiccompat reload`) to apply it without a restart.

## Items already handled by Apotheosis

Apotheosis hardcodes defaults in `config/apotheosis/adventure.cfg` under the `Equipment Type Overrides` list. These take precedence over both Apothic Compat's config and built in compat modules. As of Apotheosis 7.4.8 the hardcoded defaults are:

- `minecraft:iron_sword` set to `sword`
- `minecraft:shulker_shell` set to `none`

Setting these items in `apothic_compat-common.toml` will not work. To override them, edit `adventure.cfg` directly.

## Reload command

`/apothiccompat reload` or `/ac reload` (op level 2) rereads the config and reapplies it without a restart, every time it's run.

## Requirements

Minecraft 1.20.1, Forge 47.x, Apotheosis 7.4.x. Everything else is optional.

## Installation

Drop the jar in `mods/`. Install on both client and server for correct affix attribute display and gem socket bonuses. Server only works for gameplay (affix stats apply correctly during combat), but client tooltips and gem stats display based on the client's category resolution, which needs the mod installed locally.

## License

MIT, Copyright 2026 Nightwielder23. https://github.com/Nightwielder23/apothic-compat/blob/main/LICENSE

## Source

https://github.com/Nightwielder23/apothic-compat

## Author

Nightwielder23, https://github.com/Nightwielder23
