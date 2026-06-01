# Apothic Compat

A small server-side 1.19.2 Forge mod that fills in Apotheosis loot-category assignments for weapon/armor mods that don't ship them. Uses Apotheosis's own IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. Apothic Compat sends the right categories at load time.

## Supported mods

Every module is a soft dep. A module only runs when both Apotheosis and the target mod are loaded.

- **Epic Samurai**: katanas, kama, and spears as swords
- **Dread Steel**: scythe as heavy weapon, shield as shield
- **Tetra**: fixes miscategorized bows, crossbows, and stabilizer-upgraded weapons
- **Weapons of Miracles**: overrides for named weapons and armor
- **L'Ender's Cataclysm**: full weapon, shield, and armor coverage
- **Simply Swords**: suffix-matched, heavy variants as heavy weapons
- **Iron's Spellbooks**: melee weapons only (staves, scythes, blades)
- **Aquamirae**: weapons and armor
- **Mowzie's Mobs**: weapons and armor
- **Spartan Shields**: all shields
- **Spartan Weaponry**: suffix-matched for all material variants
- **Epic Knights**: polearms and mauls as heavy weapons, shield overrides
- **Born in Chaos**: scythes/axes/hammers as heavy, swords and daggers as swords
- **Celestisynth**: nine named weapons, mostly swords with Poltergeist as heavy and Rainfall Serenity as bow
- **Alex's Mobs**: Blood Sprayer as bow
- **Forbidden and Arcanus**: Draco Arcanus axe as heavy, rest as swords
- **Bosses of Mass Destruction**: Obsidian Spear as heavy, Nether Staff as sword
- **Meet Your Fight**: Dusk Greatsword as heavy, rest as swords, Bell Crossbow as crossbow
- **Deeper and Darker**: suffix-matched swords and knives
- **Knight Quest Reforged**: Paladin Sword as heavy, Cleaver/Uchigatana/Nail/Kukri as swords
- **Enigmatic Legacy**: Voracious Pan as sword, Axe of Executioner and Astral Breaker as heavy weapons
- **Malum**: scythes (crude, soul stained steel, edge of deliverance, weight of worlds) as heavy weapons, tyrving and sundering anchor as swords
- **Twilight Forest**: the lifedrain/fortification/twilight/zombie scepters as swords, Mazebreaker Pickaxe as pickaxe, Ice Bomb as none. Standard swords, bows, axes, and pickaxes go through the universal fallback
- **The Undergarden**: cloggrum, forgotten, froststeel, and utherium battleaxes as heavy weapons, spear as sword, slingshot as bow. Standard items go through the universal fallback
- **Epic Fight**: greatswords as heavy weapons; longswords, daggers, spears, tachis, bokken, uchigatana, and glove as swords
- **Spartan and Fire**: picks up Spartan Weaponry tag registrations
- **Immersive Armors**: handled by universal fallback
- **Universal fallback**: anything else categorized by Java class

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

`/apothiccompat reload` or `/ac reload` (op level 2) re-reads the config and re-applies it without a restart, every time it's run.

## Requirements

Minecraft 1.19.2, Forge 43.x, Apotheosis 6.5.2. Everything else is optional.

## Installation

Drop the jar in `mods/`. Server-side only (clients don't need it).

## License

MIT, Copyright 2026 Nightwielder23. https://github.com/Nightwielder23/apothic-compat/blob/main/LICENSE

## Source

https://github.com/Nightwielder23/apothic-compat

## Author

Nightwielder23, https://github.com/Nightwielder23
