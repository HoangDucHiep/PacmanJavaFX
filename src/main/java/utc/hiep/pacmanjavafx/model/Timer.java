package utc.hiep.pacmanjavafx.model;

import javafx.animation.AnimationTimer;

public class Timer {
    private static final long ONE_SECOND = 1_000_000_000; // One second in nanoseconds

    private long tick;              //timer

    private long lastUpdate;
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
            tick++;
            lastUpdate = now;
        }
    }

    public long getTick() {
        return tick;
    }
}
