package utc.hiep.pacmanjavafx;

import javafx.application.Application;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.controller.GameController;

public class PacManApplication extends Application {
    private static Stage stage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        PacManApplication.stage = stage;
        stage.setTitle("Pacman");
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setMaximized(true);

        GameController gController = new GameController();

        stage.getIcons().add(GameController.rm().getImage("app_icon"));
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }
}
