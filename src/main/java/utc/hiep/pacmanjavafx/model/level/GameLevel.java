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
import static utc.hiep.pacmanjavafx.model.entity.GhostState.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.*;
import static utc.hiep.pacmanjavafx.lib.Global.*;

public class GameLevel {
    public record Data(
            int number, // Level number, starts at 1.
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
        public Data(int number, byte[] data) {
            this(number, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8],
                    data[9], data[10], data[11]);
        }
    }

    //LevelNum and data of this level
    private int levelNum;
    private Data data;

    //state things
    private LevelState levelState;
    private GameEvent gameEvent;

    //entity
    private Pacman pacman;
    private World world;

    private final Ghost[] ghosts;

    //Timer
    private Timer huntingTimer;
    private Timer levelStateTimer;
    private Timer frightenedTimer;
    private Timer gameEventTimer;

    //Game logic things
    private byte huntingPhaseIndex;
    private byte totalNumGhostsKilled;
    private byte cruiseElroyState;
    private GhostHouseControl houseControl;
    private int points;
    private LevelState lastState;   //for pause :))



    public GameLevel() {
        this(1);
    }


    public GameLevel(int levelNum) {
        this.levelNum = levelNum;
        data = new Data(levelNum, RAW_LEVEL_DATA[levelNum - 1]);

        levelState = LevelState.LEVEL_CREATED;
        gameEvent = GameEvent.NONE;

        levelStateTimer = new Timer();
        frightenedTimer = new Timer();
        gameEventTimer = new Timer();
        huntingTimer = new Timer();

        this.pacman = new Pacman("PACMAN");
        this.world = PacmanMap.createPacManWorld();
        ghosts = Stream.of(GameModel.RED_GHOST, GameModel.PINK_GHOST, GameModel.CYAN_GHOST, GameModel.ORANGE_GHOST)
                .map(id -> new Ghost(id, ghostName(id))).toArray(Ghost[]::new);
        this.houseControl = new GhostHouseControl(levelNum);


        setUpGhost();
        setUpPacman();


        huntingPhaseIndex = 0;
        points = 0;
    }

    private void setUpPacman() {
        pacman.setDefaultSpeed((float) PPS_AT_100_PERCENT / FPS);
        pacman.setPercentageSpeed(data.pacSpeedPercentage);
        pacman.placeAtTile(PacmanMap.PAC_POSITION);
        pacman.hide();
    }

    private void setUpGhost() {
        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.reset();
            ghost.setHouse(world.house());
            ghost.setFrightenedBehavior(g -> {
                g.setPercentageSpeed(ghostSpeedPercentage(g));
                EntityMovement.randomMove(g, world);
            });
            ghost.setRevivalPosition(ghostRevivalPosition(ghost.id()));
            ghost.setOutOfHouseSpeed(GameModel.PPS_AT_100_PERCENT / (float) GameModel.FPS);
            ghost.setDefaultSpeedReturnHouse(GameModel.PPS_GHOST_RETURNING_HOME / (float) GameModel.FPS);
            ghost.setDefaultSpeedInsideHouse(GameModel.PPS_GHOST_INHOUSE / (float) GameModel.FPS);
            ghost.setState(LOCKED);
            ghost.setPercentageSpeed(ghostSpeedPercentage(ghost));

            ///------
            ghost.placeAtTile(ghostRevivalPosition(ghost.id()));
            if(ghost.id() == GameModel.RED_GHOST) {
                ghost.placeAtTile(PacmanMap.HOUSE_DOOR_SEAT);
            }
            ghost.setMovingDir(ghostInHouseInitDir(ghost.id()));
        });

        var forbiddenMovesAtTile = new HashMap<iVector2D, List<Direction>>();
        var up = List.of(UP);

        PacmanMap.PACMAN_RED_ZONE.forEach(tile -> forbiddenMovesAtTile.put(tile, up));
        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.setForbiddenMoves(forbiddenMovesAtTile);
            ghost.setHuntingBehavior(this::huntingBehavior);
            ghost.hide();
        });
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

        if(gameEvent == GameEvent.GHOST_EATEN) {
            gameEventTimer.updateTimer();
            if(gameEventTimer.ticks() <= FPS) {
                gameEvent = GameEvent.GHOST_EATEN;
                pacman.hide();
                Arrays.stream(ghosts).filter(ghost -> ghost.state().equals(GhostState.EATEN)).forEach(ghost -> {
                    ghost.update(pacman, world);
                });
                return;
            } else {
                gameEvent = GameEvent.PAC_EAT_ENERGIZER;
                pacman.show();
                Arrays.stream(ghosts).filter(ghost -> ghost.state().equals(GhostState.EATEN)).forEach(ghost -> {
                    ghost.setState(RETURNING_TO_HOUSE);
                });
                gameEventTimer.reset();
            }
        }

        if(gameEvent == GameEvent.PAC_EAT_ENERGIZER) {
            frightenedTimer.updateTimer();

            updateEventPacEatEnergizer();
            for(var ghost : ghosts) {
                if(world.isTunnel(ghost.atTile()) || world.belongsToPortal(ghost.atTile())) {
                    ghost.setPercentageSpeed(ghostSpeedPercentage(ghost));
                };
            }
            if(frightenedTimer.ticks() >= GameModel.frightenedDuration(levelNum) * 0.6) {
                for(var ghost : pacman.victims()) {
                    ghost.setAnimator(AnimatorLib.GHOST_ANIMATOR[LATE_FRIGTHENED_GHOST]);
                }
            }

            if(frightenedTimer.ticks() >= GameModel.frightenedDuration(levelNum)) {
                gameEvent = GameEvent.NONE;
                huntingTimer.resume();

                for(var ghost : pacman.victims()) {
                    if(ghost.state() == LOCKED)
                        ghost.setState(LOCKED);
                    else if(ghost.state() == FRIGHTENED)
                        ghost.setState(CHASING_TARGET);
                    else
                        System.out.println("UnExpected things happened");
                }

                frightenedTimer.reset();
            }
        }

        huntingTimer.updateTimer();
        updateChasingTargetPhase();
        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.update(pacman, world);
        });

        movePacman();
        handlePacmanEatFoot();
        handlePacAndGhostCollision();
    }




    /* --------------Ghost logic------------------*/


    /**
     * change ghost behavior based on current phase - between scattering and chasing
     * */
    private void huntingBehavior(Ghost ghost) {
        if (chasingPhase().isPresent() || ghost.id() == GameModel.RED_GHOST && cruiseElroyState > 0) {
            //chase pacman
            ghost.setHuntingBehavior(g -> {
                byte relSpeed = ghostSpeedPercentage(g);
                g.setPercentageSpeed(relSpeed);
                g.setTargetTile(chasingTarget(g.id()));
                EntityMovement.chaseTarget(g, world);
            });
        } else {
            //chase scatter
            ghost.setHuntingBehavior(g -> {
                byte relSpeed = ghostSpeedPercentage(g);
                g.setPercentageSpeed(relSpeed);
                g.setTargetTile(ghostScatterTarget(g.id()));
                EntityMovement.chaseTarget(g, world);
            });
        }
    }


    /**
     * Switch phase between scattering and chasing when time is up
     */
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



    /**
     * @param ghost a ghost
     * @return relative speed of ghost in percent of the base speed
     */
    public byte ghostSpeedPercentage(Ghost ghost) {
        if (world.isTunnel(ghost.atTile())) {
            return data.ghostSpeedTunnelPercentage();
        }
        if (ghost.id() == GameModel.RED_GHOST && cruiseElroyState == 1) {
            return data.elroy1SpeedPercentage();
        }
        if (ghost.id() == GameModel.RED_GHOST && cruiseElroyState == 2) {
            return data.elroy2SpeedPercentage();
        }

        if(ghost.state().equals(FRIGHTENED)) {
            return data.ghostSpeedFrightenedPercentage();
        }
        return data.ghostSpeedPercentage();
    }


    /**
     * Return the chasing target of the ghost in chasing phase by ghost ID
     * @param ghostID id of the ghost
     * @return the chasing target of the ghost
     */
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
     * Return the revival position of the ghost (where ghost will head to after be eaten) by ghost ID
     * @param ghostID id of the ghost
     * @return the revival position of the ghost
     */
    private fVector2D ghostRevivalPosition(byte ghostID) {
        checkGhostID(ghostID);
        return switch (ghostID) {
            case GameModel.RED_GHOST, GameModel.PINK_GHOST -> PacmanMap.HOUSE_MIDDLE_SEAT;
            case GameModel.CYAN_GHOST            -> PacmanMap.HOUSE_LEFT_SEAT;
            case GameModel.ORANGE_GHOST          -> PacmanMap.HOUSE_RIGHT_SEAT;
            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }


    /**
     * Return the scatter target of the ghost by ghost ID
     * @param ghostID id of the ghost
     * @return the scatter target of the ghost
     */
    private iVector2D ghostScatterTarget(byte ghostID) {
        checkGhostID(ghostID);
        return switch (ghostID) {
            case GameModel.RED_GHOST    -> PacmanMap.SCATTER_TARGET_RIGHT_UPPER_CORNER;
            case GameModel.PINK_GHOST   -> PacmanMap.SCATTER_TARGET_LEFT_UPPER_CORNER;
            case GameModel.CYAN_GHOST   -> PacmanMap.SCATTER_TARGET_RIGHT_LOWER_CORNER;
            case GameModel.ORANGE_GHOST -> PacmanMap.SCATTER_TARGET_LEFT_LOWER_CORNER;
            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }


    /* Init dir for ghost when level created and ghosts are locked in house */
    private Direction ghostInHouseInitDir(byte ghostID) {
        checkGhostID(ghostID);
        return switch (ghostID) {
            case GameModel.RED_GHOST, GameModel.ORANGE_GHOST, GameModel.CYAN_GHOST -> UP;
            case GameModel.PINK_GHOST   -> DOWN;
            default -> throw new IllegalArgumentException("Illegal ghost ID: " + ghostID);
        };
    }




    /* ------------------Pac logic------------------------*/

    /**
    * Handle pacman and ghost collision
     * if ghosts are in chasing state, pacman will die, game lose
     * if ghosts are in frightened state, pacman will eat ghost, get points
    * */
    private void handlePacAndGhostCollision() {
        //havent have eaten state
        for(var ghost : ghosts) {
            if(pacman.sameTile(ghost)) {
                if(ghost.state().equals(GhostState.CHASING_TARGET)) {
                    levelState = LevelState.LEVEL_LOST;
                }
                else if(ghost.state().equals(GhostState.FRIGHTENED)) {
                    gameEvent = GameEvent.GHOST_EATEN;
                    pacman.victims().remove(ghost);
                    points += 200 * (int) Math.pow(2, 4 - pacman.victims().size() - 1);

                    System.out.println("Points: " + points);
                    ghost.setState(EATEN);

                }
            }
        }
    }


    /**
     * Apply direction key for pacman, change pacman next direction
     * @param nextDir next direction
     * */
    public void applyPacDirKey(Direction nextDir) {
        pacman.setNextDir(nextDir);
    }


    /**
     * Move pacman each frame, @see {@link EntityMovement#move} to see logic
     * */
    private void movePacman() {
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


    /**
    * Handle pacman eat food
    * */
    private void handlePacmanEatFoot() {
        iVector2D currentTile = pacman.atTile();
        if(world.hasFoodAt(currentTile) && !world.hasEatenFoodAt(currentTile)) {
            houseControl().updateDotCount(this);

            if(world.isEnergizerTile(currentTile)) {
                points += POINTS_ENERGIZER;
                gameEvent = GameEvent.PAC_EAT_ENERGIZER;
                updateEventPacEatEnergizer();
                pacman.setVictims(ghosts);
                frightenedTimer.reset();
            } else {
                points += POINTS_NORMAL_PELLET;
            }

            System.out.println("Points: " + points);

            world.removeFood(currentTile);
            pacman.endStarving();
        }
        else {
            pacman.starve();
        }

        if(world.uneatenFoodCount() == 0) {
            levelState = LevelState.LEVEL_WON;
        }
    }

    /**
    * update ghost state, speed, and animator when pacman eat energizer
    * */
    private void updateEventPacEatEnergizer() {
        if(gameEvent == GameEvent.PAC_EAT_ENERGIZER) {
            for(var ghost : pacman.victims()) {
                if(ghost.state().equals(CHASING_TARGET)) {
                    ghost.setState(GhostState.FRIGHTENED);
                    ghost.setPercentageSpeed(ghostSpeedPercentage(ghost));
                    ghost.turnBackInstantly();
                }
                ghost.setAnimator(AnimatorLib.GHOST_ANIMATOR[FRIGHTENED_GHOST]);
            }
            huntingTimer.pause();
        }
    }



    /* Scatter and Chasing phase */

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






    /*Get entity methods*/


    public Pacman pacman() {
        return pacman;
    }

    public World world() {
        return world;
    }

    public int levelNum() {
        return levelNum;
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


    public GhostHouseControl houseControl() {
        return houseControl;
    }

    public LevelState currentEvent() {
        return levelState;
    }

    public void updateEvent(LevelState event) {
        levelState = event;
    }

    public GameLevel nextLevel() {
        GameLevel nextLevel = new GameLevel(this.levelNum + 1);
        nextLevel.points = this.points;
        return nextLevel;
    }

    public void switchPause() {
        if(levelState == LevelState.LEVEL_PAUSED) {
            levelState = LevelState.LEVEL_STARTED;
        } else {
            levelState = LevelState.LEVEL_PAUSED;
            lastState = levelState;
        }
        huntingTimer.switchPause();
        levelStateTimer.switchPause();
    }
}
