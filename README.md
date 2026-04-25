# Apothic Compat

A small server-side 1.20.1 Forge mod that fills in Apotheosis loot-category assignments for weapon/armor mods that don't ship them. Uses Apotheosis's own IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. This mod sends the right categories at load time.

## Supported mods

Every module is a soft dep. A module only runs when both Apotheosis and the target mod are loaded.

- **L'Ender's Cataclysm**: full weapon, shield, and armor coverage
- **Iron's Spellbooks**: melee weapons only (staves, scythes, blades)
- **Tetra**: fixes miscategorized bows, crossbows, and stabilizer-upgraded weapons
- **Alex's Mobs**: Blood Sprayer as bow
- **Alex's Caves**: spears, dagger, staves, ortholance, and gauntlet as swords. Primitive Club as heavy. Dreadbow and Raygun as bows.
- **Spartan Weaponry**: suffix-matched for all material variants
- **Simply Swords**: suffix-matched, heavy variants as heavy weapons
- **Enigmatic Legacy**: Voracious Pan as sword, Axe of Executioner and Astral Breaker as heavy weapons
- **Mowzie's Mobs**: weapons and armor
- **Bosses of Mass Destruction**: Obsidian Spear and Nether Staff as swords
- **Forbidden and Arcanus**: Draco Arcanus axe as heavy, rest as swords
- **Born in Chaos**: scythes/axes/hammers as heavy, swords and daggers as swords
- **Deeper and Darker**: suffix-matched swords and knives
- **Spartan Shields**: all shields
- **Knight Quest**: supports both GPL (knightquest) and Count Grimhart (knight_quest) variants. Paladin Sword as heavy, other weapons as swords
- **Aquamirae**: weapons and armor
- **Epic Knights**: polearms and mauls as heavy weapons, shield overrides
- **Spartan and Fire**: Spartan-style additions handled via Spartan Weaponry compat
- **Meet Your Fight**: Dusk Greatsword as heavy, rest as swords, Bell Crossbow as crossbow
- **Samurai Dynasty**: katanas, kama, and spears as swords
- **Dread Steel**: scythe as heavy weapon, shield as shield
- **Marium's Soulslike Weaponry**: greatswords/scythes/glaives as heavy, spears/swordspears as swords, named legendaries pinned
- **Dungeons and Combat**: hammers as heavy weapons, spears as swords
- **Weapons of Miracles**: overrides for named weapons and armor
- **EpicFight-Resurrection**: greatswords, longswords, and great tachi as heavy weapons
- **EpicFight-Nightfall**: Ruins Greatsword and Ghiza's Wheel as heavy weapons
- **Celestisynth**: nine named weapons, mostly swords with Poltergeist as heavy and Rainfall Serenity as bow

## Handled by universal fallback

These mods extend the right vanilla classes (SwordItem, AxeItem, BowItem, etc.) so the universal fallback categorizes them correctly without needing an explicit module:

- **Farmer's Delight**: knives as swords
- **Dungeons Delight**: knives and cleavers as swords
- **dacxirons**: staves as swords
- **Cataclysm Weaponry**: ignitium tools and sword
- **Immersive Armors**: armor pieces
- **Armageddon**: most items
- Anything else with vanilla-class weapons/armor

## Config

A config file shows up at `config/apothic_compat.toml` on first launch. Per-item and per-tag overrides go there:

```toml
[item_overrides]
"ruins:greatsword" = "heavy_weapon"

[tag_overrides]
"simplyswords:greathammers" = "heavy_weapon"
```

Valid category names: `sword`, `heavy_weapon`, `bow`, `crossbow`, `shield`, `helmet`, `chestplate`, `leggings`, `boots`, `pickaxe`, `shovel`, `none`.

## Reload command

`/apothiccompat reload` or `/ac reload` (op level 2) re-applies the config without a restart. It's a no-op if the file hasn't changed.

## Requirements

Minecraft 1.20.1, Forge 47.x, Apotheosis 7.4.x. Everything else is optional.

## Installation

Drop the jar in `mods/`. Server-side only (clients don't need it).

## License

MIT, Copyright 2026 Nightwielder23. https://github.com/Nightwielder23/apothic-compat/blob/main/LICENSE

## Source

https://github.com/Nightwielder23/apothic-compat

## Author

Nightwielder23, https://github.com/Nightwielder23