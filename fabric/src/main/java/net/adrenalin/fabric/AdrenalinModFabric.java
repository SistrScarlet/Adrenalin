package net.adrenalin.fabric;

import net.adrenalin.AdrenalinMod;
import net.fabricmc.api.ModInitializer;

public class AdrenalinModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AdrenalinMod.init();
    }
}
