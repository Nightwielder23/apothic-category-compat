# Apothic Compat

A small server-side 1.20.1 Forge mod that fills in Apotheosis loot-category assignments for weapon/armor mods that don't ship them. Uses Apotheosis's own IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. This mod sends the right categories at load time.

## Supported mods

Every module is a soft dep. A module only runs when both Apotheosis and the target mod are loaded.

- **Samurai Dynasty** — katanas, kama, and sai as swords; spears as heavy weapons.
- **Dread Steel** — Dreadsteel Scythe → heavy weapon, Dreadsteel Shield → shield.
- **Tetra** — fixes Tetra bows/crossbows being miscategorized, and weapons upgraded with Planar Stabilizers being seen as pickaxes. Picks sword vs. heavy-weapon by attack damage.
- **Weapons of Miracles** — explicit overrides for named weapons/armor, plus a class-based fallback for anything else in the `wom` namespace.
- **L'Ender's Cataclysm** — full set of weapons, shields, and armor categorized by explicit ID.
- **Cataclysm Weaponry** — categorized by item class (sword / axe / bow / crossbow).
- **Simply Swords** — suffix-matched: greathammers/greataxes/spears/glaives as heavy weapons, the sword-like variants as swords.
- **Iron's Spellbooks** — only the straight melee weapons (staves, scythes, blades); magic items are left alone.
- **Aquamirae** — weapons and armor by explicit ID.
- **Mowzie's Mobs** — weapons and masks/armor by explicit ID.
- **Spartan Shields** — everything that's a `ShieldItem`.
- **Dungeons and Combat** — class-based plus suffix matching for polearms/hammers that extend `SwordItem`.
- **Spartan Weaponry** — tag-based, so every material variant is covered. Daggers/longswords/katanas/sabers/rapiers/cestuses → sword, the big stuff → heavy weapon, longbows → bow, heavy crossbows → crossbow. Throwing weapons are skipped (Apotheosis has no category for them).
- **Armageddon** — placeholder; most of the mod's weapons extend vanilla classes and are covered by the universal fallback.
- **Universal fallback** — anything Apotheosis didn't already categorize gets assigned by Java class (swords, axes, bows, crossbows, tridents, pickaxes, shovels, armor slots).

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

Drop the jar in `mods/`. Server-side only — clients don't need it.

## License

MIT, Copyright 2026 Nightwielder23. https://github.com/Nightwielder23/apothic-compat/blob/main/LICENSE

## Source

https://github.com/Nightwielder23/apothic-compat

## Author

Nightwielder23 — https://github.com/Nightwielder23
