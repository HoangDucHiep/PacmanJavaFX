package utc.hiep.pacmanjavafx.scene;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import utc.hiep.pacmanjavafx.lib.settings;

import java.util.HashSet;
import java.util.Set;

//these weird import :__)
import static utc.hiep.pacmanjavafx.lib.settings.WINDOW_HEIGHT;
import static utc.hiep.pacmanjavafx.lib.settings.WINDOW_WIDTH;

public abstract class GeneralScene extends Scene {

    private StackPane root = new StackPane();
    //protected GraphicsContext gc;

    protected Set<KeyCode> activeKeys;
    protected Set<KeyCode> releasedKeys;


    public GeneralScene() {

        //Call for Scene's Constructor to initialize it
        super(new StackPane(), WINDOW_WIDTH, WINDOW_HEIGHT);

        //Change scene's root to our own stack pane
        root = new StackPane();
        this.setRoot(root);

        //Init activeKeys and releasedKeys
        activeKeys = new HashSet<>();
        releasedKeys = new HashSet<>();
        this.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        this.setOnKeyReleased(e -> {
            activeKeys.remove(e.getCode());
            releasedKeys.add(e.getCode());
        });
    }


    public abstract void draw();
}
