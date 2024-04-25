package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.model.level.GameModel.*;
import static utc.hiep.pacmanjavafx.lib.Global.*;

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
    private Ghost testGhost;


    public GameLevel() {
        this.level = 1;
        data = new Data(level, false, RAW_LEVEL_DATA[level - 1]);

        this.pacman = new Pacman("PACMAN");
        this.world = PacmanMap.createPacManWorld();
        this.testGhost = new Ghost(PINK_GHOST, "Pinky");
        setUpPacman();
        setUpGhost();
    }


    private void setUpPacman() {
        pacman.setDefaultSpeed((float) PPS_AT_100_PERCENT / FPS);
        pacman.setPercentageSpeed(data.pacSpeedPercentage);
        pacman.placeAtTile(PacmanMap.PAC_POSITION);
    }


    private void setUpGhost() {
        testGhost.setHouse(world.house());
        testGhost.setDefaultSpeed((float) PPS_AT_100_PERCENT / FPS);
        testGhost.setPercentageSpeed((byte) data.ghostSpeedPercentage);
        testGhost.setRevivalPosition(PacmanMap.HOUSE_LEFT_SEAT);
        testGhost.placeAtTile(testGhost.getRevivalPosition());
        testGhost.setMovingDir(Direction.UP);
    }

    public Pacman getPacman() {
        return pacman;
    }

    public World getWorld() {
        return world;
    }

    public Ghost getTestGhost() {
        return testGhost;
    }


    public void update() {
        moveLockedGhost();
    }



    private void moveGhost() {
        iVector2D currentTile = testGhost.atTile();


        /* Handle turn back instantly */
//        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
//            pacman.turnBackInstantly();
//            return;
//        }

        /*  handle ghost be blocked by wall or smth */
        if(!testGhost.canAccessTile(testGhost.tilesAhead(1), world) && testGhost.offset().almostEquals(fVector2D.ZERO, testGhost.currentSpeed(),  testGhost.currentSpeed())) {
            if(!testGhost.isStanding()) {
                testGhost.placeAtTile(currentTile.toFloatVec());
                testGhost.standing();
            }
        }

        /*  handle ghost at intersection */
        else if(world.isIntersection(currentTile)) {
            //if ghost haven't aligned to tile, but almost aligned, then aligned it
            if(testGhost.isNewTileEntered() && testGhost.offset().almostEquals(fVector2D.ZERO, testGhost.currentSpeed(), testGhost.currentSpeed())) {
                testGhost.placeAtTile(currentTile.toFloatVec());
            }
        }

        //Handle if ghost gothrough portal
        else if(world.belongsToPortal(currentTile)) {
            if(!world.belongsToPortal(testGhost.tilesAhead(1))) {
                iVector2D teleportTo = world.portals().otherTunnel(currentTile);
                testGhost.placeAtTile(teleportTo.toFloatVec());
            }
        }

        /* Handle if ghost be blocked in next turn, it'll keep moving in current direction*/
        if(testGhost.isAlignedToTile()) {
            testGhost.setNextDir(ghostDetermineNextDir());

            if(testGhost.canAccessTile(currentTile.plus(testGhost.nextDir().vector()), world)) {
                testGhost.setMovingDir(testGhost.nextDir());
            }
        }


        // If ghost is not standing, it can move :)))
        if(!testGhost.isStanding()) {
            testGhost.move();
        }
    }


    private void moveLockedGhost() {
        testGhost.setPercentageSpeed((byte) 50);
        fVector2D currentTile = halfTileLeftOf(testGhost.atTile().x(), testGhost.atTile().y());
        System.out.println(currentTile);
        System.out.println(testGhost.getRevivalPosition());

        if(!currentTile.equals(testGhost.getRevivalPosition())) {
            testGhost.setNextDir(testGhost.movingDir().opposite());
            testGhost.turnBackInstantly();
        }

        testGhost.move();

    }


    private Direction ghostDetermineNextDir() {
        Direction nextDir = Direction.randomDirection(testGhost.movingDir());
        while(!testGhost.canAccessTile(testGhost.tilesAhead(1, nextDir), world) || testGhost.movingDir().opposite().equals(nextDir)) {
            nextDir = nextDir.nextClockwise();
        }
        return nextDir;
    }



}
