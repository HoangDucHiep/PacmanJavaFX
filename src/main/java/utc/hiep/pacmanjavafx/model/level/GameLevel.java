package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.event.GameEvent;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.GhostState;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.GhostHouseControl;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static utc.hiep.pacmanjavafx.lib.Direction.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.*;
import static utc.hiep.pacmanjavafx.lib.Global.*;

public class GameLevel {
    private int levelNum;

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

    private LevelState levelState;
    private GameEvent gameEvent;
    private Pacman pacman;
    private World world;
    private Data data;
    private final Ghost[] ghosts;
    private Timer huntingTimer;
    private Timer levelStateTimer;
    private Timer frightenedTimer;
    private byte huntingPhaseIndex;
    private byte totalNumGhostsKilled;
    private byte cruiseElroyState;
    private GhostHouseControl houseControl;



    public GameLevel() {
        this.levelNum = 1;
        data = new Data(levelNum, false, RAW_LEVEL_DATA[levelNum - 1]);
        levelState = LevelState.LEVEL_CREATED;
        levelStateTimer = new Timer();
        frightenedTimer = new Timer();

        this.pacman = new Pacman("PACMAN");
        this.world = PacmanMap.createPacManWorld();
        this.houseControl = new GhostHouseControl(levelNum);

        ghosts = Stream.of(GameModel.RED_GHOST, GameModel.PINK_GHOST, GameModel.CYAN_GHOST, GameModel.ORANGE_GHOST)
                .map(id -> new Ghost(id, ghostName(id))).toArray(Ghost[]::new);

        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.reset();
            ghost.setHouse(world.house());
            ghost.setFrightenedBehavior(g -> {
                byte relSpeed = data.ghostSpeedFrightenedPercentage;
                g.setPercentageSpeed(relSpeed);
                EntityMovement.randomMove(g, world);
            });
            ghost.setRevivalPosition(ghostRevivalPosition(ghost.id()));
            ghost.setOutOfHouseSpeed(GameModel.PPS_AT_100_PERCENT / (float) GameModel.FPS);
            ghost.setDefaultSpeedReturnHouse(GameModel.PPS_GHOST_RETURNING_HOME / (float) GameModel.FPS);
            ghost.setDefaultSpeedInsideHouse(GameModel.PPS_GHOST_INHOUSE / (float) GameModel.FPS);
            ghost.setDefaultSpeed(GameModel.PPS_AT_100_PERCENT / (float) GameModel.FPS);
            ghost.setPercentageSpeed(data.ghostSpeedPercentage);

            ///------
            ghost.placeAtTile(ghostRevivalPosition(ghost.id()));
            if(ghost.id() == GameModel.RED_GHOST) {
                ghost.placeAtTile(PacmanMap.HOUSE_DOOR_SEAT);
            }
            ghost.setMovingDir(ghostInhouseInitDir(ghost.id()));
        });

        pacman.hide();


        var forbiddenMovesAtTile = new HashMap<iVector2D, List<Direction>>();
        var up = List.of(UP);

        PacmanMap.PACMAN_RED_ZONE.forEach(tile -> forbiddenMovesAtTile.put(tile, up));
        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.setForbiddenMoves(forbiddenMovesAtTile);
            ghost.setHuntingBehavior(this::huntingBehavior);
            ghost.hide();
        });

        huntingTimer = new Timer();
        huntingPhaseIndex = 0;
        setUpPacman();

    }

    private void setUpPacman() {
        pacman.setDefaultSpeed((float) PPS_AT_100_PERCENT / FPS);
        pacman.setPercentageSpeed(data.pacSpeedPercentage);
        pacman.placeAtTile(PacmanMap.PAC_POSITION);
    }


    public void update() {
        if(levelState == LevelState.LEVEL_PAUSED) {
            return;
        }

        if(levelState == LevelState.LEVEL_CREATED){
            levelStateTimer.updateTimer();
            if((int) levelStateTimer.seconds() == 2) {
                pacman.show();
                Arrays.stream(ghosts).forEach(Ghost::show);
            }

            if((int) levelStateTimer.seconds() == 4) {
                levelState = LevelState.LEVEL_STARTED;
                levelStateTimer.reset();
            }
        }

        if(levelState == LevelState.LEVEL_STARTED) {
            updateLevelStartedStateEvent();
        }
    }


    private void updateLevelStartedStateEvent() {
        houseControl.unlockGhost(this);
        huntingTimer.updateTimer();
        updateChasingTargetPhase();
        
        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.update(pacman, world);
        });

        movePacman();
        handlePacmanEatFoot();
        handlePacAndGhostCollision();
    }




    private void huntingBehavior(Ghost ghost) {
        if (chasingPhase().isPresent() || ghost.id() == GameModel.RED_GHOST && cruiseElroyState > 0) {
            //chase pacman
            ghost.setHuntingBehavior(g -> {
                byte relSpeed = huntingSpeedPercentage(g);
                g.setPercentageSpeed(relSpeed);
                g.setTargetTile(chasingTarget(g.id()));
                EntityMovement.chaseTarget(g, world);
            });
        } else {
            //chase scatter
            ghost.setHuntingBehavior(g -> {
                byte relSpeed = huntingSpeedPercentage(g);
                g.setPercentageSpeed(relSpeed);
                g.setTargetTile(ghostScatterTarget(g.id()));
                EntityMovement.chaseTarget(g, world);
            });
        }
    }

    /**
     * @param ghost a ghost
     * @return relative speed of ghost in percent of the base speed
     */
    public byte huntingSpeedPercentage(Ghost ghost) {
        if (world.isTunnel(ghost.atTile())) {
            return data.ghostSpeedTunnelPercentage();
        }
        if (ghost.id() == GameModel.RED_GHOST && cruiseElroyState == 1) {
            return data.elroy1SpeedPercentage();
        }
        if (ghost.id() == GameModel.RED_GHOST && cruiseElroyState == 2) {
            return data.elroy2SpeedPercentage();
        }
        return data.ghostSpeedPercentage();
    }

    /**
     * @return (optional) index of current scattering phase <code>(0-3)</code>
     */
    public Optional<Integer> scatterPhase() {
        return isEven(huntingPhaseIndex) ? Optional.of(huntingPhaseIndex / 2) : Optional.empty();
    }

    /**
     * @return (optional) index of current chasing phase <code>(0-3)</code>
     */
    public Optional<Integer> chasingPhase() {
        return isOdd(huntingPhaseIndex) ? Optional.of(huntingPhaseIndex / 2) : Optional.empty();
    }


    public static String SCATTERING = "Scattering";
    public static String CHASING = "Chasing";
    public String currentChasingTargetPhaseName() {
        return isEven(huntingPhaseIndex) ? SCATTERING : CHASING;
    }



    public void updateChasingTargetPhase() {
        int currentPhaseDuration = GameModel.chasingTargetDuration(levelNum, huntingPhaseIndex);

        if(currentPhaseDuration == -1) //Infinite, no need to update
            return;

        if (huntingTimer.ticks() >= currentPhaseDuration) {
            huntingTimer.reset();
            huntingPhaseIndex++;
            for(var ghost : ghosts) {
                ghost.turnBackInstantly();
                huntingBehavior(ghost);
            }
        }
    }




    private iVector2D chasingTarget(byte ghostID) {
        return switch (ghostID) {
            // Blinky: attacks Pac-Man directly
            case GameModel.RED_GHOST -> pacman.atTile();
            // Pinky: ambushes Pac-Man
            case GameModel.PINK_GHOST -> pacman.tilesAheadWithOverflowBug(4);
            // Inky: attacks from opposite side as Blinky
            case GameModel.CYAN_GHOST -> pacman.tilesAheadWithOverflowBug(2).scaled(2).minus(ghosts[GameModel.RED_GHOST].atTile());
            // Clyde/Sue: attacks directly but retreats if Pac is near


            case GameModel.ORANGE_GHOST -> Math.sqrt(ghosts[GameModel.ORANGE_GHOST].atTile().sqrEuclideanDistance(pacman.atTile())) < (8)      //using squared distance
                        ? pacman.atTile()
                        : ghostScatterTarget(GameModel.ORANGE_GHOST);

            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }


    /**
     * @param id ghost ID, one of {@link GameModel#RED_GHOST}, {@link GameModel#PINK_GHOST},
     *           {@value GameModel#CYAN_GHOST}, {@link GameModel#ORANGE_GHOST}
     * @return the ghost with the given ID
     */
    public Ghost ghost(byte id) {
        checkGhostID(id);
        return ghosts[id];
    }


    public Ghost[] ghosts() {
        return ghosts;
    }

    public fVector2D ghostRevivalPosition(byte ghostID) {
        checkGhostID(ghostID);
        return switch (ghostID) {
            case GameModel.RED_GHOST, GameModel.PINK_GHOST -> PacmanMap.HOUSE_MIDDLE_SEAT;
            case GameModel.CYAN_GHOST            -> PacmanMap.HOUSE_LEFT_SEAT;
            case GameModel.ORANGE_GHOST          -> PacmanMap.HOUSE_RIGHT_SEAT;
            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }

    public iVector2D ghostScatterTarget(byte ghostID) {
        checkGhostID(ghostID);
        return switch (ghostID) {
            case GameModel.RED_GHOST    -> PacmanMap.SCATTER_TARGET_RIGHT_UPPER_CORNER;
            case GameModel.PINK_GHOST   -> PacmanMap.SCATTER_TARGET_LEFT_UPPER_CORNER;
            case GameModel.CYAN_GHOST   -> PacmanMap.SCATTER_TARGET_RIGHT_LOWER_CORNER;
            case GameModel.ORANGE_GHOST -> PacmanMap.SCATTER_TARGET_LEFT_LOWER_CORNER;
            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }

    public Direction ghostInhouseInitDir(byte ghostID) {
        checkGhostID(ghostID);
        return switch (ghostID) {
            case GameModel.RED_GHOST, GameModel.ORANGE_GHOST, GameModel.CYAN_GHOST -> UP;
            case GameModel.PINK_GHOST   -> DOWN;
            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }


    private void handlePacAndGhostCollision() {
        //havent have eaten state

        for(var ghost : ghosts) {
            if(pacman.sameTile(ghost)) {
                if(ghost.state().equals(GhostState.CHASING_TARGET)) {
                    levelState = LevelState.LEVEL_LOST;
                }
            }
        }
    }


    public Pacman pacman() {
        return pacman;
    }

    public World world() {
        return world;
    }

    public int levelNum() {
        return levelNum;
    }

    public void applyPacDirKey(Direction nextDir) {
        pacman.setNextDir(nextDir);
    }

    public void movePacman() {
        EntityMovement.move(pacman, world);
//        iVector2D currentTile = pacman.atTile();
//
//        /* Handle turn back instantly */
//        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
//            pacman.turnBackInstantly();
//            return;
//        }
//
//        /*  handle pacman be blocked by wall or smth */
//        if(!pacman.canAccessTile(pacman.tilesAhead(1), world) && pacman.offset().almostEquals(fVector2D.ZERO, pacman.currentSpeed(),  pacman.currentSpeed())) {
//            if(!pacman.isStanding()) {
//                pacman.centerOverTile(currentTile);
//                pacman.standing();
//            }
//        }
//        /*  handle pacman at intersection */
//        else if(world.isIntersection(currentTile)) {
//            //if pacman haven't aligned to tile, but almost aligned, then aligned it
//            if(pacman.isNewTileEntered() && pacman.offset().almostEquals(fVector2D.ZERO, pacman.currentSpeed(), pacman.currentSpeed())) {
//                pacman.placeAtTile(currentTile.toFloatVec());
//            }
//        }
//        //Handle if pacman gothrough portal
//        else if(world.belongsToPortal(currentTile)) {
//            if(!world.belongsToPortal(pacman.tilesAhead(1))) {
//                iVector2D teleportTo = world.portals().otherTunnel(currentTile);
//                pacman.placeAtTile(teleportTo.toFloatVec());
//            }
//        }
//
//        /* Handle if pacman be blocked in next turn, it'll keep moving in current direction*/
//        if(pacman.isAlignedToTile()) {
//            if(pacman.canAccessTile(currentTile.plus(pacman.nextDir().vector()), world)) {
//                pacman.setMovingDir(pacman.nextDir());
//            }
//        }
//
//
//        // If pacman is not standing, it can move :)))
//        if(!pacman.isStanding()) {
//            pacman.move();
//        }
    }


    private void handlePacmanEatFoot() {
        iVector2D currentTile = pacman.atTile();
        if(world.hasFoodAt(currentTile) && !world.hasEatenFoodAt(currentTile)) {
            houseControl().updateDotCount(this);

            if(world.isEnergizerTile(currentTile)) {
            }

            world.removeFood(currentTile);
            pacman.endStarving();
        }
        else {
            pacman.starve();
        }
    }

    public GhostHouseControl houseControl() {
        return houseControl;
    }

    public LevelState currentEvent() {
        return levelState;
    }

    public void updateEvent(LevelState event) {
        levelState = event;
    }

    public void switchPause() {
        if(levelState == LevelState.LEVEL_PAUSED) {
            levelState = LevelState.LEVEL_STARTED;
        } else {
            levelState = LevelState.LEVEL_PAUSED;
        }
        huntingTimer.switchPause();
        levelStateTimer.switchPause();
    }
}
