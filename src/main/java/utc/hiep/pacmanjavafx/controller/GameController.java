package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.event.GameEvent;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.event.MouseListener;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.EntityMovement;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.SceneControl;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.scene.ScoreScene;
import utc.hiep.pacmanjavafx.scene.WelcomeScene;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class GameController {

    private static int GAME_VIEW = 0;
    private static int SCORE_SCENE = 1;
    private static int WELCOME_SCENE = 2;
    private int currentScene = WELCOME_SCENE;

    private final GameView gameView;
    private final ScoreScene scoreScene = new ScoreScene();
    private final WelcomeScene welcomeScene = new WelcomeScene();
    private SceneControl sceneControl;

    private List<KeyType> pressedKey = new Stack<>();

    private Timer timer;
    private GameLevel gameLevel;

    /*Event listener*/
    //GameView event listener
    private KeyListener gameViewKL;

    //MenuScene event listener
    private MouseListener startBtn;
    private MouseListener scoreBtn;
    private MouseListener exitBtn;

    private int score;

    public GameController() {
        this.gameView = new GameView();
        timer = new Timer();
        score = 0;

        gameLevel = new GameLevel();

        gameView.setGameLevel(gameLevel);

        gameViewKL = new KeyListener(gameView);
        gameViewKL.setKeyAction(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP -> gameViewKL.getPressedKey().add(KeyType.TURN_UP);
                case DOWN -> gameViewKL.getPressedKey().add(KeyType.TURN_DOWN);
                case LEFT -> gameViewKL.getPressedKey().add(KeyType.TURN_LEFT);
                case RIGHT -> gameViewKL.getPressedKey().add(KeyType.TURN_RIGHT);
                case P -> gameViewKL.getPressedKey().add(KeyType.PAUSE);
                case G -> gameViewKL.getPressedKey().add(KeyType.GRID_SWITCH);
                case C -> gameViewKL.getPressedKey().add(KeyType.CHANGE_SCENE);
                case B -> gameViewKL.getPressedKey().add(KeyType.CHANGE_BACK);
            }
        });


        startBtn = new MouseListener(welcomeScene.getStartButton());
        startBtn.setMouseAction(
                mouseEvent -> {
                    sceneControl.setScene(gameView);
                    currentScene = GAME_VIEW;
                },
                mouseEvent -> welcomeScene.getStartButton().setTextFill(Color.color(1, 0.71, 1)),
                mouseEvent -> welcomeScene.getStartButton().setTextFill(Color.WHITE)
        );


        exitBtn = new MouseListener(welcomeScene.getExitButton());
        exitBtn.setMouseAction(
                mouseEvent -> sceneControl.exit(),
                mouseEvent -> welcomeScene.getExitButton().setTextFill(Color.color(1, 0.71, 1)),
                mouseEvent -> welcomeScene.getExitButton().setTextFill(Color.WHITE)
        );

        scoreBtn = new MouseListener(welcomeScene.getScoreButton());
        scoreBtn.setMouseAction(
                mouseEvent -> sceneControl.setScene(scoreScene),
                mouseEvent -> welcomeScene.getScoreButton().setTextFill(Color.color(1, 0.71, 1)),
                mouseEvent -> welcomeScene.getScoreButton().setTextFill(Color.WHITE)
        );


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
        if(currentScene == GAME_VIEW) {
            keyHandler();
            if(gameLevel.currentEvent() == GameEvent.LEVEL_STARTED) {
                updateAnimator();
                if (timer.isPaused()) {
                    return;
                }
            } else if (gameLevel.currentEvent() == GameEvent.LEVEL_LOST) {
                sceneControl.setScene(scoreScene);
            }
            gameLevel.update();
        }
    }




    private void updateAnimator() {
        gameLevel.pacman().animatorUpdate();
        gameLevel.world().animatorUpdate();
        Arrays.stream(gameLevel.ghosts()).forEach(Ghost::animatorUpdate);
    }


    public void keyHandler() {
        gameViewKL.keyListening();
        System.out.println(gameViewKL.getPressedKey());
        for (KeyType key : gameViewKL.getPressedKey()) {
            switch (key) {
                case PAUSE:
                    timer.switchPause();
                case GRID_SWITCH:
                    gameView.switchGridDisplay();
            }

            if(!timer.isPaused()) {
                switch (key) {
                    case TURN_UP -> gameLevel.applyPacDirKey(Direction.UP);
                    case TURN_DOWN -> gameLevel.applyPacDirKey(Direction.DOWN);
                    case TURN_LEFT -> gameLevel.applyPacDirKey(Direction.LEFT);
                    case TURN_RIGHT -> gameLevel.applyPacDirKey(Direction.RIGHT);
                    case CHANGE_SCENE -> sceneControl.setScene(welcomeScene);
                    case CHANGE_BACK -> sceneControl.setScene(gameView);
                }
            }
        }
        gameViewKL.clearKey();
    }

    public GameView getGameView() {
        return gameView;
    }

    public ScoreScene getScoreScene() {
        return scoreScene;
    }

    public WelcomeScene getWelcomeScene() {
        return welcomeScene;
    }


    public void setSceneControl(SceneControl sceneControl) {
        this.sceneControl = sceneControl;
    }


}
