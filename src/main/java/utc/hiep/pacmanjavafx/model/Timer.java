package utc.hiep.pacmanjavafx.model;

import javafx.animation.AnimationTimer;

public class Timer {
    private static final long ONE_SECOND = 1_000_000_000; // One second in nanoseconds
    private static final long ONE_MILISECOND = 1_000; // One second in milisecond

    private long tick;                          //tick time
    private double second;                   //second time

    private long lastUpdate;                    //used for tracking time
    private double delta;
    private boolean pause;

    public Timer(long lastUpdate) {
        tick = 0;
        lastUpdate = 0;
        pause = false;
    }

    /**
     * @param now current time in nanoseconds
     * update tick to increase by 1 second each time
     */
    public void updateTimer(long now) {

        if(pause) {lastUpdate = 0; return;}

        if (lastUpdate > 0) {
            delta += now - lastUpdate;
        }

        while (delta >= (10 * ONE_MILISECOND)) {
            second += delta / ONE_SECOND;
            delta = 0;
        }

        lastUpdate = now;
        tick++;
    }

    public long getTick() {
        return tick;
    }

    public int getSecondTimer() {
        return (int) second;
    }

    public void switchPause(long updateTime) {
        lastUpdate = updateTime;
        pause = !pause;
    }

    public void setLastTick(long lastTick) {
        this.lastUpdate = lastTick;
    }
}
