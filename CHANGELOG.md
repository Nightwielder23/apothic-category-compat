# Apothic Compat changelog

## 2.0.0

Rebuilt categorization around a universal attack speed and damage rule: any item that deals melee attack damage is sorted into sword or heavy_weapon by its live attack speed, while bows, crossbows, tridents, tools, shields, and armor are read from the vanilla class hierarchy. Explicit per mod overrides now remain only for items the class hierarchy and speed read cannot place, across L'Ender's Cataclysm, Tetra, Meet Your Fight, Epic Fight, Epic Samurai, Aquamirae, Forbidden and Arcanus, Alex's Mobs, The Undergarden, and Twilight Forest. Modules whose items the universal rule already covers were dropped. Two config toggles tune the categorization: name_based_heavy_override (off by default) forces items whose registry id contains a heavy weapon name to heavy_weapon regardless of attack speed and damage, and weapon_pickaxes_as_heavy (on by default) routes dual-purpose pickaxe tools like the Void Forge, Infernal Forge, and Blacksmith Gavels to heavy_weapon instead of pickaxe. Also fixed the MULTIPLY_BASE attack attribute math to match vanilla, scaling the full base plus addition sum instead of dropping the base term.

## 1.8.0

Fixed the /apothiccompat reload command and updated the mod metadata.

## 1.0.0

Initial 1.19.2 backport of Apothic Compat.
