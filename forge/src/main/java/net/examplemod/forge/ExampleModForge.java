package net.examplemod.forge;

import dev.architectury.platform.forge.EventBuses;
import net.adrenalin.AdrenalinMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AdrenalinMod.MOD_ID)
public class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(AdrenalinMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AdrenalinMod.init();
    }
}
