package utc.hiep.pacmanjavafx;

import javafx.application.Application;
import javafx.stage.Stage;

public class PacManApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Pacman");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
