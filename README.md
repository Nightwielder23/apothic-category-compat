# Apothic Compat

A small server-side 1.19.2 Forge mod that fills in Apotheosis loot-category assignments for weapon/armor mods that don't ship them. Uses Apotheosis's own IMC override API, so nothing is patched or mixin'd.

## What it does

Apotheosis uses loot categories to decide which affixes and gem sockets an item can roll. A lot of modded weapons either don't have a category at all or get the wrong one, so affixes never appear on them. Apothic Compat sends the right categories at load time over Apotheosis's IMC override API.

The core rule is universal: every registered item is categorized by what it actually is, not by a hardcoded list. Any item that deals melee attack damage is split into `sword` or `heavy_weapon` by its attack speed, using the same thresholds Obscure API shows in its weapon tooltips: an attack speed above 1.0 reads as a sword, at or below 1.0 reads as a heavy weapon. This works for any mod's weapons no matter which Java class they extend, including plain `Item`, `TieredItem`, `DiggerItem`, and modular subclasses. Bows, crossbows, tridents, pickaxes, shovels, shields, and armor are read straight from the vanilla class hierarchy.

## Per-mod modules

The universal rule covers almost everything on its own. A few mods register ranged weapons, shields, or utility items under custom classes that carry no usable hierarchy, so those get small explicit overrides. A module only runs when both Apotheosis and the target mod are loaded.

- **L'Ender's Cataclysm**: cursed bow and wrath of the desert as bows, the assault shoulder weapons and laser gatling as crossbows, and bulwark of the flame as a shield (all plain `Item` or `ProjectileWeaponItem`; the black steel targe is a real `ShieldItem` so the universal rule covers it)
- **Tetra**: modular bow, crossbow, and shield (all extend `ModularItem`, not the vanilla ranged or shield classes); modular melee builds go through the universal rule by attack speed
- **Meet Your Fight**: the Guns Without Roses compat shotgun and rifle as crossbows
- **Alex's Mobs**: Blood Sprayer as bow
- **The Undergarden**: slingshot as bow (extends `ProjectileWeaponItem`)
- **Twilight Forest**: the lifedrain/fortification/twilight/zombie scepters and Ice Bomb as none (utility items with no attack damage)
- **Everything else** (including all `ShieldItem` shields, e.g. Dread Steel, Spartan Shields, Epic Knights): categorized by the universal rule above

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
