package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.GhostState;
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
        testGhost.setTargetTile(PacmanMap.SCATTER_TARGET_LEFT_UPPER_CORNER);
        testGhost.placeAtTile(testGhost.getRevivalPosition());
        testGhost.setMovingDir(Direction.UP);
        testGhost.setState(GhostState.LEAVING_HOUSE);
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
        if(testGhost.getState() == GhostState.LEAVING_HOUSE) {
            ghostLeavingHouse();
        } else
            moveScatterGhost();
        //moveLockedGhost();
        //ghostLeavingHouse();
    }



    private void moveGhost() {
        iVector2D currentTile = testGhost.atTile();


        /* Handle turn back instantly */
//        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
//            pacman.turnBackInstantly();
//            return;
//        }

        Direction nextDir = testGhost.movingDir();


        /*  handle ghost be blocked by wall or smth */
        if(!testGhost.canAccessTile(testGhost.tilesAhead(1), world) && testGhost.offset().almostEquals(fVector2D.ZERO, testGhost.currentSpeed(),  testGhost.currentSpeed())) {
            if(!testGhost.isStanding()) {
                testGhost.placeAtTile(currentTile.toFloatVec());
                testGhost.standing();
                nextDir = ghostDetermineNextDir();
            }
        }

        /*  handle ghost at intersection */
        else if(world.isIntersection(currentTile)) {
            //if ghost haven't aligned to tile, but almost aligned, then aligned it
            if(testGhost.isNewTileEntered() && testGhost.offset().almostEquals(fVector2D.ZERO, testGhost.currentSpeed(), testGhost.currentSpeed())) {
                testGhost.placeAtTile(currentTile.toFloatVec());
                nextDir = ghostDetermineNextDir();
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


        // If ghost is not standing, it can move :)))
        if(!testGhost.isStanding()) {
            testGhost.move();
        }
    }


    private void moveScatterGhost() {
        moveGhost();
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


    private void ghostLeavingHouse() {
        testGhost.setPercentageSpeed((byte) 50);
        fVector2D houseEntryPosition = testGhost.house.door().entryPosition();
        if (testGhost.posY() <= houseEntryPosition.y()) {
            // has raised and is outside house
            testGhost.setPosition(houseEntryPosition);
            testGhost.setMovingDir(Direction.LEFT);
            testGhost.setNextDir(Direction.LEFT);
            testGhost.newTileEntered = false; // force moving left until new tile is entered
            testGhost.setState(GhostState.SCATTER);
            return;
        }
        // move inside house
        float centerX = testGhost.center().x();
        float houseCenterX = testGhost.house.center().x();
        if (differsAtMost(0.5f * testGhost.currentSpeed(), centerX, houseCenterX)) {
            // align horizontally and raise
            testGhost.setPosX(houseCenterX - HALF_TILE_SIZE);
            testGhost.setMovingDir(Direction.UP);
            testGhost.setNextDir(Direction.UP);
        } else {
            // move sidewards until center axis is reached
            testGhost.setMovingDir(centerX < houseCenterX ? Direction.RIGHT : Direction.LEFT);
            testGhost.setNextDir(centerX < houseCenterX ? Direction.RIGHT : Direction.LEFT);
        }
        //testGhost.setSpeed(speedInsideHouse);
        testGhost.move();
//        if (pac.powerTimer().isRunning() && !pac.victims().contains(this)) {
//            updateFrightenedAnimation(pac);
//        } else {
//            selectAnimation(ANIM_GHOST_NORMAL);
//        }
    }


    private Direction ghostDetermineNextDir() {
//        Direction nextDir = Direction.randomDirection(testGhost.movingDir());
//        while(!testGhost.canAccessTile(testGhost.tilesAhead(1, nextDir), world) || testGhost.movingDir().opposite().equals(nextDir)) {
//            nextDir = nextDir.nextClockwise();
//        }
//        return nextDir;


        /* scatter state */

        iVector2D currentTile = testGhost.atTile();
        Direction currentDir = testGhost.movingDir();
        Direction tempNextDir = testGhost.movingDir();

        Direction nextDir = tempNextDir;

        float minDistance = Float.MAX_VALUE;

        for(int i = 0; i < 4; i++) {
            tempNextDir = tempNextDir.nextClockwise();
            System.out.println(tempNextDir);
            iVector2D nextTile = testGhost.tilesAhead(1, tempNextDir);
            if(!testGhost.canAccessTile(nextTile, world) || currentDir.opposite().equals(tempNextDir)) {
                System.out.println("Continue");
                continue;
            }



            iVector2D target = PacmanMap.SCATTER_TARGET_RIGHT_UPPER_CORNER;

            float distance = nextTile.euclideanDistance(target);
            if(distance < minDistance) {
                minDistance = distance;
                nextDir = tempNextDir;
            }
        }
        return nextDir;
    }



}
