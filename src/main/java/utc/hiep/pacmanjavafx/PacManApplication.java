package utc.hiep.pacmanjavafx;

import javafx.application.Application;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.lib.ImageLib;

public class PacManApplication extends Application {


    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("Pacman");
        stage.getIcons().add(ImageLib.APP_ICON);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setMaximized(true);

        PacManApplication.stage = stage;

        GameController gController = new GameController();
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }
}
