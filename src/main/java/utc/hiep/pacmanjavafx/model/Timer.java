package utc.hiep.pacmanjavafx.model;

public class Timer {
    public static final long ONE_SECOND = 1_000_000_000; // One second in nanoseconds
    private static final long ONE_MILISECOND = 1_000; // One second in milisecond

    private long tick;                          //tick time
    private double second;                   //second time

    private long lastUpdate;                    //used for tracking time
    private double delta;
    private boolean isPaused;

    public Timer() {
        tick = 0;
        isPaused = false;
    }

    /**
     * @param now current time in nanoseconds
     * update tick to increase by 1 second each time
     */
    public void updateTimer() {

        long now = System.nanoTime();

        if(isPaused) {return;}
        if(lastUpdate == 0) {lastUpdate = now; return;}

        delta += now - lastUpdate;
        while (delta >= (10 * ONE_MILISECOND)) {
            second += delta / ONE_SECOND;
            delta = 0;
        }

        lastUpdate = now;
        tick++;
    }

    public long ticks() {
        return tick;
    }

    public double seconds() {
        return second;
    }

    public void switchPause() {
        lastUpdate = System.nanoTime();
        isPaused = !isPaused;
    }

    public void reset() {
        tick = 0;
        second = 0;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
