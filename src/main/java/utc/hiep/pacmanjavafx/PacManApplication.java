package utc.hiep.pacmanjavafx;

import javafx.application.Application;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.model.SceneControl;

public class PacManApplication extends Application {

    private static final GameController gController = new GameController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Pacman");
        stage.setMaximized(true);
        SceneControl sController = new SceneControl(stage);
        gController.setSceneControl(sController);
        sController.setScene(gController.getWelcomeScene());
        stage.show();
    }
}
