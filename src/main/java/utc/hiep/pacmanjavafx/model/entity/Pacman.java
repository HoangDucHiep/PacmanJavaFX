package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.AnimatorLib;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.lib.Global.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.FPS;
import static utc.hiep.pacmanjavafx.model.level.GameModel.PPS_AT_100_PERCENT;

public class Pacman extends MovableEntity{

    private final String name;
    private static final float PACMAN_DEFAULT_SPEED = (float) PPS_AT_100_PERCENT / FPS;

    private static final int PAC_UI_SIZE = 15;  //size of pacman int sprite_sheet.png

    //UI part
    private final Image spriteSheet;
    private final Animator animator;

    public Pacman(String name) {
        super();
        //set animation sprite ui
        this.name = name;
        spriteSheet = ImageLibrary.SPRITE_SHEET;
        animator = AnimatorLib.PACMAN_ANIMATOR;
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


    public void standing() {
        setIsStanding(true);
        setVelocity(0, 0);
    }

    public void animatorUpdate() {
        if(!isStanding()) {
            animator.update(movingDir());
        }
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(spriteSheet, animator.getAnimationPos().posX(), animator.getAnimationPos().posY(), PAC_UI_SIZE, PAC_UI_SIZE, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE, 32, 32);
    }

}
