package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public class GameView extends GeneralScene{
    public static final int GAME_WIDTH = TILE_SIZE * TILES_X;   //all size is pixel
    public static final int GAME_HEIGHT = TILE_SIZE * TILES_Y;

    private Canvas canvas;
    private GraphicsContext gc;


    private World world;
    private Pacman pacman;



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



        //World
        world = PacmanMap.createPacManWorld();

        //Pacman
        pacman = new Pacman("PACMAN");
    }


    /**
     * Set background for main pane
     */
    public void setBackGround() {
        BackgroundImage background = new BackgroundImage(ImageLibrary.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }


    public World getWorld() {
        return world;
    }

    public Pacman getPacman() {
        return pacman;
    }

    /**
     * Drawing game each pulse
     */
    @Override
    public void render() {
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT); //clear last frame rendering
        drawTileSystem();
        world.drawMap(gc);
        pacman.render(gc);
    }



    //Use only for testing things
    private void drawTileSystem() {
        for (int i = 0; i < TILES_X; i++) {
            for (int j = 0; j < TILES_Y; j++) {
                gc.setStroke(Color.GREEN);
                gc.strokeRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }
}
