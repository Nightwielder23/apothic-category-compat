# Apothic Compat changelog

## v2.0.0

### Changed
- Rebuilt categorization around a universal rule: any item that deals melee attack damage is sorted into sword or heavy_weapon by its live attack speed, with a heavy damage cutoff for fast weapons that still hit hard. Bows, crossbows, tridents, tools, shields, and armor are read from the vanilla class hierarchy. Explicit per mod overrides now remain only for items the class hierarchy and speed read cannot place.
- A second pass at mod load complete reruns categorization after mods that finalize weapon stats during deferred init, correcting items that read stale on the first scan.

### Added
- Name based staff detection: with Fallen Gems & Affixes loaded, any item whose registry id names a staff, scepter, or wand routes to its staffs category, so modded casters no longer each need an explicit override. Battle and war staves and quarterstaves stay melee.
- Iron's Spellbooks staffs route to the Fallen Gems & Affixes staffs category.
- Forbidden and Arcanus draco_arcanus_scepter and Alex's Caves sea and sugar staves defer to the staffs category when present.
- Born in Chaos pumpkinhandgun (Pumpkin Pistol) categorized as crossbow.

### Fixed
- Traveloptics scepters and staffs emit the staffs category explicitly instead of relying on Fallen Gems autodetection, which never claimed them.
- Malum scythes route by attack speed as melee weapons again, rather than deferring to staffs.

### Removed
- Per mod modules whose items the universal rule already covers: Simply Swords, Integrated Simply Swords, Spartan Weaponry, Spartan Shields, Epic Knights, Samurai Dynasty, Dread Steel, Mowzie's Mobs, Bosses of Mass Destruction, Deeper and Darker, Knight Quest, Enigmatic Legacy, and RPG Style More Weapons.

## v1.7.1

### Fixed
- Simply Swords warglaives were being miscategorized as heavy weapon due to a suffix matching collision with the glaive heavy suffix; now short circuited to sword before the heavy pass
- Prevented potential crash when FG&A was loaded without Iron's Spellbooks since FG&A's staffs category requires Iron's; Dungeons and Combat scepter routing now checks both mods are loaded
- Added trident to the loot category list in the generated config comment
- Fixed spurious minecraft:air overrides being dispatched when configured item ids did not exist in the current modpack; ForgeRegistries.ITEMS.getValue returns Items.AIR for unregistered keys instead of null, so the existing null guard never fired; added a RegistryLookup helper that checks containsKey first and logs misses at warn so version skew entries surface in user logs
- Removed stale Cataclysm throwing weapon ids (astrape_throwing, ceraunus_throwing, coral_spear_throwing, coral_bardiche_throwing) which were consolidated into base items in Cataclysm 2.x
- Removed stale Weapons of Miracles netherite_tachi id which was renamed or removed in the 2.x rewrite
- Reverted Simply Swords and Integrated Simply Swords claymores back to sword since they play at sword speed (gameplay observation, not historical naming)
- Moved Cataclysm zweiender from heavy weapon to sword for the same reason
- Moved Epic Fight Resurrection longswords from heavy weapon to sword for the same reason

## v1.7.0

### Added
- Epic Fight compat for greatswords as heavy weapons; longswords, daggers, spears, tachis, bokken, uchigatana, and glove as swords
- Integrated Simply Swords compat for cross mod material variants, matching the Simply Swords mapping; alexscaves polarizer integration as heavy weapon
- RPG Style More Weapons compat for battle axes and greatswords as heavy, knives as swords

### Changed
- Bosses of Mass Destruction module now covers earthdive_spear; legacy nether_staff and obsidian_spear entries kept as no-ops for older mod versions
- Dungeons and Combat module now categorizes greatswords (including dragon_greatsword_bone, dragon_greatsword_fire, dragon_greatsword_ice, dragon_greatsword_lightning) and claymores as heavy weapons
- Simply Swords and Integrated Simply Swords claymores moved from sword to heavy weapon
- Epic Knights module dropped the flamberge and greatsword tokens that had no matching items in current versions
- Migrated all compat modules to ResourceLocation.fromNamespaceAndPath
- Removed unused wo_traveloptics dependency declaration

## v1.6.3

### Fixed
- Moved wither assault shoulder weapon, laser gatling, and void assault shoulder weapon from heavy weapon to crossbow category since they fire projectiles

## v1.6.2

### Fixed
- Corrected wrath of the desert categorization
- Routed Dungeons and Combat scepters to Fallen Gems' staffs when present, otherwise sword
- Fixed blackstar and solar (Weapons of Miracles) categorization

## v1.6.1

### Added
- Runtime registered loot category support; config now accepts categories like Fallen Gems' staffs, celestial_melee, and celestial_ranged when those mods are loaded
- The "none" category for fully blacklisting items from rolling any affixes

## v1.6.0

### Added
- Fallen Gems and Affixes detection helper; Iron's Spellbooks and Celestisynth defer to it when present
- T.O Magic 'n Extras (traveloptics) compat covering boss weapons across all four upgrade tiers, with staff lines gated on Fallen Gems and Affixes

## v1.5.0

### Added
- Epic Fight Resurrection (cdmoveset) compat for greatsword, longsword, and great tachi as heavy weapons
- Epic Fight Nightfall (efn) compat for ruins greatsword and Ghiza's Wheel as heavy weapons
- Alex's Caves compat for spears, dagger, staves, ortholance, gauntlet, primitive club, dreadbow, and raygun

## v1.4.1

### Fixed
- Epic Knights mod ID was checking `epicknights`, actual is `magistuarmory`. Module now works.
- Armageddon mod ID was checking `armageddon`, actual is `armageddon_mod`.

### Added
- Knight Quest (Count Grimhart variant `knight_quest`) support alongside GPL `knightquest`.
- Spartan Weaponry `_javelin` suffix support.

### Changed
- Spears across all modules now categorized as SWORD instead of HEAVY_WEAPON. Affects Spartan Weaponry, Simply Swords, Samurai Dynasty, Bosses of Mass Destruction, Knight Quest, Marium's Soulslike, Mowzie's Mobs, Dungeons and Combat, Epic Knights, L'Ender's Cataclysm.
- Cataclysm `void_core` removed from HEAVY_WEAPON (it's a mage cast item).

## 1.4.0

Added compat modules for:

- Marium's Soulslike Weaponry
- Born in Chaos
- Celestisynth
- Alex's Mobs
- Forbidden and Arcanus
- Bosses of Mass Destruction
- Meet Your Fight
- Deeper and Darker
- Knight Quest Reforged
- Enigmatic Legacy

Removed the Cataclysm Weaponry module. Its items extend vanilla weapon classes, so the universal fallback already categorizes them correctly.
