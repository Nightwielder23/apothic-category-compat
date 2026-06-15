package com.nightwielder.apothiccategorycompat;

import com.mojang.logging.LogUtils;
import com.nightwielder.apothiccategorycompat.compat.AlexsCavesCompat;
import com.nightwielder.apothiccategorycompat.compat.AlexsMobsCompat;
import com.nightwielder.apothiccategorycompat.compat.AquamiraeCompat;
import com.nightwielder.apothiccategorycompat.compat.BornInChaosCompat;
import com.nightwielder.apothiccategorycompat.compat.CelestisynthCompat;
import com.nightwielder.apothiccategorycompat.compat.DungeonsAndCombatCompat;
import com.nightwielder.apothiccategorycompat.compat.EpicFightCompat;
import com.nightwielder.apothiccategorycompat.compat.EpicFightNightfallCompat;
import com.nightwielder.apothiccategorycompat.compat.EpicFightResurrectionCompat;
import com.nightwielder.apothiccategorycompat.compat.ForbiddenArcanusCompat;
import com.nightwielder.apothiccategorycompat.compat.LEnderCataclysmCompat;
import com.nightwielder.apothiccategorycompat.compat.MariumsSoulslikeCompat;
import com.nightwielder.apothiccategorycompat.compat.MeetYourFightCompat;
import com.nightwielder.apothiccategorycompat.compat.TetraCompat;
import com.nightwielder.apothiccategorycompat.compat.TravelopticsCompat;
import com.nightwielder.apothiccategorycompat.compat.TwilightForestCompat;
import com.nightwielder.apothiccategorycompat.compat.UndergardenCompat;
import com.nightwielder.apothiccategorycompat.compat.UniversalCompat;
import com.nightwielder.apothiccategorycompat.compat.WeaponsOfMiraclesCompat;
import com.nightwielder.apothiccategorycompat.command.ReloadCommand;
import com.nightwielder.apothiccategorycompat.config.ApothicCategoryCompatConfig;
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

@Mod(ApothicCategoryCompat.MODID)
public class ApothicCategoryCompat {
    public static final String MODID = "apothic_category_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ApothicCategoryCompat(FMLJavaModLoadingContext context) {
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
        ApothicCategoryCompatConfig.loadAffixBlacklist();
        // Item tags are bound and the second pass has run by now, so reapply config overrides here: tag
        // overrides finally resolve and a user's per item overrides reclaim last-wins.
        ApothicCategoryCompatConfig.applyOverridesAtRuntime();
    }

    private void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            return;
        }
        if (!ModList.get().isLoaded("apotheosis")) {
            return;
        }
        ApothicCategoryCompatConfig.loadAffixBlacklist();
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
        ApothicCategoryCompatConfig.load();
    }

    // Some mods (e.g. Mowzie's Mobs) finalize attack stats in deferred init that runs after the IMC scan, so
    // the first pass reads stale stats and miscategorizes. FMLLoadCompleteEvent is the last load event, so
    // rerun the dispatch here, writing Apoth's override maps directly since the IMC window is closed.
    private void reapplyAfterDeferredInit(FMLLoadCompleteEvent event) {
        if (!ModList.get().isLoaded("apotheosis")) {
            return;
        }
        int changed = ApothicCategoryCompatConfig.reapply(this::dispatchModules);
        LOGGER.info("Apothic Category Compat second pass recategorized {} item(s) after deferred mod init.", changed);
    }

    // Shared dispatch for both passes. The active CompatImc sink (IMC for the first pass, runtime override
    // writes for the second) decides where results land, so the module code is identical.
    private void dispatchModules() {
        // UniversalCompat reads the categorization toggles, so refresh them before it runs.
        ApothicCategoryCompatConfig.loadSettings();
        UniversalCompat.send();
        if (ModList.get().isLoaded("tetra")) {
            TetraCompat.send();
        }
        if (ModList.get().isLoaded("wom")) {
            WeaponsOfMiraclesCompat.send();
        }
        if (ModList.get().isLoaded("cataclysm")) {
            LEnderCataclysmCompat.send();
        }
        if (ModList.get().isLoaded("aquamirae")) {
            AquamiraeCompat.send();
        }
        if (ModList.get().isLoaded("dungeons_and_combat")) {
            DungeonsAndCombatCompat.send();
        }
        if (ModList.get().isLoaded("soulsweapons")) {
            MariumsSoulslikeCompat.send();
        }
        if (ModList.get().isLoaded("born_in_chaos_v1")) {
            BornInChaosCompat.send();
        }
        if (ModList.get().isLoaded("celestisynth")) {
            CelestisynthCompat.send();
        }
        if (ModList.get().isLoaded("alexsmobs")) {
            AlexsMobsCompat.send();
        }
        if (ModList.get().isLoaded("alexscaves")) {
            AlexsCavesCompat.send();
        }
        if (ModList.get().isLoaded("forbidden_arcanus")) {
            ForbiddenArcanusCompat.send();
        }
        if (ModList.get().isLoaded("meetyourfight")) {
            MeetYourFightCompat.send();
        }
        if (ModList.get().isLoaded("epicfight")) {
            EpicFightCompat.send();
        }
        if (ModList.get().isLoaded("cdmoveset")) {
            EpicFightResurrectionCompat.send();
        }
        if (ModList.get().isLoaded("efn")) {
            EpicFightNightfallCompat.send();
        }
        if (ModList.get().isLoaded("traveloptics")) {
            TravelopticsCompat.send();
        }
        if (ModList.get().isLoaded("twilightforest")) {
            TwilightForestCompat.send();
        }
        if (ModList.get().isLoaded("undergarden")) {
            UndergardenCompat.send();
        }
    }
}
