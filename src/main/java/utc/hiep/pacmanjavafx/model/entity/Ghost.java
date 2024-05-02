package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.EatenDotCounter;
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

    private static final int GHOST_UI_SIZE = 16;
    private Image sprite_sheet = ImageLib.SPRITE_SHEET;
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
    private EatenDotCounter eatenFoodCounter;

    private Map<iVector2D, List<Direction>> forbiddenMoves = Collections.emptyMap();


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
        state = LOCKED;
        this.animator = AnimatorLib.GHOST_ANIMATOR[id];
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


    public void updateStateLocked() {
        updateDefaultSpeed(speedInsideHouse);
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
        if (differsAtMost(0.5f * currentSpeed(), centerX, houseCenterX)) {
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


    /**
     * When an eaten ghost has arrived at the ghost house door, he falls down to the center of the house,
     * then moves up again (if the house center is his revival position), or moves sidewards towards his revival position.
     */
    private void updateStateEnteringHouse() {
        fVector2D houseCenter = house.center();
        if (posY() >= houseCenter.y()) {
            // reached ground
            setPosY(houseCenter.y());
            if (revivalPosition.x() < posX()) {
                setMovingDir(LEFT);
                setNextDir(LEFT);
            } else if (revivalPosition.x() > posX()) {
                setMovingDir(RIGHT);
                setNextDir(RIGHT);
            }
        }
        setSpeed(speedReturningToHouse);
        move();
        if (posY() >= revivalPosition.y() && differsAtMost(0.5 * speedReturningToHouse, posX(), revivalPosition.x())) {
            setPosition(revivalPosition);
            setMovingDir(RIGHT);
            setNextDir(RIGHT);
            state = LOCKED;
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




    public void setOutOfHouseSpeed(float pixelsPerTick) {
        outOfHouseSpeed = pixelsPerTick;
    }

    public void setDefaultSpeedInsideHouse(float pixelsPerTick) {
        speedInsideHouse = pixelsPerTick;
    }

    public void setDefaultSpeedReturnHouse(float pixelsPerTick) {
        speedReturningToHouse = pixelsPerTick;
    }





    // Here begins the state machine part

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


    /**
     * Changes the state of this ghost.
     *
     * @param state the new state
     */
    public void setState(GhostState state) {
        checkNotNull(state);
        if (this.state == state) {
            return;
        }
        this.state = state;
        switch (state) {
            case LOCKED, LEAVING_HOUSE -> {
                updateDefaultSpeed(speedInsideHouse);
            }
            case CHASING_TARGET -> {
                updateDefaultSpeed(outOfHouseSpeed);
                //huntingBehavior.accept(this);
            }
//            case ENTERING_HOUSE, RETURNING_TO_HOUSE -> selectAnimation(ANIM_GHOST_EYES);
            case FRIGHTENED -> updateDefaultSpeed(speedReturningToHouse);
            default -> {}
        }
    }


    /**
     * Executes a single simulation step for this ghost in the current game level.
     *
//     * @param pac Pac-Man or Ms. Pac-Man
     */
    public void update(Pacman pac, World world) {
        checkNotNull(pac);
        switch (state) {
            case LOCKED             -> updateStateLocked();
            case LEAVING_HOUSE      -> updateStateLeavingHouse();
            case CHASING_TARGET     -> updateStateChasingTarget();
            case FRIGHTENED         -> updateStateFrightened();
//            case EATEN              -> updateStateEaten();
//            case RETURNING_TO_HOUSE -> updateStateReturningToHouse(world);
//            case ENTERING_HOUSE     -> updateStateEnteringHouse();
        }
    }

    private void updateStateFrightened() {
        frightenedBehavior.accept(this);
    }

    private void updateStateChasingTarget() {
        huntingBehavior.accept(this);
    }

//    public void eaten(int index) {
//        setState(EATEN);
//    }


    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(sprite_sheet, animator.getAnimationPos().posX(), animator.getAnimationPos().posY(), GHOST_UI_SIZE, GHOST_UI_SIZE, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE, 32, 32);
    }

    public void animatorUpdate() {
        animator.update(movingDir());
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }
}
