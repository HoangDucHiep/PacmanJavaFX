package utc.hiep.pacmanjavafx.model;

import javafx.animation.AnimationTimer;

public class Timer {
    private static final long ONE_SECOND = 1_000_000_000; // One second in nanoseconds

    private long tick;                          //tick time
    private long secondClock;                   //second time

    private long lastUpdate;                    //used for tracking time
    public Timer() {
        tick = 0;
        lastUpdate = 0;
    }

    /**
     * @param now current time in nanoseconds
     * update tick to increase by 1 second each time
     */
    public void updateTimer(long now) {
        if(now - lastUpdate > ONE_SECOND) {
            secondClock++;
            lastUpdate = now;
        }
        tick++;
    }

    public long getTick() {
        return tick;
    }

    public long getSecondTimer() {
        return secondClock;
    }
}
