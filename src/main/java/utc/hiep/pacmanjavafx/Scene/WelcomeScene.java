package utc.hiep.pacmanjavafx.Scene;

public class WelcomeScene extends GeneralScene {

    public WelcomeScene() {
        super();
        showWelcomeMessage();
    }

    private void showWelcomeMessage() {
        gc.fillText("Welcome to Pacman", 100, 100);
        gc.fillText("Press Enter to start", 100, 150);
    }

    @Override
    public void draw() {
        showWelcomeMessage();
    }
}
