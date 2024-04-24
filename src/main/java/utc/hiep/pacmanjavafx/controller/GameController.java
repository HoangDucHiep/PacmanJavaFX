package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.model.Timer;

import java.util.List;
import java.util.Stack;

public class GameController {
    private final GameView gameView;
    private List<KeyType> pressedKey = new Stack<>();

    private final World map;
    private Timer timer;
    private Pacman pacman;

    private GameLevel gameLevel;


    private KeyListener kl;

    private int score;

    public GameController() {
        this.gameView = new GameView();
        timer = new Timer();
        score = 0;

        gameLevel = new GameLevel();

        map = gameLevel.getWorld();
        pacman = gameLevel.getPacman();

        gameView.setGameEntity(pacman, map);

        kl = new KeyListener(gameView);
        running();
    }

    private void running() {
        new AnimationTimer() {
            private static final float timeStep = 1.0f / 60;   //FPS = 60, 1.0f is one second, 0.01667f
            private float accumulatedTime = 0;

            private long previousTime = 0;
            final float maxDelta = 0.05f;

            @Override
            public void handle(long currentTime)
            {
                if (previousTime == 0) {
                    previousTime = currentTime;
                    return;
                }

                float secondsElapsed = (currentTime - previousTime) / 1e9f;
                float secondsElapsedCapped = Math.min(secondsElapsed, maxDelta);
                accumulatedTime += secondsElapsedCapped;
                previousTime = currentTime;

                while (accumulatedTime >= timeStep) {
                    update();
                    accumulatedTime -= timeStep;
                }

                gameView.render();
            }

            @Override
            public void stop()
            {
                previousTime = 0;
                accumulatedTime = 0;
                super.stop();
            }
        }.start();
    }


    private void update() {
        timer.updateTimer();
        keyHandler();
        updateAnimator();
        if (timer.isPaused()) {
            return;
        }
        gameLevel.update();
    }





    private void updateAnimator() {
        pacman.animatorUpdate();
        map.animatorUpdate();
    }






    public void keyHandler() {
        kl.keyListening();
        pressedKey = kl.getPressedKey();

        for (KeyType key : pressedKey) {
            if(key == KeyType.PAUSE) {
                timer.switchPause(System.nanoTime());
            }
            if(!timer.isPaused()) {
                switch (key) {
                    case TURN_UP -> pacman.setNextDir(Direction.UP);
                    case TURN_DOWN -> pacman.setNextDir(Direction.DOWN);
                    case TURN_LEFT -> pacman.setNextDir(Direction.LEFT);
                    case TURN_RIGHT -> pacman.setNextDir(Direction.RIGHT);
                    case GRID_SWITCH -> gameView.switchGridDisplay();
                }
            }
        }
        kl.clearKey();
    }

    public GameView getGameView() {
        return gameView;
    }
}
