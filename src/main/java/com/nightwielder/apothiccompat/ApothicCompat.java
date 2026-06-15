package com.nightwielder.apothiccompat;

import com.mojang.logging.LogUtils;
import com.nightwielder.apothiccompat.command.ReloadCommand;
import com.nightwielder.apothiccompat.config.ApothicCompatConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

// Loot category overrides are in a data map, so Apotheosis applies those itself. The only code here is
// the reload command and the affix blacklist, which must be reapplied after the affix pool loads.
@Mod(ApothicCompat.MODID)
public final class ApothicCompat {
    public static final String MODID = "apothic_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ApothicCompat() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ReloadCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        ApothicCompatConfig.loadAffixBlacklist();
    }

    // A /reload rebuilds Apotheosis's affix pool and drops the blacklist filter, so reapply after datapacks
    // sync. Per player sync is skipped since the pool only changes on a full reload.
    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            ApothicCompatConfig.loadAffixBlacklist();
        }
    }
}
