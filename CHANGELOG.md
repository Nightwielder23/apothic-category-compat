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
- Rebuilt categorization around a universal attack speed and damage rule: any item that deals melee attack damage is sorted into sword or heavy_weapon by its live attack speed, while bows, crossbows, tridents, tools, shields, and armor are read from the vanilla class hierarchy. Explicit per mod overrides now remain only for items the class hierarchy and speed read cannot place.
- Renamed the config file from apothic_category_compat.toml to apothic_category_compat-common.toml to follow Forge's COMMON config naming. Breaking change: the old file is not migrated, so a fresh default is generated on first launch and any previous per item, per tag, and affix blacklist entries are lost. Copy them into the new file.
- /apothiccategorycompat reload now reports what changed: the override count split into new, changed, and unchanged, the affix blacklist diff, and a note when the categorization toggles were edited (those still need a restart).

### Added
- Configurable name based heavy override, name_based_heavy_override, off by default: when on, items whose registry id contains a heavy weapon name such as greatsword, claymore, or zweihander are forced to heavy_weapon regardless of attack speed and damage.
- Configurable dual-purpose pickaxe handling, weapon_pickaxes_as_heavy, on by default: combat tools that subclass PickaxeItem (the Void Forge, Infernal Forge, and Blacksmith Gavels) categorize as heavy_weapon instead of pickaxe.
- Optional affix blacklist that stops named affixes from rolling on newly generated gear, set in the config and reapplied on server start and after /reload.

### Fixed
- MULTIPLY_BASE attack attribute math now matches vanilla: the base and addition sum is scaled together by the multiplier instead of dropping the base term.

### Removed
- Per mod modules whose items the universal rule already covers.

## v1.8.0

Fixed the /apothiccategorycompat reload command and updated the mod metadata.

## v1.0.0

Initial 1.19.2 backport of Apothic Category Compat.
