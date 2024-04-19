package utc.hiep.pacmanjavafx;

import javafx.application.Application;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.scene.GeneralScene;
import utc.hiep.pacmanjavafx.scene.ScoreScene;
import utc.hiep.pacmanjavafx.scene.WelcomeScene;

public class PacManApplication extends Application {
    public static final int MAX_SCENES = 3;
    public static final int WELCOME_SCENE = 0;
    public static final int GAME_SCENE = 1;
    public static final int SCORE_SCENE = 2;

    public static final GeneralScene[] scenes = new GeneralScene[MAX_SCENES];

    private static Stage stage;

    private static GameController gController = new GameController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        PacManApplication.stage = stage;

        scenes[0] = new WelcomeScene();
        scenes[1] = gController.getGameView();
        scenes[2] = new ScoreScene();


        stage.setTitle("Pacman");
        setScene(GAME_SCENE);
        stage.show();
    }


    public static void setScene(int numScene) {
        stage.setScene(scenes[numScene]);
    }

    public static void exit() {
        stage.close();
    }
}
