package utc.hiep.pacmanjavafx.scene;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import utc.hiep.pacmanjavafx.lib.Global;

import java.util.HashSet;
import java.util.Set;


public abstract class GeneralScene extends Scene {

    private Pane root;

    public GeneralScene() {

        super(new Group(), Global.WINDOW_WIDTH, Global.WINDOW_HEIGHT);
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