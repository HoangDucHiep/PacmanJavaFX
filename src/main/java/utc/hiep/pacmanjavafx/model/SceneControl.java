package utc.hiep.pacmanjavafx.model;

import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.scene.GeneralScene;

public class SceneControl {
    private final Stage stage;
    public SceneControl(Stage stage) {
        this.stage = stage;
    }

    public void setScene(GeneralScene scene) {
        // Đặt scene mới
        stage.setScene(scene);
    }

    public void exit() {
        stage.close();
    }


}
