package utc.hiep.pacmanjavafx.scene;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.HashSet;
import java.util.Set;

import static utc.hiep.pacmanjavafx.lib.Global.WINDOW_HEIGHT;
import static utc.hiep.pacmanjavafx.lib.Global.WINDOW_WIDTH;

public abstract class GeneralScene extends Scene {

    private Pane root;

    public GeneralScene() {
        super(new Group(), WINDOW_WIDTH, WINDOW_HEIGHT);
        setRootPane(new StackPane());
    }

    public Pane getRootPane() {
        return root;
    }

    public void setRootPane(Pane root) {
        this.root = root;
        this.setRoot(root);
    }
    public abstract void render();
}