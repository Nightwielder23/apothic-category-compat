package com.nightwielder.apothiccategorycompat.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nightwielder.apothiccategorycompat.ApothicCategoryCompat;
import com.nightwielder.apothiccategorycompat.config.ApothicCategoryCompatConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

// Gates a data map entry on a boolean from apothic_category_compat-common.toml, so a pack can toggle an override
// without editing the data map. Apotheosis owns the data map type, so a condition is the only hook left.
public record ConfigFlagCondition(String key) implements ICondition {
    public static final MapCodec<ConfigFlagCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("key").forGetter(ConfigFlagCondition::key)
    ).apply(inst, ConfigFlagCondition::new));

    @Override
    public boolean test(IContext context) {
        return switch (key) {
            case "weapon_pickaxes_as_melee" -> ApothicCategoryCompatConfig.weaponPickaxesAsMelee();
            default -> {
                ApothicCategoryCompat.LOGGER.warn("Unknown config_flag key '{}'; applying the entry as if no toggle exists.", key);
                yield true;
            }
        };
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
