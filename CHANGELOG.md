# Apothic Compat changelog

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
