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
import static utc.hiep.pacmanjavafx.model.entity.GhostState.HUNTING_PAC;

public class Ghost extends MovableEntity{


    public static final String ANIM_GHOST_NORMAL     = "normal";
    public static final String ANIM_GHOST_FRIGHTENED = "frightened";
    public static final String ANIM_GHOST_EYES       = "eyes";
    public static final String ANIM_GHOST_FLASHING   = "flashing";
    public static final String ANIM_GHOST_NUMBER     = "number";
    public static final String ANIM_BLINKY_DAMAGED   = "damaged";
    public static final String ANIM_BLINKY_STRETCHED = "stretched";
    public static final String ANIM_BLINKY_PATCHED   = "patched";
    public static final String ANIM_BLINKY_NAKED     = "naked";


    private static final int GHOST_UI_SIZE = 16;
    private Image sprite_sheet = ImageLibrary.SPRITE_SHEET;
    private Animator animator;


    private final byte id;
    private final String name;
    private GhostState state;
    //private Consumer<Ghost> huntingBehavior;
    //private Consumer<Ghost> frightenedBehavior;
    private House house;
    private fVector2D revivalPosition;
    private float speedReturningToHouse;
    private float speedInsideHouse;
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
        this.id = id;
        this.name = name;
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







    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean canTurnBack() {
        //return newTileEntered && is(HUNTING_PAC, FRIGHTENED);
        return false;
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
        }
        if (world.insideBounds(tile)) {
            return !world.isWall(tile);
        }
        return world.belongsToPortal(tile);
    }


    @Override
    public void render(GraphicsContext gc) {

    }
}
