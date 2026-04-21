package com.nightwielder.apothiccompat;

import com.mojang.logging.LogUtils;
import com.nightwielder.apothiccompat.compat.AquamiraeCompat;
import com.nightwielder.apothiccompat.compat.ArmageddonCompat;
import com.nightwielder.apothiccompat.compat.CataclysmWeaponryCompat;
import com.nightwielder.apothiccompat.compat.DreadSteelCompat;
import com.nightwielder.apothiccompat.compat.DungeonsAndCombatCompat;
import com.nightwielder.apothiccompat.compat.EpicKnightsCompat;
import com.nightwielder.apothiccompat.compat.IronsSpellbooksCompat;
import com.nightwielder.apothiccompat.compat.LEnderCataclysmCompat;
import com.nightwielder.apothiccompat.compat.MowziesMobsCompat;
import com.nightwielder.apothiccompat.compat.SamuraiDynastyCompat;
import com.nightwielder.apothiccompat.compat.SimplySwordsCompat;
import com.nightwielder.apothiccompat.compat.SpartanShieldsCompat;
import com.nightwielder.apothiccompat.compat.SpartanWeaponryCompat;
import com.nightwielder.apothiccompat.compat.TetraCompat;
import com.nightwielder.apothiccompat.compat.UniversalCompat;
import com.nightwielder.apothiccompat.compat.WeaponsOfMiraclesCompat;
import com.nightwielder.apothiccompat.command.ReloadCommand;
import com.nightwielder.apothiccompat.config.ApothicCompatConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
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
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ReloadCommand.register(event.getDispatcher());
    }

    // Apotheosis's loot_category_override IMC accepts only Map.Entry<Item, String> (item + category);
    // there is no slot parameter. Equipment-slot tooltip lines (e.g. literal "{mainhand}") come from
    // vanilla's item.modifiers.<slot> lang keys or a third-party tooltip mod (Curios, etc.), not from
    // anything Apotheosis or this mod renders — do not try to "fix" it by changing the IMC payload.
    private void sendCategoryOverrides(InterModEnqueueEvent event) {
        if (!ModList.get().isLoaded("apotheosis")) {
            LOGGER.info("Apotheosis not present; skipping all compat modules.");
            return;
        }
        if (ModList.get().isLoaded("samurai_dynasty")) SamuraiDynastyCompat.send();
        if (ModList.get().isLoaded("dreadsteel")) DreadSteelCompat.send();
        if (ModList.get().isLoaded("tetra")) TetraCompat.send();
        if (ModList.get().isLoaded("weaponsofmiracles")) WeaponsOfMiraclesCompat.send();
        if (ModList.get().isLoaded("cataclysmweaponry")) CataclysmWeaponryCompat.send();
        if (ModList.get().isLoaded("cataclysm")) LEnderCataclysmCompat.send();
        if (ModList.get().isLoaded("simplyswords")) SimplySwordsCompat.send();
        if (ModList.get().isLoaded("irons_spellbooks")) IronsSpellbooksCompat.send();
        if (ModList.get().isLoaded("aquamirae")) AquamiraeCompat.send();
        if (ModList.get().isLoaded("mowziesmobs")) MowziesMobsCompat.send();
        if (ModList.get().isLoaded("spartanshields")) SpartanShieldsCompat.send();
        if (ModList.get().isLoaded("dungeons_and_combat")) DungeonsAndCombatCompat.send();
        if (ModList.get().isLoaded("spartanweaponry")) SpartanWeaponryCompat.send();
        if (ModList.get().isLoaded("armageddon")) ArmageddonCompat.send();
        if (ModList.get().isLoaded("epicknights")) EpicKnightsCompat.send();
        UniversalCompat.send();
        ApothicCompatConfig.load();
    }
}
