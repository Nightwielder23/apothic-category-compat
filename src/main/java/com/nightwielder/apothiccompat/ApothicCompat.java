package com.nightwielder.apothiccompat;

import com.mojang.logging.LogUtils;
import com.nightwielder.apothiccompat.compat.AlexsMobsCompat;
import com.nightwielder.apothiccompat.compat.AquamiraeCompat;
import com.nightwielder.apothiccompat.compat.BornInChaosCompat;
import com.nightwielder.apothiccompat.compat.CelestisynthCompat;
import com.nightwielder.apothiccompat.compat.EpicFightCompat;
import com.nightwielder.apothiccompat.compat.EpicSamuraiCompat;
import com.nightwielder.apothiccompat.compat.ForbiddenArcanusCompat;
import com.nightwielder.apothiccompat.compat.LEnderCataclysmCompat;
import com.nightwielder.apothiccompat.compat.MeetYourFightCompat;
import com.nightwielder.apothiccompat.compat.TetraCompat;
import com.nightwielder.apothiccompat.compat.TwilightForestCompat;
import com.nightwielder.apothiccompat.compat.UndergardenCompat;
import com.nightwielder.apothiccompat.compat.UniversalCompat;
import com.nightwielder.apothiccompat.command.ReloadCommand;
import com.nightwielder.apothiccompat.config.ApothicCompatConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ApothicCompat.MODID)
public class ApothicCompat {
    public static final String MODID = "apothic_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ApothicCompat(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::sendCategoryOverrides);
        modBus.addListener(this::reapplyAfterDeferredInit);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ReloadCommand.register(event.getDispatcher());
    }

    // Affixes load with datapacks after the IMC pass, so apply the blacklist here, not through IMC. A /reload
    // rebuilds Apoth's pool and drops the filter, so reapply on sync; per player sync is skipped.
    private void onServerStarted(ServerStartedEvent event) {
        if (!ModList.get().isLoaded("apotheosis")) {
            return;
        }
        ApothicCompatConfig.loadAffixBlacklist();
        // Item tags are bound and the second pass has run by now, so reapply config overrides here: tag
        // overrides finally resolve and a user's per item overrides reclaim last-wins.
        ApothicCompatConfig.applyOverridesAtRuntime();
    }

    private void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            return;
        }
        if (!ModList.get().isLoaded("apotheosis")) {
            return;
        }
        ApothicCompatConfig.loadAffixBlacklist();
    }

    // Apotheosis's IMC takes only (item, category), no slot, so "{mainhand}" tooltip lines come from vanilla
    // or Curios lang keys, not from changing the IMC payload here.
    //
    // Overrides store last wins, so send order is precedence: UniversalCompat first (speed default), per mod
    // modules next (explicit decisions win), config last (user override beats all).
    private void sendCategoryOverrides(InterModEnqueueEvent event) {
        if (!ModList.get().isLoaded("apotheosis")) {
            LOGGER.info("Apotheosis not present; skipping all compat modules.");
            return;
        }
        dispatchModules();
        ApothicCompatConfig.load();
    }

    // Some mods (e.g. Mowzie's Mobs) finalize attack stats in deferred init that runs after the IMC scan, so
    // the first pass reads stale stats and miscategorizes. FMLLoadCompleteEvent is the last load event, so
    // rerun the dispatch here, writing Apoth's override maps directly since the IMC window is closed.
    private void reapplyAfterDeferredInit(FMLLoadCompleteEvent event) {
        if (!ModList.get().isLoaded("apotheosis")) {
            return;
        }
        int changed = ApothicCompatConfig.reapply(this::dispatchModules);
        LOGGER.info("Apothic Compat second pass recategorized {} item(s) after deferred mod init.", changed);
    }

    // Shared dispatch used by both passes. The active CompatImc sink (IMC for the first pass, runtime
    // override map writes for the second) decides where the results land, so the module code is identical.
    private void dispatchModules() {
        // UniversalCompat reads the categorization toggles, so refresh them before it runs.
        ApothicCompatConfig.loadSettings();
        UniversalCompat.send();
        if (ModList.get().isLoaded("tetra")) {
            TetraCompat.send();
        }
        if (ModList.get().isLoaded("cataclysm")) {
            LEnderCataclysmCompat.send();
        }
        if (ModList.get().isLoaded("alexsmobs")) {
            AlexsMobsCompat.send();
        }
        if (ModList.get().isLoaded("born_in_chaos_v1")) {
            BornInChaosCompat.send();
        }
        if (ModList.get().isLoaded("celestisynth")) {
            CelestisynthCompat.send();
        }
        if (ModList.get().isLoaded("meetyourfight")) {
            MeetYourFightCompat.send();
        }
        if (ModList.get().isLoaded("epicfight")) {
            EpicFightCompat.send();
        }
        if (ModList.get().isLoaded("epicsamurai")) {
            EpicSamuraiCompat.send();
        }
        if (ModList.get().isLoaded("aquamirae")) {
            AquamiraeCompat.send();
        }
        if (ModList.get().isLoaded("forbidden_arcanus")) {
            ForbiddenArcanusCompat.send();
        }
        if (ModList.get().isLoaded("twilightforest")) {
            TwilightForestCompat.send();
        }
        if (ModList.get().isLoaded("undergarden")) {
            UndergardenCompat.send();
        }
    }
}
