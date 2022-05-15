package net.adrenalin.util;

public class ChunkTimeStasisDate implements TimeDeacceleratable {
    private final TimeDeacceleratable timeDeacceleratable = new TimeDeacceleratableImpl();
    public float prevAdrenalin = -1.0f;
    public float adrenalin = 1.0f;

    @Override
    public void setTickDelta(float delta) {
        this.timeDeacceleratable.setTickDelta(delta);
    }

    @Override
    public float getTickDelta() {
        return this.timeDeacceleratable.getTickDelta();
    }

    @Override
    public void setTickInterval(int tickInterval) {
        this.timeDeacceleratable.setTickInterval(tickInterval);
    }

    @Override
    public int getTickInterval() {
        return this.timeDeacceleratable.getTickInterval();
    }
}
