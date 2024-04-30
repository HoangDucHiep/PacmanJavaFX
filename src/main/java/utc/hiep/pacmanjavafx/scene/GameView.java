package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.lib.AnimatorLib;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.function.Consumer;

import static utc.hiep.pacmanjavafx.lib.Global.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.FPS;
import static utc.hiep.pacmanjavafx.model.level.GameModel.PPS_AT_100_PERCENT;

public class GameView extends GeneralScene{
    public static final int GAME_WIDTH = TILE_SIZE * TILES_X;   //all size is pixel
    public static final int GAME_HEIGHT = TILE_SIZE * TILES_Y;

    private boolean isGridDisplayed = false;

    private Canvas canvas;
    private GraphicsContext gc;


    //Game entity
    private World world;
    private Pacman pacman;
    private Ghost[] ghosts;


    /**
     * Constructor
     */
    public GameView() {
        super();
        setBackGround();

        //Border around canvas
        Region rectangle = new Region();
        rectangle.setStyle("-fx-background-color: white, black; -fx-background-insets: 0, 12; -fx-background-radius: 10px; -fx-min-width: 520; -fx-min-height:669; -fx-max-width:480; -fx-max-height: 669;");
        getRootPane().getChildren().add(rectangle);

        //Canvas Init
        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getRootPane().getChildren().add(canvas);

    }

    public void setGameEntity(Pacman pacman, World world, Ghost[] ghosts) {
        this.pacman = pacman;
        this.world = world;
        this.ghosts = ghosts;
    }


    /**
     * Set background for main pane
     */
    private void setBackGround() {
        BackgroundImage background = new BackgroundImage(ImageLibrary.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }


    /**
     * Drawing game each pulse
     */
    @Override
    public void render() {
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT); //clear last frame rendering

        if (isGridDisplayed)
            drawTileSystem();

        world.drawMap(gc);
        pacman.render(gc);
        for (Ghost ghost : ghosts) {
            ghost.render(gc);
        }
    }






    public void switchGridDisplay() {
        isGridDisplayed = !isGridDisplayed;
    }


    //Use only for testing things
    private void drawTileSystem() {
        for (int i = 0; i < TILES_X; i++) {
            for (int j = 0; j < TILES_Y; j++) {
                gc.setStroke(Color.color(0.12, 0.23, 0.12));
                gc.strokeRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

}
