package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import utc.hiep.pacmanjavafx.lib.IMAGE_LIB;
import utc.hiep.pacmanjavafx.model.world.World;

public class GameScene extends GeneralScene{
    public static final int GAME_WIDTH = 448;   //all size is pixel
    public static final int GAME_HEIGHT = 576;

    //private AnchorPane gameBorder;
    private Canvas canvas;
    private GraphicsContext gc;


    //Object in game
    private World world;


    public GameScene() {
        super();
        setBackGround();
        Region rectangle = new Region();
        rectangle.setStyle("-fx-background-color: white, black; -fx-background-insets: 0, 12; -fx-background-radius: 10px; -fx-min-width: 520; -fx-min-height:669; -fx-max-width:480; -fx-max-height: 669;");
        getRootPane().getChildren().add(rectangle);

        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getRootPane().getChildren().add(canvas);
//        gc.setFill(Color.RED);
//        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        world = new World();
        world.drawMap(gc);

    }


    //Set background for mainPain - game window background
    public void setBackGround() {
        BackgroundImage background = new BackgroundImage(IMAGE_LIB.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }


    @Override
    public void draw() {

    }
}
