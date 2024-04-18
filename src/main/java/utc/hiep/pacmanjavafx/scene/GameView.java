package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

public class GameView extends GeneralScene{
    public static final int GAME_WIDTH = 448;   //all size is pixel
    public static final int GAME_HEIGHT = 576;

    private Canvas canvas;
    private GraphicsContext gc;


    private World world;



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

    /**
     * Drawing game each pulse
     */
    @Override
    public void render() {
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT); //clear last frame rendering
        world.drawMap(gc);
    }
}
