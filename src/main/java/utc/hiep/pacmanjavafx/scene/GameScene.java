package utc.hiep.pacmanjavafx.scene;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utc.hiep.pacmanjavafx.lib.IMAGE_LIB;

import static utc.hiep.pacmanjavafx.lib.settings.*;

public class GameScene extends GeneralScene{
    public static final int GAME_WIDTH = 448;   //all size is pixel
    public static final int GAME_HEIGHT = 576;

    //private AnchorPane gameBorder;
    private Canvas canvas;
    private GraphicsContext gc;


    public GameScene() {
        super();
        setBackGround();
        Region rectangle = new Region();
        rectangle.setStyle("-fx-background-color: white, black; -fx-background-insets: 0, 12; -fx-background-radius: 10px; -fx-min-width: 520; -fx-min-height:669; -fx-max-width:480; -fx-max-height: 669;");
        getRootPane().getChildren().add(rectangle);

        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getRootPane().getChildren().add(canvas);
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        drawMap();

    }


    private void drawMap() {
        Image map = IMAGE_LIB.MAP_EMPTY;
        gc.drawImage(map, 0, TILE_SIZE * 3, MAP_WIDTH, MAP_HEIGHT);
    }


    public void setBackGround() {
        BackgroundImage background = new BackgroundImage(IMAGE_LIB.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }


    @Override
    public void draw() {

    }
}
