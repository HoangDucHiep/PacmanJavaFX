package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;

public class GameController {
    GameView gameView;
    World map;

    private static final long ONE_SECOND = 1_000_000_000; // One second in nanoseconds

    private long lastUpdate = 0;
    private int seconds = 0;

    public GameController() {
        this.gameView = new GameView();
        map = gameView.getWorld();
        running();
    }



    public void running() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= ONE_SECOND) {
                    seconds++;
                    System.out.println("Seconds: " + seconds);
                    lastUpdate = now;
                }
                gameView.render();
            }
        }.start();
    }

    public GameView getGameView() {
        return gameView;
    }
}
