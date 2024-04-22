package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public class Pacman extends MovableEntity{

    private static final int PAC_UI_SIZE = 15;  //size of pacman int sprite_sheet.png
    private static final int ANIMATION_TICK = 8;
    private static final float PACMAN_DEFAULT_SPEED = 1.33f;


    private String name;
    private Timer ticker;
    private Animator animator;






    //UI part
    private final Image spriteSheet;

    Animator.AnimatorPos[] RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 0),
            new Animator.AnimatorPos(16, 0),
            new Animator.AnimatorPos(32, 0)
    };

    Animator.AnimatorPos[] LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 16),
            new Animator.AnimatorPos(16, 16),
            new Animator.AnimatorPos(32, 0)
    };

    Animator.AnimatorPos[] UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 32),
            new Animator.AnimatorPos(16, 32),
            new Animator.AnimatorPos(32, 0)
    };

    Animator.AnimatorPos[] DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 48),
            new Animator.AnimatorPos(0, 48),
            new Animator.AnimatorPos(16, 48),
            new Animator.AnimatorPos(32, 0)
    };


    public Pacman(String name) {
        super();
        //set animation sprite ui
        this.name = name;
        spriteSheet = ImageLibrary.SPRITE_SHEET;
        animator = Animator.getDirAnimator(ANIMATION_TICK, LEFT, RIGHT, UP, DOWN);


        reset();
        setDefaultSpeed(PACMAN_DEFAULT_SPEED);
        setPercentageSpeed((byte) 80);
        placeAtTile(PacmanMap.PAC_POSITION);
    }



    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean canTurnBack() {
        return true;
    }

    @Override
    public boolean canAccessTile(Vector2i tile, World world) {
        if (world.house().contains(tile)) {
            return false;
        }
        if (world.insideBounds(tile)) {
            return !world.isWall(tile);
        }
        return world.belongsToPortal(tile);
    }


    public void setTicker(Timer ticker) {
        this.ticker = ticker;
        //animator.setTicker(ticker);
    }


    public void standing() {
        setIsStanding(true);
        setVelocity(0, 0);
    }



    @Override
    public void render(GraphicsContext gc) {
        if(!isStanding()) {
            animator.update(movingDir());
        }

        gc.drawImage(spriteSheet, animator.getAnimationPos().posX(), animator.getAnimationPos().posY(), PAC_UI_SIZE, PAC_UI_SIZE, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE, 32, 32);
    }



}
