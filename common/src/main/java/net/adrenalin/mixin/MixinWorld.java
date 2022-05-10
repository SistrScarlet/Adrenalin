package net.adrenalin.mixin;

import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public class MixinWorld implements TimeDeacceleratable {
    private double timeDelta = 0.0;
    private double timeFactor = 1.0;

    @Override
    public void setTimeDelta(double delta) {
        this.timeDelta = MathHelper.clamp(delta, 0.0, 2.0);
    }

    @Override
    public double getTimeDelta() {
        return this.timeDelta;
    }

    @Override
    public void setTimeFactor(double factor) {
        this.timeFactor = MathHelper.clamp(factor, 1.0 / 20.0, 1.0);
    }

    @Override
    public double getTimeFactor() {
        return this.timeFactor;
    }
}
