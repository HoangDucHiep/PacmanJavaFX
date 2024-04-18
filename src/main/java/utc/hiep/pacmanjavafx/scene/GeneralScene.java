package utc.hiep.pacmanjavafx.scene;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.HashSet;
import java.util.Set;

import static utc.hiep.pacmanjavafx.lib.settings.WINDOW_HEIGHT;
import static utc.hiep.pacmanjavafx.lib.settings.WINDOW_WIDTH;

public abstract class GeneralScene extends Scene {

    protected Set<KeyCode> activeKeys;
    protected Set<KeyCode> releasedKeys;
    private StackPane root;

    public GeneralScene() {
        super(new Group(), WINDOW_WIDTH, WINDOW_HEIGHT);
        root = new StackPane();
        this.setRoot(root);
        activeKeys = new HashSet<>();
        releasedKeys = new HashSet<>();
        this.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        this.setOnKeyReleased(e -> {
            activeKeys.remove(e.getCode());
            releasedKeys.add(e.getCode());
        });
    }

    public StackPane getRootPane() {
        return root;
    }

    public abstract void draw();
}