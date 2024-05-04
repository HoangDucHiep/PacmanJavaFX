package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.event.GameEvent;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.entity.Entity;
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

import static utc.hiep.pacmanjavafx.event.GameEvent.NONE;
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
            byte elroy1SpeedPercentage, // The Relative speed of Blinky being "Cruise Elroy" grade 1.
            byte elroy2DotsLeft, // Number of pellets left when Blinky becomes "Cruise Elroy" grade 2.
            byte elroy2SpeedPercentage, //The Relative speed of Blinky being "Cruise Elroy" grade 2.
            byte pacSpeedPoweredPercentage, // Relative speed of Pac-Man in power mode.
            byte ghostSpeedFrightenedPercentage, // Relative speed of frightened ghost.
            byte pacPowerSeconds, // Number of seconds Pac-Man gets power.
            byte numFlashes // Number of maze flashes at the end of this level.
    )

    {
        public Data(int number, byte[] data) {
            this(number, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8],
                    data[9], data[10]);
        }
    }

    //LevelNum and data of this level
    private final int levelNum;
    private final Data data;

    //state things
    private LevelState levelState;
    private GameEvent gameEvent;

    //entity
    private final Pacman pacman;
    private final World world;

    private final Ghost[] ghosts;

    //Timer
    private final Timer huntingTimer;
    private final Timer levelStateTimer;
    private final Timer frightenedTimer;
    private final Timer gameEventTimer;

    //Game logic things
    private byte huntingPhaseIndex;
    //    private byte totalNumGhostsKilled; //haven't implemented yet
//    private byte cruiseElroyState; //haven't implemented yet
//    private byte cruiseElroyState; //haven't implemented yet
    private final GhostHouseControl houseControl;
    private long score;
    private int lives;



    public GameLevel() {
        this(1);
        score = 0;
        lives = 3;
    }


    public GameLevel(int levelNum) {
        this.levelNum = levelNum;
        data = new Data(levelNum, RAW_LEVEL_DATA[levelNum - 1]);

        levelState = LevelState.LEVEL_READY;
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


        setUpGhost();
        setUpPacman();


        huntingPhaseIndex = 0;
    }

    private void setUpPacman() {
        pacman.reset();
        pacman.setDefaultSpeed((float) PPS_AT_100_PERCENT / FPS);
        pacman.setPercentageSpeed(data.pacSpeedPercentage);
        pacman.placeAtTile(PacmanMap.PAC_POSITION);
        pacman.setAnimator(AnimatorLib.PACMAN_ANIMATOR);
        pacman.hide();
    }

    private void setUpGhost() {
        Arrays.stream(ghosts()).forEach(ghost -> {
            ghost.show();
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

        if(levelState == LevelState.LEVEL_READY){
            updateLevelReadyState();
        }

        if(levelState == LevelState.LEVEL_STARTED) {
            updateLevelStartedState();
        }
    }


    /**
     * When level created, thing's pause, "Ready!" be shown, after 2 sec then pacman and ghosts will be shown, after 4 sec, level will be started
     */
    private void updateLevelReadyState() {
        levelStateTimer.updateTimer();
        if((int) levelStateTimer.ticks() == 2 * FPS) {
            pacman.show();
            Arrays.stream(ghosts).forEach(Ghost::show);
        }

        if((int) levelStateTimer.ticks() == 4 * FPS) {
            levelState = LevelState.LEVEL_STARTED;
            levelStateTimer.reset();
        }
    }



    /**
     * When the level started, pacman and ghosts will be updated, pacman can eat food, ghosts can chase pacman,...
     * */
    private void updateLevelStartedState() {
        houseControl.unlockGhost(this);

        if(gameEvent == GameEvent.PAC_DIED) {
            gameEventTimer.updateTimer();

            if(gameEventTimer.ticks() == FPS) {
                Arrays.stream(ghosts).forEach(Entity::hide);
                pacman.setAnimator(AnimatorLib.DIED_PACMAN_ANIMATOR);
                return;
            } else if (gameEventTimer.ticks()  <= (PAC_DIED_ANIMATION_LENGTH + FPS)) {
                pacman.animatorUpdate();
                return;
            } else if (gameEventTimer.ticks() > ((PAC_DIED_ANIMATION_LENGTH + 2) * FPS)) {
                return;
            }else {
                gameEvent = NONE;
                levelState = LevelState.LEVEL_READY;
                setUpPacman();
                setUpGhost();
                gameEventTimer.reset();
                levelStateTimer.reset();
                return;
            }
        }

        //When a ghost be eaten by pacman, it'll be an in eaten state for a while
        if(gameEvent == GameEvent.GHOST_EATEN) {
            gameEventTimer.updateTimer();
            if(gameEventTimer.ticks() <= FPS) {
                gameEvent = GameEvent.GHOST_EATEN;
                pacman.hide();
                Arrays.stream(ghosts)
                        .filter(ghost -> ghost.state().equals(GhostState.EATEN))
                        .forEach(ghost -> ghost.update(pacman, world));
                return;
            } else {
                gameEvent = GameEvent.PAC_EAT_ENERGIZER;
                pacman.show();
                Arrays.stream(ghosts)
                        .filter(ghost -> ghost.state().equals(GhostState.EATEN))
                        .forEach(ghost -> ghost.setState(RETURNING_TO_HOUSE));
                gameEventTimer.reset();
            }
        }


        if(gameEvent == GameEvent.PAC_EAT_ENERGIZER) {
            updateEventPacEatEnergizer();
        }

        huntingTimer.updateTimer();
        updateChasingTargetPhase();
        Arrays.stream(ghosts()).forEach(ghost -> ghost.update(pacman, world));

        movePacman();
        handlePacmanEatFoot();
        handlePacAndGhostCollision();
    }




    /* --------------Ghost logic------------------*/

    /**
     * get ghost current chasing behavior
     * */
    private void huntingBehavior(Ghost ghost) {
        if (chasingPhase().isPresent()) { // || ghost.id() == GameModel.RED_GHOST && cruiseElroyState > 0) {//this is for elroy, haven't implemented yet
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
     * @return relative speed of ghost in percentage of the base speed
     */
    public byte ghostSpeedPercentage(Ghost ghost) {
        if (world.isTunnel(ghost.atTile())) {
            return data.ghostSpeedTunnelPercentage();
        }
//        if (ghost.id() == GameModel.RED_GHOST && cruiseElroyState == 1) {
//            return data.elroy1SpeedPercentage();
//        }
//        if (ghost.id() == GameModel.RED_GHOST && cruiseElroyState == 2) {
//            return data.elroy2SpeedPercentage();
//        }

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


    /**
     *  Init dir for ghost when level created and ghosts are locked in a house
     *  */
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
            if(pacman.sameTile(ghost) && (pacman.isNewTileEntered() || ghost.isNewTileEntered())) {
                if(ghost.state().equals(GhostState.CHASING_TARGET)) {
                    gameEvent = GameEvent.PAC_DIED;
                    lives--;
                }
                else if(ghost.state().equals(GhostState.FRIGHTENED)) {
                    gameEvent = GameEvent.GHOST_EATEN;
                    pacman.victims().remove(ghost);
                    score += 200L * (int) Math.pow(2, 4 - pacman.victims().size() - 1);

                    ghost.setState(EATEN);
                    ghost.setPercentageSpeed(ghostSpeedPercentage(ghost));
                }
            }
        }
    }


    /**
     * Apply a direction key for pacman, change pacman next direction
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
//        /// If pacman is not standing, it can move :)))
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
                score += POINTS_ENERGIZER;
                gameEvent = GameEvent.PAC_EAT_ENERGIZER;
                updateEventPacEatEnergizer();
                pacman.setVictims(ghosts);
                frightenedTimer.reset();
            } else {
                score += POINTS_NORMAL_PELLET;
            }

            System.out.println("Points: " + score);

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
        if(gameEvent != GameEvent.PAC_EAT_ENERGIZER)
            throw new RuntimeException("Current event is not Pac_Eat_Energizer");

        frightenedTimer.updateTimer();
        for(var ghost : pacman.victims()) {
            if(ghost.state().equals(CHASING_TARGET)) {
                ghost.setState(GhostState.FRIGHTENED);
                ghost.setPercentageSpeed(ghostSpeedPercentage(ghost));
                ghost.turnBackInstantly();
            }
            ghost.setAnimator(AnimatorLib.GHOST_ANIMATOR[FRIGHTENED_GHOST]);
        }

        //In late frightened phase, change ghost animator, ghosts blink blue and white, meaning it will be back to chasing phase soon
        if(frightenedTimer.ticks() >= GameModel.frightenedDuration(levelNum) * 0.6) {
            for(var ghost : pacman.victims()) {
                ghost.setAnimator(AnimatorLib.GHOST_ANIMATOR[LATE_FRIGTHENED_GHOST]);
            }
        }

        //If frightened phase is over, change ghosts state back to their last phase
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

        huntingTimer.pause();
    }



    /* Scatter and Chasing phase */

    /**
     * @return (optional) index of current chasing phase <code>(0-3)</code>
     */
    public Optional<Integer> chasingPhase() {
        return isOdd(huntingPhaseIndex) ? Optional.of(huntingPhaseIndex / 2) : Optional.empty();
    }

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

    public LevelState currentState() {
        return levelState;
    }

    public GameEvent currentEvent() {
        return gameEvent;
    }

//    public GameLevel nextLevel() {
//        GameLevel nextLevel = new GameLevel(this.levelNum + 1);
//        nextLevel.points = this.points;
//        return nextLevel;
//    }

    public void switchPause() {
        if(levelState == LevelState.LEVEL_PAUSED) {
            levelState = LevelState.LEVEL_STARTED;
        } else {
            levelState = LevelState.LEVEL_PAUSED;
        }
        huntingTimer.switchPause();
        levelStateTimer.switchPause();
    }

    public long score() {
        return score;
    }

    public int lives() {
        return lives;
    }
}
