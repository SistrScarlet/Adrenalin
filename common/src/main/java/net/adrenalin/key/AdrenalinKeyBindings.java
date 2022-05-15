package net.adrenalin.key;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.adrenalin.AdrenalinMod;
import net.adrenalin.config.AdrenalinConfig;
import net.adrenalin.network.SparkAdrenalinPacket;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class AdrenalinKeyBindings {
    public static final KeyBinding SPARK_ADRENALIN = new KeyBinding(
            AdrenalinMod.MOD_ID + ".key.spark_adrenalin", GLFW.GLFW_KEY_R, KeyBinding.MOVEMENT_CATEGORY);
    public static int cool;

    public static void init() {
        register(SPARK_ADRENALIN);
    }

    public static void tick() {
        cool--;
        if (cool < 0 && SPARK_ADRENALIN.isPressed()) {
            SparkAdrenalinPacket.sendC2S();
            cool = AdrenalinConfig.getINSTANCE().getAdrenalinDecay() + 20 * 5;
        }
    }

    private static void register(KeyBinding keyBinding) {
        KeyMappingRegistry.register(keyBinding);
    }

}
