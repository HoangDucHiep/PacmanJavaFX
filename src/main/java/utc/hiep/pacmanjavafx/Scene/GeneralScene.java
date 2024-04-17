package utc.hiep.pacmanjavafx.Scene;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

public abstract class GeneralScene extends Scene {
    public static final int GAME_WIDTH = 816;
    public static final int GAME_HEIGHT = 480;

    private StackPane root = new StackPane();
    protected GraphicsContext gc;

    public GeneralScene() {
        //default constructor with default canvas size equal to game window
        this(GAME_WIDTH, GAME_HEIGHT);
    }

    public GeneralScene(int canvasWidth, int canvasHeight) {

        //Call for Scene's Constructor to initialize it
        super(new StackPane(), GAME_WIDTH, GAME_HEIGHT);

        //Change scene's root to our own stack pane
        root = new StackPane();
        this.setRoot(root);

        //Initialize canvas and graphics context
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        root.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();

    }


    public abstract void draw();
}
