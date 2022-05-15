package net.adrenalin.forge;

import dev.architectury.platform.forge.EventBuses;
import net.adrenalin.AdrenalinMod;
import net.adrenalin.config.AdrenalinClothConfigBuilder;
import net.adrenalin.setup.ClientSetup;
import net.adrenalin.setup.ModSetup;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AdrenalinMod.MOD_ID)
public class AdrenalinModForge {

    public AdrenalinModForge() {
        EventBuses.registerModEventBus(AdrenalinMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AdrenalinMod.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initCommon);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) ->
                        AdrenalinClothConfigBuilder.getConfigScreen(parent)));
    }

    public void initCommon(FMLCommonSetupEvent event) {
        ModSetup.init();
    }

    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.init();
    }
}
