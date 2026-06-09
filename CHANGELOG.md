# Apothic Compat changelog

## v2.0.2

### Changed
- Twilight Forest Block and Chain now maps to bow instead of melee weapon, and Cube of Annihilation is added as bow. Both deal their damage through a thrown projectile entity, which is what Apotheosis's bow affixes hook, so melee was the wrong category.

### Removed
- Dropped the Alex's Mobs Shield Of The Deep override. Apotheosis 8.x already categorizes it as a shield on its own, so the entry was redundant.

## v2.0.1

First NeoForge 1.21.1 build, against Apotheosis 8.5.x. Apotheosis 8.x dropped the sword/heavy_weapon split (one melee_weapon category now), removed the IMC override channel, and moved category overrides to a NeoForge data map, so this is a much smaller mod than the 1.20.1 line rather than a feature for feature port.

### Changed
- Overrides now live in a data map at data/apotheosis/data_maps/item/loot_category_overrides.json. Apotheosis loads it the same way it loads its own, so no code applies them.
- Affix blacklist ported to the Apotheosis 8.x affix package. It still rebuilds the affix-by-type pool without the blacklisted ids, reapplied on server start and after /reload.

### Added
- Data map overrides for ranged weapons, shields, and odd melee weapons Apotheosis 8.x does not categorize: Alex's Caves Raygun and Dreadbow, Alex's Mobs Hemolymph Blaster and Blood Sprayer (bows) and Shield Of The Deep (shield), Born in Chaos Pumpkin Pistol, L_Ender's Cataclysm Cursed Bow, Wrath of the Desert, Void and Wither Assault Shoulder Weapons, and Laser Gatling, The Undergarden Slingshot (bow), and Twilight Forest Block and Chain (melee weapon).

### Removed
- The universal attack speed rule. Apotheosis 8.x categorizes any melee weapon with attack damage on its own.
- The sword/heavy_weapon split and the name_based_heavy_override and weapon_pickaxes_as_heavy settings, since there is no heavy_weapon category to route to.
- Per item and per tag TOML overrides. Use a datapack data map instead.
- The per mod IMC modules and the universal scan, replaced by the static data map.
