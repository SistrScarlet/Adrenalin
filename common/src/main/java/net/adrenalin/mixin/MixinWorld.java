package net.adrenalin.mixin;

import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public class MixinWorld implements TimeDeacceleratable {
    protected float timeDelta = 0.0f;
    protected float timeFactor = 1.0f;

    @Override
    public void setTimeDelta(float delta) {
        this.timeDelta = MathHelper.clamp(delta, 0.0f, 2.0f);
    }

    @Override
    public float getTimeDelta() {
        return this.timeDelta;
    }

    @Override
    public void setTimeFactor(float factor) {
        this.timeFactor = MathHelper.clamp(factor, 1.0f / 20.0f, 1.0f);
    }

    @Override
    public float getTimeFactor() {
        return this.timeFactor;
    }
}
