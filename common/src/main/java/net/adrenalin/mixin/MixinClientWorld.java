package net.adrenalin.mixin;

import net.adrenalin.util.ClientTimeDeacceleratable;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public class MixinClientWorld extends MixinWorld implements ClientTimeDeacceleratable {
    private double prevTimeFactor = getTimeFactor();

    @Override
    public double getPrevTimeFactor() {
        return prevTimeFactor;
    }

    @Override
    public void setTimeFactor(double factor) {
        prevTimeFactor = getTimeFactor();
        super.setTimeFactor(factor);
    }
}
