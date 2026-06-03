package com.nightwielder.apothiccompat;

import com.mojang.logging.LogUtils;
import com.nightwielder.apothiccompat.compat.AlexsMobsCompat;
import com.nightwielder.apothiccompat.compat.AquamiraeCompat;
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
import net.minecraftforge.event.RegisterCommandsEvent;
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
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ReloadCommand.register(event.getDispatcher());
    }

    // Apotheosis's loot_category_override IMC accepts only Map.Entry<Item, String> (item + category);
    // there is no slot parameter. Equipment slot tooltip lines (e.g. literal "{mainhand}") come from
    // vanilla's item.modifiers.<slot> lang keys or a third party tooltip mod (Curios, etc.), not from
    // anything Apothic Compat or Apotheosis renders. Do not try to "fix" it by changing the IMC payload.
    //
    // Apotheosis stores overrides last wins (its TYPE_OVERRIDES map is a plain put), so send order is
    // precedence order. UniversalCompat runs first as the speed based default for every item; the per mod
    // modules run next so their explicit ranged/shield/utility decisions overwrite that default; the config
    // loads last so a user's per item override beats everything.
    private void sendCategoryOverrides(InterModEnqueueEvent event) {
        if (!ModList.get().isLoaded("apotheosis")) {
            LOGGER.info("Apotheosis not present; skipping all compat modules.");
            return;
        }
        dispatchModules();
        ApothicCompatConfig.load();
    }

    // Some mods (e.g. Mowzie's Mobs) finalize weapon attack damage/speed from config during deferred work
    // enqueued at FMLCommonSetupEvent, which completes after the InterModEnqueueEvent scan above. The
    // first pass live read then sees stale stats and can miscategorize those items. FMLLoadCompleteEvent is
    // the last mod loading event, after all such deferred init, so rerun the same dispatch here. The IMC
    // window is closed by now, so this pass writes Apotheosis's override maps directly (see
    // ApothicCompatConfig.reapply), the same runtime path /apothiccompat reload uses.
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
