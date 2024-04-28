package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.GhostState;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;


import static utc.hiep.pacmanjavafx.model.entity.GhostState.*;
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
    private Timer huntingTimer;


    public GameLevel() {
        this.level = 1;
        data = new Data(level, false, RAW_LEVEL_DATA[level - 1]);

        this.pacman = new Pacman("PACMAN");
        this.world = PacmanMap.createPacManWorld();
        this.testGhost = new Ghost(PINK_GHOST, "Pinky", world);
        huntingTimer = new Timer();
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
        testGhost.setRevivalPosition(PacmanMap.HOUSE_LEFT_SEAT);
        testGhost.setTargetTile(PacmanMap.SCATTER_TARGET_LEFT_UPPER_CORNER);
        testGhost.placeAtTile(testGhost.getRevivalPosition());
        testGhost.placeAtTile(PacmanMap.HOUSE_LEFT_SEAT);
        testGhost.setMovingDir(Direction.UP);
        testGhost.setOutOfHouseSpeed((float) PPS_AT_100_PERCENT / FPS);
        testGhost.setDefaultSpeedInsideHouse((float) PPS_GHOST_INHOUSE / FPS);
        testGhost.setPercentageSpeed((byte) data.ghostSpeedPercentage);
    }




    public void update() {
        huntingTimer.updateTimer();
        testGhost.updateState();
        if(testGhost.state() == LOCKED) {
            moveLockedGhost();
        }else if(testGhost.state() == GhostState.LEAVING_HOUSE) {
            ghostLeavingHouse();
        } else
            moveScatterGhost();
        if((int)huntingTimer.getSecondTimer() > 10) {
            if(testGhost.state() == SCATTER) {
                testGhost.changeToHunter();
            }
            testGhost.setTargetTile(pacman.atTile());
        }
    }



    private void moveGhost() {
        iVector2D currentTile = testGhost.atTile();
        Direction nextDir = testGhost.movingDir();

        /*  handle ghost be blocked by wall or smth */
        if(!testGhost.canAccessTile(testGhost.tilesAhead(1), world) && testGhost.offset().almostEquals(fVector2D.ZERO, testGhost.currentSpeed(),  testGhost.currentSpeed())) {
            if(!testGhost.isStanding()) {
                testGhost.placeAtTile(currentTile.toFloatVec());
                nextDir = ghostDetermineNextDir();
            }
        }

        /*  handle ghost at intersection */
        else if(world.isIntersection(currentTile)) {
            //if ghost haven't aligned to tile, but almost aligned, then aligned it
            if(testGhost.isNewTileEntered() && testGhost.offset().almostEquals(fVector2D.ZERO, testGhost.currentSpeed(), testGhost.currentSpeed())) {
                testGhost.placeAtTile(currentTile.toFloatVec());
                System.out.println("call in intersection");
                nextDir = ghostDetermineNextDir();
                System.out.println(nextDir);
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
            testGhost.setNextDir(nextDir);

            if(testGhost.canAccessTile(currentTile.plus(testGhost.nextDir().vector()), world)) {
                testGhost.setMovingDir(testGhost.nextDir());
            }
        }
        testGhost.move();
    }


    private void moveScatterGhost() {
        testGhost.setPercentageSpeed(data.ghostSpeedPercentage);
        moveGhost();
    }


    private void moveLockedGhost() {
        fVector2D currentTile = halfTileLeftOf(testGhost.atTile().x(), testGhost.atTile().y());
        if(!currentTile.equals(testGhost.getRevivalPosition())) {
            testGhost.setNextDir(testGhost.movingDir().opposite());
            testGhost.turnBackInstantly();
        }

        testGhost.move();
    }


    private void ghostLeavingHouse() {
        testGhost.leaveHouse();
    }


    private Direction ghostDetermineNextDir() {
//        Direction nextDir = Direction.randomDirection(testGhost.movingDir());
//        while(!testGhost.canAccessTile(testGhost.tilesAhead(1, nextDir), world) || testGhost.movingDir().opposite().equals(nextDir)) {
//            nextDir = nextDir.nextClockwise();
//        }
//        return nextDir;


        /* scatter state */

        Direction currentDir = testGhost.movingDir();
        Direction tempNextDir = testGhost.movingDir();

        Direction nextDir = tempNextDir;


        float minDistance = Float.MAX_VALUE;
        iVector2D target = testGhost.targetTile().get();



        for(int i = 0; i < 4; i++) {
            iVector2D nextTile = testGhost.tilesAhead(1, tempNextDir);

            if(!testGhost.canAccessTile(nextTile, world) || currentDir.opposite().equals(tempNextDir)) {
                tempNextDir = tempNextDir.nextClockwise();
                continue;
            }

            float distance = nextTile.sqrEuclideanDistance(target);
            if(distance < minDistance) {
                minDistance = distance;
                nextDir = tempNextDir;
            } else if(distance == minDistance) {
                nextDir = tempNextDir.isHigherPriority(nextDir) ? tempNextDir : nextDir;
            }

            tempNextDir = tempNextDir.nextClockwise();
        }
        return nextDir;

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


}
