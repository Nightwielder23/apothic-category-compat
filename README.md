# Apothic Compat

A small server-side 1.20.1 Forge mod that fills in Apotheosis loot-category assignments for weapon/armor mods that don't ship them. Uses Apotheosis's own IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. This mod sends the right categories at load time.

## Supported mods

Every module is a soft dep. A module only runs when both Apotheosis and the target mod are loaded.

- **Samurai Dynasty**: katanas, kama, and sai as swords; spears as heavy weapons.
- **Dread Steel**: Dreadsteel Scythe → heavy weapon, Dreadsteel Shield → shield.
- **Tetra**: fixes Tetra bows/crossbows being miscategorized, and weapons upgraded with Planar Stabilizers being seen as pickaxes. Picks sword vs. heavy-weapon by attack damage.
- **Weapons of Miracles**: explicit overrides for named weapons/armor, plus a class-based fallback for anything else in the `wom` namespace.
- **L'Ender's Cataclysm**: full set of weapons, shields, and armor categorized by explicit ID.
- **Simply Swords**: suffix-matched. Greathammers/greataxes/spears/glaives as heavy weapons, the sword-like variants as swords.
- **Iron's Spellbooks**: only the straight melee weapons (staves, scythes, blades); magic items are left alone.
- **Aquamirae**: weapons and armor by explicit ID.
- **Mowzie's Mobs**: weapons and masks/armor by explicit ID.
- **Spartan Shields**: everything that's a `ShieldItem`.
- **Dungeons and Combat**: class-based plus suffix matching for polearms/hammers that extend `SwordItem`.
- **Spartan Weaponry**: suffix-matched, so every material variant is covered. Daggers/parrying daggers/longswords/katanas/sabers/rapiers → sword, the big stuff (battleaxes, battle hammers, warhammers, flanged maces, greatswords, glaives, halberds, lances, pikes, spears, quarterstaves, scythes, clubs, cestuses) → heavy weapon, longbows → bow, heavy crossbows → crossbow. Throwing weapons are skipped (Apotheosis has no category for them).
- **Armageddon**: placeholder. Most of the mod's weapons extend vanilla classes and are covered by the universal fallback.
- **Epic Knights**: explicit overrides for shields and for polearms/mauls that extend `SwordItem` but should roll heavy weapon affixes. Everything else goes through the universal fallback.
- **Marium's Soulslike Weaponry**: suffix plus explicit overrides. Greatswords/scythes/spears/glaives/swordspears as heavy weapons, shortswords and regular swords as swords, longbows/bowblades as bows. Legendary one-offs (Leviathan Axe, Mjolnir, Kirkhammer, Darkin Blade, Soul Reaper, Glaive of Hodir, Whirligig Sawblade, Master Sword) are pinned as heavy weapons, and named swords without a shape suffix (Bloodthirster, Nightfall, Skofnung, Excalibur, Frostmourne, etc.) are pinned as swords.
- **Born in Chaos**: suffix plus explicit overrides. Scythes/axes/hammers/maces as heavy weapons, swords/daggers/cutlasses/blades as swords. Darkwarblade, Trident Hayfork, and Soulbane are pinned explicitly.
- **Celestisynth**: explicit overrides for the nine named weapons. Aquaflora, Breezebreaker, Crescentia, Frostbound, Keres, and Solaris as swords, Poltergeist as a heavy weapon, Rainfall Serenity as a bow.
- **Alex's Mobs**: Blood Sprayer as a sword. Armor and vanilla-class items fall through to the universal fallback.
- **Forbidden and Arcanus**: Mystical Dagger, Draco Arcanus Scepter, and Draco Arcanus Sword as swords, Draco Arcanus Axe as a heavy weapon. Material-tier blacksmith gavels are caught via suffix as swords.
- **Bosses of Mass Destruction**: Obsidian Spear as a heavy weapon, Nether Staff as a sword.
- **Meet Your Fight**: Mossy Sword, Shoulder Revolver, and Dusk Blade as swords, Dusk Greatsword as a heavy weapon, Bell Crossbow as a crossbow. Suffix fallback (`_greatsword`, `_sword`, `_crossbow`) catches anything added in later versions.
- **Deeper and Darker**: suffix-matched, so anything ending in `_sword` or `_knife` gets sword. Armor, elytra, and sculk blocks fall through to the universal fallback.
- **Knight Quest Reforged**: suffix plus explicit overrides. Swords end in `_sword`, battle axes in `_axe` as heavy weapons, spears in `_spear` as heavy weapons. Hoplite Spear and the four named sword sets (Paladin, Nightlord, Fireforged, Dawnbringer) are pinned explicitly.
- **Spartan and Fire**: picks up the Spartan Weaponry tag registrations, so installing SW alongside is enough.
- **Immersive Armors**: handled by the universal fallback, since every piece extends `ArmorItem`.
- **Universal fallback**: anything Apotheosis didn't already categorize gets assigned by Java class (swords, axes, bows, crossbows, tridents, pickaxes, shovels, armor slots).

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
