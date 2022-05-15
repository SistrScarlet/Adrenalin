package net.adrenalin.util;

public interface TimeDeacceleratable {
    void setTickDelta(float delta);

    float getTickDelta();

    void setTickInterval(int tickInterval);

    int getTickInterval();

}
