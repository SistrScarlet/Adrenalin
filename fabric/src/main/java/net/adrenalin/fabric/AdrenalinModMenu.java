package net.adrenalin.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.adrenalin.config.AdrenalinClothConfigBuilder;

public class AdrenalinModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AdrenalinClothConfigBuilder::getConfigScreen;
    }
}
