package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.world.House;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static utc.hiep.pacmanjavafx.lib.Direction.*;
import static utc.hiep.pacmanjavafx.lib.Global.*;
import static utc.hiep.pacmanjavafx.model.entity.GhostState.*;

public class Ghost extends MovableEntity{

    private Animator animator;


    private final byte id;
    private final String name;
    private GhostState state;
    private Consumer<Ghost> huntingBehavior;
    private Consumer<Ghost> frightenedBehavior;
    public House house;
    private fVector2D revivalPosition;
    private float outOfHouseSpeed;
    private float speedReturningToHouse;
    private float speedInsideHouse;

    //eaten food counter for exit house
    private Map<iVector2D, List<Direction>> forbiddenMoves = Collections.emptyMap();
    private int pointDisplay;

    /**
     * @param id  The ghost ID. One of
     * {@link GameModel#RED_GHOST},
     * {@link GameModel#PINK_GHOST},
     * {@link GameModel#CYAN_GHOST},
     * {@link GameModel#ORANGE_GHOST}.
     * @param name the ghost's readable name, e.g. "Pinky"
     */
    public Ghost(byte id, String name) {
        //checkGhostID(id);
        checkNotNull(name);
        reset();
        this.id = id;
        this.name = name;
        setState(LOCKED);
        this.animator = GameController.rm().getAnimator(id);
    }

    public void setHouse(House house) {
        checkNotNull(house);
        this.house = house;
    }

    public boolean insideHouse() {
        return house.contains(atTile());
    }

    public void setRevivalPosition(fVector2D position) {
        checkNotNull(position);
        revivalPosition = position;
    }


    public void setForbiddenMoves(Map<iVector2D, List<Direction>> moves) {
        forbiddenMoves = moves;
    }



    /**
     * Executes a single simulation step for this ghost in the current game level.
     *
     * @param pac Pac-Man or Ms. Pac-Man
     */
    public synchronized void update(Pacman pac, World world) {
        checkNotNull(pac);
        switch (state) {
            case LOCKED             -> updateStateLocked();
            case LEAVING_HOUSE      -> updateStateLeavingHouse();
            case CHASING_TARGET     -> updateStateChasingTarget();
            case FRIGHTENED         -> updateStateFrightened();
            case EATEN              -> updateStateEaten(pac);
            case RETURNING_TO_HOUSE -> updateStateReturningToHouse(world);
            case ENTERING_HOUSE     -> updateStateEnteringHouse();
        }
    }


    /**
     * Changes the state of this ghost.
     *
     * @param state the new state
     */
    public void setState(GhostState state) {
        checkNotNull(state);
        this.state = state;
        switch (state) {
            case LOCKED, LEAVING_HOUSE -> {
                animator = GameController.rm().getAnimator(id);;
                updateDefaultSpeed(speedInsideHouse);
            }
            case CHASING_TARGET, FRIGHTENED -> {
                animator = GameController.rm().getAnimator(id);;
                updateDefaultSpeed(outOfHouseSpeed);
            }
            case EATEN, ENTERING_HOUSE, RETURNING_TO_HOUSE -> {
                animator = GameController.rm().getAnimator(GameModel.EATEN_GHOST);
                setDefaultSpeed(speedReturningToHouse);
            }
            default -> {}
        }
    }



    public void updateStateLocked() {
        fVector2D currentTile = halfTileLeftOf(atTile().x(), atTile().y());
        if(!currentTile.equals(getRevivalPosition())) {
            setNextDir(movingDir().opposite());
            turnBackInstantly();
            setMovingDir(movingDir().opposite());
            setNextDir(movingDir());
        }
        move();
    }

    public void updateStateLeavingHouse() {
        fVector2D houseEntryPosition = house.door().entryPosition();
        if (posY() <= houseEntryPosition.y()) {
            // has raised and is outside house
            setPosition(houseEntryPosition);
            setMovingDir(LEFT);
            setNextDir(LEFT);
            newTileEntered = false;
            setState(CHASING_TARGET);
            return;
        }
        // move inside house
        float centerX = center().x();
        float houseCenterX = house.center().x();
        if (differsAtMost(currentSpeed(), centerX, houseCenterX)) {
            // align horizontally and raise
            setPosX(houseCenterX - HALF_TILE_SIZE);
            setMovingDir(Direction.UP);
            setNextDir(Direction.UP);
        } else {
            // move sidewards until center axis is reached
            setMovingDir(centerX < houseCenterX ? Direction.RIGHT : LEFT);
            setNextDir(centerX < houseCenterX ? Direction.RIGHT : LEFT);
        }

        move();
    }

    private void updateStateEaten(Pacman pac) {
        if(pointDisplay != 0)
            return;
        pointDisplay = 200 * (int) Math.pow(2, 4 - pac.victims().size() - 1);
    }

    private void updateStateReturningToHouse(World world) {
        fVector2D houseEntry = house.door().entryPosition();
        if (position().almostEquals(houseEntry, currentSpeed(), currentSpeed())) {
            setPosition(houseEntry);
            setMovingDir(DOWN);
            setNextDir(DOWN);
            setState(ENTERING_HOUSE);
        } else {
            setTargetTile(house.door().leftWing().plus(UP.vector()));
            EntityMovement.chaseTarget(this, world);
        }
    }

    private void updateStateFrightened() {
        frightenedBehavior.accept(this);
    }

    private void updateStateChasingTarget() {
        huntingBehavior.accept(this);
    }


    /**
     * When an eaten ghost has arrived at the ghost house door, he falls down to the center of the house,
     * then moves up again (if the house center is his revival position), or moves sidewards towards his revival position.
     */
    private void updateStateEnteringHouse() {
        fVector2D houseCenter = house.center();
        if (posY() >= houseCenter.y()) {
            // reached ground
            setPosY(houseCenter.y());
            if (revivalPosition.x() * TILE_SIZE < posX()) {
                setMovingDir(LEFT);
                setNextDir(LEFT);
            } else if (revivalPosition.x() * TILE_SIZE > posX()) {
                setMovingDir(RIGHT);
                setNextDir(RIGHT);
            }
        }
        updateDefaultSpeed(speedReturningToHouse);
        move();

        if (posY() >= revivalPosition.y() * TILE_SIZE && differsAtMost(currentSpeed(), posX(), revivalPosition.x() * TILE_SIZE)) {
            setPosition(revivalPosition.scaled(TILE_SIZE));
            setState(LOCKED);
        }
    }

    public void setHuntingBehavior(Consumer<Ghost> function) {
        checkNotNull(function);
        huntingBehavior = function;
    }

    /**
     * @param function function specifying the behavior when frightened
     */
    public void setFrightenedBehavior(Consumer<Ghost> function) {
        checkNotNull(function);
        frightenedBehavior = function;
    }

    public byte id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }


    @Override
    public boolean canTurnBack() {
        //return newTileEntered && is(CHASING_TARGET, FRIGHTENED);
        return false;
    }

    public fVector2D getRevivalPosition() {
        return revivalPosition;
    }

    @Override
    public boolean canAccessTile(iVector2D tile, World world) {
        checkNotNull(tile);

        // hunting ghosts cannot move up at certain tiles in Pac-Man game
        if (state == CHASING_TARGET) {
            var currentTile = atTile();
            if (forbiddenMoves.containsKey(currentTile)) {
                for (Direction dir : forbiddenMoves.get(currentTile)) {
                    if (currentTile.plus(dir.vector()).equals(tile)) {
                        return false;
                    }
                }
            }
        }
        if (house.door().occupies(tile)) {
            return is(ENTERING_HOUSE, LEAVING_HOUSE);
        }

        if (world.insideBounds(tile)) {
            return !world.isWall(tile);
        }

        return world.belongsToPortal(tile) || world.isTunnel(tile);
    }



    //Set default speed
    public void setOutOfHouseSpeed(float pixelsPerTick) {
        outOfHouseSpeed = pixelsPerTick;
    }

    public void setDefaultSpeedInsideHouse(float pixelsPerTick) {
        speedInsideHouse = pixelsPerTick;
    }

    public void setDefaultSpeedReturnHouse(float pixelsPerTick) {
        speedReturningToHouse = pixelsPerTick;
    }



    // Here begins the state part

    /**
     * The current state of this ghost.
     */
    public GhostState state() {
        return state;
    }

    /**
     * @param alternatives ghost states to be checked
     * @return <code>true</code> if this ghost is in any of the given states. If no alternatives are given, returns
     * <code>false</code>
     */
    public boolean is(GhostState... alternatives) {
        if (state == null) {
            throw new IllegalStateException("Ghost state is not defined");
        }
        return oneOf(state, alternatives);
    }





    @Override
    public void render(GraphicsContext gc) {
        if(state == EATEN) {
            gc.setFont(FontLib.EMULOGIC(10));
            gc.setFill(Color.color(0, 1, 1));
            gc.fillText(String.valueOf(pointDisplay), posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE);
        } else {
            animator.render(gc, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE);
            pointDisplay = 0;
        }
    }

    public void animatorUpdate() {
        animator.update(movingDir());
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }
}
