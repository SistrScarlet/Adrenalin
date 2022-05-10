package net.adrenalin.util;

public interface TimeDeacceleratable {
    //doubleなのは計算誤差対策
    void setTimeDelta(double delta);

    double getTimeDelta();

    void setTimeFactor(double factor);

    double getTimeFactor();

}
