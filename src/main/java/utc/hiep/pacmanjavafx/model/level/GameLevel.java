package utc.hiep.pacmanjavafx.model.level;

public class GameLevel {
    private int level;
    private int score;
    private int life;

    public record Data(
            int number, // Level number, starts at 1.
            boolean demoLevel,
            byte pacSpeedPercentage, // Relative Pac-Man speed (percentage of base speed).
            byte ghostSpeedPercentage, // Relative ghost speed when hunting or scattering.
            byte ghostSpeedTunnelPercentage, // Relative ghost speed inside tunnel.
            byte elroy1DotsLeft,//  Number of pellets left when Blinky becomes "Cruise Elroy" grade 1.
            byte elroy1SpeedPercentage, // Relative speed of Blinky being "Cruise Elroy" grade 1.
            byte elroy2DotsLeft, // Number of pellets left when Blinky becomes "Cruise Elroy" grade 2.
            byte elroy2SpeedPercentage, //Relative speed of Blinky being "Cruise Elroy" grade 2.
            byte pacSpeedPoweredPercentage, // Relative speed of Pac-Man in power mode.
            byte ghostSpeedFrightenedPercentage, // Relative speed of frightened ghost.
            byte pacPowerSeconds, // Number of seconds Pac-Man gets power.
            byte numFlashes, // Number of maze flashes at end of this level.
            byte intermissionNumber) // Number (1,2,3) of intermission scene played after this level (0=no intermission).
    {
        public Data(int number, boolean demoLevel, byte[] data) {
            this(number, demoLevel, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8],
                    data[9], data[10], data[11]);
        }
    }

    final byte[][] RAW_LEVEL_DATA = {
            /* 1*/ { 80, 75, 40,  20,  80, 10,  85,  90, 50, 6, 5, 0},
            /* 2*/ { 90, 85, 45,  30,  90, 15,  95,  95, 55, 5, 5, 1},
            /* 3*/ { 90, 85, 45,  40,  90, 20,  95,  95, 55, 4, 5, 0},
            /* 4*/ { 90, 85, 45,  40,  90, 20,  95,  95, 55, 3, 5, 0},
            /* 5*/ {100, 95, 50,  40, 100, 20, 105, 100, 60, 2, 5, 2},
            /* 6*/ {100, 95, 50,  50, 100, 25, 105, 100, 60, 5, 5, 0},
            /* 7*/ {100, 95, 50,  50, 100, 25, 105, 100, 60, 2, 5, 0},
            /* 8*/ {100, 95, 50,  50, 100, 25, 105, 100, 60, 2, 5, 0},
            /* 9*/ {100, 95, 50,  60, 100, 30, 105, 100, 60, 1, 3, 3},
            /*10*/ {100, 95, 50,  60, 100, 30, 105, 100, 60, 5, 5, 0},
            /*11*/ {100, 95, 50,  60, 100, 30, 105, 100, 60, 2, 5, 0},
            /*12*/ {100, 95, 50,  80, 100, 40, 105, 100, 60, 1, 3, 0},
            /*13*/ {100, 95, 50,  80, 100, 40, 105, 100, 60, 1, 3, 3},
            /*14*/ {100, 95, 50,  80, 100, 40, 105, 100, 60, 3, 5, 0},
            /*15*/ {100, 95, 50, 100, 100, 50, 105, 100, 60, 1, 3, 0},
            /*16*/ {100, 95, 50, 100, 100, 50, 105, 100, 60, 1, 3, 0},
            /*17*/ {100, 95, 50, 100, 100, 50, 105,   0,  0, 0, 0, 3},
            /*18*/ {100, 95, 50, 100, 100, 50, 105, 100, 60, 1, 3, 0},
            /*19*/ {100, 95, 50, 120, 100, 60, 105,   0,  0, 0, 0, 0},
            /*20*/ {100, 95, 50, 120, 100, 60, 105,   0,  0, 0, 0, 0},
            /*21*/ { 90, 95, 50, 120, 100, 60, 105,   0,  0, 0, 0, 0},
    };


}
