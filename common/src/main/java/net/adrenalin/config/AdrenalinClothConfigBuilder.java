package net.adrenalin.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;

public class AdrenalinClothConfigBuilder {

    //ビルドせずConfigBuilderを渡すと、Forge側でコンパイルできず01んだ
    public static Screen getConfigScreen(@Nullable Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(new TranslatableText("title.adrenalin.config"));
        if (parent != null) {
            builder.setParentScreen(parent);
        }
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
                .setMin(1.0f / 100)
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

        general.addEntry(entryBuilder.startBooleanToggle(
                        new TranslatableText("option.adrenalin.chunk_base_time_stasis"), config.isChunkBaseTimeStasis())
                .setDefaultValue(false)
                .setTooltip(new TranslatableText("option.adrenalin.chunk_base_time_stasis.tooltip"))
                .setSaveConsumer(config::setChunkBaseTimeStasis)
                .build());

        return builder.build();
    }

}
