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
    private static final int ANIMATION_TICK = 5;
    private static final float PACMAN_DEFAULT_SPEED = 1.33f;


    private String name;
    private Timer ticker;
    private Animator animator;






    //UI part
    private final Image spriteSheet;

    public Pacman(String name) {
        super();
        //set animation sprite ui
        this.name = name;
        spriteSheet = ImageLibrary.SPRITE_SHEET;
        animator = new Animator(ANIMATION_TICK);
        animator.setANIMATIOR_SPRITE(
                new int[][]{
                        {32, 224, 240, 224},
                        {32, 0, 16, 0},
                        {32, 448, 464, 448},
                        {32, 672, 688, 672}
                });
        animator.setSpirteSize(PAC_UI_SIZE + 1); //plus 1 for gap between each sprite state in png
        reset();
        setDefaultSpeed(PACMAN_DEFAULT_SPEED);
        setPercentageSpeed((byte) 80);
        placeAtTile(PacmanMap.PAC_POSITION);
        setNewTileEntered(false);
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
        gc.drawImage(spriteSheet, animator.animatorX(), animator.animatorY(), PAC_UI_SIZE, PAC_UI_SIZE, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE, 32, 32);
    }



}
