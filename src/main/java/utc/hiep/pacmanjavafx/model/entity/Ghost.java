package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.AnimatorLib;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.world.House;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static utc.hiep.pacmanjavafx.lib.Global.*;
import static utc.hiep.pacmanjavafx.model.entity.GhostState.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.*;

public class Ghost extends MovableEntity{

    private static final int GHOST_UI_SIZE = 16;
    private Image sprite_sheet = ImageLibrary.SPRITE_SHEET;
    private Animator animator;


    private final byte id;
    private final String name;
    private GhostState state;
    //private Consumer<Ghost> huntingBehavior;
    //private Consumer<Ghost> frightenedBehavior;
    public House house;
    private fVector2D revivalPosition;
    private float outOfHouseSpeed;
    private float speedReturningToHouse;
    private float speedInsideHouse;

    //eaten food counter for exit house
    private int eatenFoodCounter;

    //private Map<Vector2i, List<Direction>> forbiddenMoves = Collections.emptyMap();


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
        eatenFoodCounter = 0;
        updateState();
        this.animator = AnimatorLib.GHOST_ANIMATOR[id];
    }

    public void setHouse(House house) {
        checkNotNull(house);
        this.house = house;
    }


    public void setRevivalPosition(fVector2D position) {
        checkNotNull(position);
        revivalPosition = position;
    }


    public void updateEatenFoodCounter() {
        eatenFoodCounter++;
    }

    public void updateState() {
        if(eatenFoodCounter == 0) {
            state = LOCKED;
            updateDefaultSpeed(speedInsideHouse);
            return;
        }

        if(eatenFoodCounter == 30) {
            state = LEAVING_HOUSE;
            updateDefaultSpeed(speedInsideHouse);
            return;
        }


        fVector2D houseEntryPosition = house.door().entryPosition();
        if (state == LEAVING_HOUSE && posY() == houseEntryPosition.y()) {
            state = SCATTER;
            updateDefaultSpeed(outOfHouseSpeed);
            return;
        }




    }


    public void changeToHunter() {
        state = HUNTING_PAC;
        turnBackInstantly();
        updateDefaultSpeed(outOfHouseSpeed);
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean canTurnBack() {
        //return newTileEntered && is(HUNTING_PAC, FRIGHTENED);
        return false;
    }

    public fVector2D getRevivalPosition() {
        return revivalPosition;
    }

    @Override
    public boolean canAccessTile(iVector2D tile, World world) {
        checkNotNull(tile);

        // hunting ghosts cannot move up at certain tiles in Pac-Man game
        if (state == HUNTING_PAC) {
            var currentTile = atTile();
//            if (forbiddenMoves.containsKey(currentTile)) {
//                for (Direction dir : forbiddenMoves.get(currentTile)) {
//                    if (currentTile.plus(dir.vector()).equals(tile)) {
//                        Logger.trace("Hunting {} cannot move {} at {}", name, dir, currentTile);
//                        return false;
//                    }
//                }
//            }
        }
        if (house.door().occupies(tile)) {
            //return is(ENTERING_HOUSE, LEAVING_HOUSE);
            return false;
        }

        if (world.house().contains(tile)) {
            return false;
        }

        if (world.insideBounds(tile)) {
            return !world.isWall(tile);
        }

        return world.belongsToPortal(tile);
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(sprite_sheet, animator.getAnimationPos().posX(), animator.getAnimationPos().posY(), GHOST_UI_SIZE, GHOST_UI_SIZE, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE, 32, 32);
    }

    public void animatorUpdate() {
        animator.update(movingDir());
    }

    public GhostState getState() {
        return state;
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

}
