package utc.hiep.pacmanjavafx;

import javafx.application.Application;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.model.SceneController;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.scene.GeneralScene;
import utc.hiep.pacmanjavafx.scene.ScoreScene;
import utc.hiep.pacmanjavafx.scene.WelcomeScene;

public class PacManApplication extends Application {

    private static final GameController gController = new GameController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Pacman");
        SceneController sController = new SceneController(stage);
        gController.setSceneController(sController);
        stage.setScene(gController.getGameView());
        stage.show();
    }




}
