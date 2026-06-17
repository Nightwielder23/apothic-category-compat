# Apothic Category Compat changelog

## v2.1.0

### Added
- Name-based sword rule for the universal categorizer: with `name_based_heavy_override` on, an item whose id path ends in `sword` is categorized as `sword`, which demotes a formula-classified `heavy_weapon` back to `sword` when no heavy keyword matched. Heavy keywords are checked first, so a greatsword stays heavy.

### Changed
- `name_based_heavy_override` now defaults to true, so the name-based rules apply without editing the config. Set it false for categorization by attack speed and damage alone.
  - Existing configs keep their current `name_based_heavy_override` value; the new default applies only to fresh installs. Set it to `true` or delete the config file to pick up the name-based rules.
- Renamed the mod from Apothic Compat to Apothic Category Compat (mod id `apothic_compat` to `apothic_category_compat`) to avoid confusion with Apothic Compats by ianm1647. An existing `apothic_compat-common.toml` moves to `apothic_category_compat-common.toml` on first load, so your settings carry over.
- Reload command short alias changed from `/ac` to `/acc`. The long form `/apothiccategorycompat reload` is unchanged.

### Fixed
- Twilight Forest Block and Chain moved from bow to crossbow. It is a slow returning chained shot, so crossbow suits it better than bow.
- Twilight Forest Cube of Annihilation now routes through the `weapon_pickaxes_as_heavy` toggle (default `heavy_weapon`), the same as the Cataclysm forges, since it both breaks blocks and deals damage. v2.0.2 had categorized it as bow.
- Epic Fight style weapons that carry their attack damage only at the stack level, such as Weapons of Miracles greataxes, now reach the speed and name rules instead of defaulting to sword.

## v2.0.2

### Fixed
- Twilight Forest Block and Chain and Cube of Annihilation now categorize as bow. Both deal their damage through a thrown projectile entity, which is what Apotheosis's bow affixes hook, so they were left uncategorized before.

## v2.0.1

### Fixed
- Categorization no longer takes down mod loading when another mod's item throws while its attack attributes are read (seen with Enigmatic Addons' Annihilating Sword, which reaches for the server during IMC). The offending item is logged and skipped.
- Tag overrides apply at server start, when item tags are bound, instead of being skipped during early startup.
- Per item and per tag overrides are reapplied after the deferred-init pass, so a user override is no longer overwritten by the second categorization pass.
- Tridents and other TridentItem-class weapons categorize as trident instead of heavy_weapon.
- A broken third-party affix no longer takes down the affix blacklist rebuild on server start; it is logged and skipped.
- README trident wording corrected.

## v2.0.0

### Changed
- Rebuilt categorization around a universal rule: any item that deals melee attack damage is sorted into sword or heavy_weapon by its live attack speed, with a heavy damage cutoff for fast weapons that still hit hard. Bows, crossbows, tridents, tools, shields, and armor are read from the vanilla class hierarchy. Explicit per mod overrides now remain only for items the class hierarchy and speed read cannot place.
- A second pass at mod load complete reruns categorization after mods that finalize weapon stats during deferred init, correcting items that read stale on the first scan.
- Renamed the config file from apothic_category_compat.toml to apothic_category_compat-common.toml to follow Forge's COMMON config naming. Breaking change: the old file is not migrated, so a fresh default is generated on first launch and any previous per item, per tag, and affix blacklist entries are lost. Copy them into the new file.
- /apothiccategorycompat reload now reports what actually changed: the override count split into new, changed, and unchanged, the affix blacklist diff, and a note when the categorization toggles were edited (those still need a restart). It says "No changes detected." when the file is untouched.

### Added
- Name based staff detection: with Fallen Gems & Affixes loaded, any item whose registry id names a staff, scepter, or wand routes to its staffs category, so modded casters no longer each need an explicit override. Battle and war staves and quarterstaves stay melee.
- Iron's Spellbooks staffs route to the Fallen Gems & Affixes staffs category.
- Forbidden and Arcanus draco_arcanus_scepter and Alex's Caves sea and sugar staves defer to the staffs category when present.
- Born in Chaos pumpkinhandgun (Pumpkin Pistol) categorized as crossbow.
- Configurable name based heavy override, name_based_heavy_override, off by default: when on, items whose registry id contains a heavy weapon name such as greatsword, claymore, or zweihander are forced to heavy_weapon regardless of attack speed and damage.
- Configurable dual-purpose pickaxe handling, weapon_pickaxes_as_heavy, on by default: combat tools that subclass PickaxeItem (the Void Forge, Infernal Forge, and Blacksmith Gavels) categorize as heavy_weapon instead of pickaxe.

### Fixed
- Traveloptics scepters and staffs emit the staffs category explicitly instead of relying on Fallen Gems autodetection, which never claimed them.
- Malum scythes route by attack speed as melee weapons again, rather than deferring to staffs.
- MULTIPLY_BASE attack attribute math now matches vanilla: the base and addition sum is scaled together by the multiplier instead of dropping the base term, so weapons carrying a MULTIPLY_BASE modifier read the correct attack damage and speed.

### Removed
- Per mod modules whose items the universal rule already covers: Simply Swords, Integrated Simply Swords, Spartan Weaponry, Spartan Shields, Epic Knights, Samurai Dynasty, Dread Steel, Mowzie's Mobs, Bosses of Mass Destruction, Deeper and Darker, Knight Quest, Enigmatic Legacy, and RPG Style More Weapons.

## v1.8.0

### Added
- Affix blacklist: a config list that stops specific affixes from rolling on newly generated gear (loot drops, reforging, trades, and gem application), reapplied on server start and after /reload.
- Malum, Twilight Forest, and Undergarden compat modules.

### Changed
- Routed Twilight Forest scepters to the Fallen Gems & Affixes staffs category when present.
- Consolidated the per mod compat modules.

### Fixed
- /apothiccategorycompat reload now reapplies overrides correctly instead of dropping them.

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

## v1.4.0

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
