package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.model.level.GameModel.*;

public class GameLevel {
    private int level;

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

    private Pacman pacman;
    private World world;
    private Data data;


    public GameLevel() {
        this.level = 1;
        data = new Data(level, false, RAW_LEVEL_DATA[level - 1]);

        this.pacman = new Pacman("PACMAN");
        this.world = PacmanMap.createPacManWorld();
        setUpPacman();
    }


    private void setUpPacman() {
        pacman.setDefaultSpeed((float) PPS_AT_100_PERCENT / FPS);
        pacman.setPercentageSpeed(data.pacSpeedPercentage);
        pacman.placeAtTile(PacmanMap.PAC_POSITION);

    }

    public Pacman getPacman() {
        return pacman;
    }

    public World getWorld() {
        return world;
    }


    public void update() {

    }



}
