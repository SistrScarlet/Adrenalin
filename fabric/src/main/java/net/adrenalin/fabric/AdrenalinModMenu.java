package net.adrenalin.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.adrenalin.config.AdrenalinConfig;
import net.minecraft.text.TranslatableText;

public class AdrenalinModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("title.adrenalin.config"));
            builder.setSavingRunnable(() -> AdrenalinConfig.getINSTANCE().save());

            var entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.adrenalin.adrenalin"));

            var config = AdrenalinConfig.getINSTANCE();

            general.addEntry(entryBuilder.startBooleanToggle(
                            new TranslatableText("option.adrenalin.enable"), config.isEnable())
                    .setDefaultValue(true)
                    .setTooltip(new TranslatableText("option.adrenalin.enable.tooltip"))
                    .setSaveConsumer(config::setEnable)
                    .build());

            general.addEntry(entryBuilder.startFloatField(
                            new TranslatableText("option.adrenalin.max_time_stasis_factor"), config.getMaxTimeStasisFactor())
                    .setDefaultValue(100.0f)
                    .setMax(1.0f)
                    .setMin(0.01f)
                    .setTooltip(new TranslatableText("option.adrenalin.max_time_stasis_factor.tooltip"))
                    .setSaveConsumer(config::setMaxTimeStasisFactor)
                    .build());

            general.addEntry(entryBuilder.startIntField(
                            new TranslatableText("option.adrenalin.adrenalin_decay"), config.getAdrenalinDecay())
                    .setDefaultValue(100)
                    .setMax(1000)
                    .setMin(0)
                    .setTooltip(new TranslatableText("option.adrenalin.adrenalin_decay.tooltip"))
                    .setSaveConsumer(config::setAdrenalinDecay)
                    .build());

            general.addEntry(entryBuilder.startFloatField(
                            new TranslatableText("option.adrenalin.low_health_threshold"), config.getLowHealthThreshold())
                    .setDefaultValue(0.5f)
                    .setMax(1.0f)
                    .setMin(0.0f)
                    .setTooltip(new TranslatableText("option.adrenalin.low_health_threshold.tooltip"))
                    .setSaveConsumer(config::setLowHealthThreshold)
                    .build());

            general.addEntry(entryBuilder.startFloatField(
                            new TranslatableText("option.adrenalin.continuous_damage_threshold"), config.getContinuousDamageThreshold())
                    .setDefaultValue(0.2f)
                    .setMax(1.0f)
                    .setMin(0.0f)
                    .setTooltip(new TranslatableText("option.adrenalin.continuous_damage_threshold.tooltip"))
                    .setSaveConsumer(config::setContinuousDamageThreshold)
                    .build());

            general.addEntry(entryBuilder.startIntField(
                            new TranslatableText("option.adrenalin.continuous_damage_decay"), config.getContinuousDamageDecay())
                    .setDefaultValue(100)
                    .setMax(1000)
                    .setMin(0)
                    .setTooltip(new TranslatableText("option.adrenalin.continuous_damage_decay.tooltip"))
                    .setSaveConsumer(config::setContinuousDamageDecay)
                    .build());

            return builder.build();
        };
    }
}
