package com.nightwielder.apothiccategorycompat;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.nightwielder.apothiccategorycompat.command.ReloadCommand;
import com.nightwielder.apothiccategorycompat.condition.ConfigFlagCondition;
import com.nightwielder.apothiccategorycompat.config.ApothicCategoryCompatConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

// Loot category overrides are in a data map, so Apotheosis applies those itself. The code here registers
// the config_flag condition that gates one of those entries, the reload command, and the affix blacklist.
@Mod(ApothicCategoryCompat.MODID)
public final class ApothicCategoryCompat {
    public static final String MODID = "apothic_category_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MODID);

    static {
        CONDITION_CODECS.register("config_flag", () -> ConfigFlagCondition.CODEC);
    }

    public ApothicCategoryCompat(IEventBus modBus) {
        CONDITION_CODECS.register(modBus);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ReloadCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        ApothicCategoryCompatConfig.loadAffixBlacklist();
    }

    // A /reload rebuilds Apotheosis's affix pool and drops the blacklist filter, so reapply after datapacks
    // sync. Per player sync is skipped since the pool only changes on a full reload.
    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            ApothicCategoryCompatConfig.loadAffixBlacklist();
        }
    }
}
