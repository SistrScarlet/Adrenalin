package net.adrenalin.fabric;

import net.adrenalin.AdrenalinMod;
import net.adrenalin.setup.ClientSetup;
import net.adrenalin.setup.ModSetup;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class AdrenalinModFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        AdrenalinMod.init();
        ModSetup.init();
    }

    @Override
    public void onInitializeClient() {
        ClientSetup.init();
    }
}
