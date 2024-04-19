package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public class Pacman extends MovableEntity{

    private static final int PAC_UI_SIZE = 15;  //px

    private final String name;
    private Timer ticker;


    //to turn it to x and y: x = value / 224, y = value % 224
    private static final int[] RIGHT_DIR_ANI = {32, 0, 16};     //px
    private static final int[] LEFT_DIR_ANI = {32, 224, 240};   //px
    private static final int[] UP_DIR_ANI = {32, 448, 464};     //px
    private static final int[] DOWN_DIR_ANI = {32, 672, 688};   //px


    //UI part
    private final Image spriteSheet;

    public Pacman(String name) {
        super();
        this.name = name;
        spriteSheet = ImageLibrary.SPRITE_SHEET;
        placeAtTile(PacmanMap.PAC_POSITION);
    }

    public void setTicker(Timer ticker) {
        this.ticker = ticker;
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

    @Override
    public void render(GraphicsContext gc) {
        int animationCount = (int) (ticker.getTick() % 2);
        gc.drawImage(spriteSheet, RIGHT_DIR_ANI[animationCount] % 224, RIGHT_DIR_ANI[animationCount] / 224, 15, 15, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE, 32, 32);
    }
}
