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
        movePacman();
        handlePacmanEatFoot();
    }


    public void movePacman() {
        iVector2D currentTile = pacman.atTile();

        /* Handle turn back instantly */
        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
            pacman.turnBackInstantly();
            return;
        }

        /*  handle pacman be blocked by wall or smth */
        if(!pacman.canAccessTile(pacman.tilesAhead(1), world) && pacman.offset().almostEquals(fVector2D.ZERO, pacman.currentSpeed(),  pacman.currentSpeed())) {
            if(!pacman.isStanding()) {
                pacman.placeAtTile(currentTile.toFloatVec());
                pacman.standing();
            }
        }
        /*  handle pacman at intersection */
        else if(world.isIntersection(currentTile)) {
            //if pacman haven't aligned to tile, but almost aligned, then aligned it
            if(pacman.isNewTileEntered() && pacman.offset().almostEquals(fVector2D.ZERO, pacman.currentSpeed(), pacman.currentSpeed())) {
                pacman.placeAtTile(currentTile.toFloatVec());
            }
        }
        //Handle if pacman gothrough portal
        else if(world.belongsToPortal(currentTile)) {
            if(!world.belongsToPortal(pacman.tilesAhead(1))) {
                iVector2D teleportTo = world.portals().otherTunnel(currentTile);
                pacman.placeAtTile(teleportTo.toFloatVec());
            }
        }

        /* Handle if pacman be blocked in next turn, it'll keep moving in current direction*/
        if(pacman.isAlignedToTile()) {
            if(pacman.canAccessTile(currentTile.plus(pacman.nextDir().vector()), world)) {
                pacman.setMovingDir(pacman.nextDir());
            }
        }


        // If pacman is not standing, it can move :)))
        if(!pacman.isStanding()) {
            pacman.move();
        }
    }


    private void handlePacmanEatFoot() {
        iVector2D currentTile = pacman.atTile();
        if(world.hasFoodAt(currentTile) && !world.hasEatenFoodAt(currentTile)) {
            world.removeFood(currentTile);
        }
    }
}
