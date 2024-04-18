package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.model.Timer;

public class GameController {
    private final GameView gameView;


    private final World map;
    private Timer timer;

    public GameController() {
        this.gameView = new GameView();
        timer = new Timer();

        map = gameView.getWorld();
        running();
    }



    public void running() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                timer.updateTimer(now);
                gameView.render();
                System.out.println("Current tick: " + timer.getTick());
            }
        }.start();
    }

    public GameView getGameView() {
        return gameView;
    }
}
