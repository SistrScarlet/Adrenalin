package net.adrenalin.util;

import net.minecraft.util.math.MathHelper;

public class TimeDeacceleratableImpl implements TimeDeacceleratable {
    private float tickDelta = 0;
    private int tickInterval = 1;

    @Override
    public void setTickDelta(float delta) {
        //フェイルセーフ
        this.tickDelta = MathHelper.clamp(delta, 0.0f, 2.0f);
    }

    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }

    @Override
    public void setTickInterval(int tickInterval) {
        //フェイルセーフ
        this.tickInterval = MathHelper.clamp(tickInterval, 1, 100);
    }

    @Override
    public int getTickInterval() {
        return this.tickInterval;
    }
}
