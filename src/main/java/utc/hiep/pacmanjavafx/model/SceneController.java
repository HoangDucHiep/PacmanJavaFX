package utc.hiep.pacmanjavafx.model;

import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.scene.GeneralScene;

public class SceneController {
    private final Stage stage;
    public SceneController(Stage stage) {
        this.stage = stage;
    }

    public void setScene(GeneralScene scene) {
        stage.setScene(scene);
    }

    public void exit() {
        stage.close();
    }
}
