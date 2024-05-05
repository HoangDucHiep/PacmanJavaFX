package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.PacManApplication;
import utc.hiep.pacmanjavafx.event.GameEvent;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.event.MouseListener;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Global;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.HUD;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.level.LevelState;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.scene.ScoreScene;
import utc.hiep.pacmanjavafx.scene.WelcomeScene;

import java.util.Arrays;

public class GameController implements GameModel {
    public static final byte GAME_VIEW = 0;
    public static final byte SCORE_SCENE = 1;
    public static final byte WELCOME_SCENE = 2;

    private int currentScene = WELCOME_SCENE;

    private final Stage window = PacManApplication.getStage();

    private GameView gameView;
    private ScoreScene scoreScene;
    private WelcomeScene welcomeScene;

    private GameLevel gameLevel;

    /*Event listener*/
    //GameView event listener
    private KeyListener gameViewKL;

    //MenuScene event listener
    private MouseListener startBtn;
    private MouseListener scoreBtn;
    private MouseListener exitBtn;

    private HUD hud;

    private long score;
    private int life;

    private long highScore;
    private int highLevel;


    public GameController() {

        changeScene(WELCOME_SCENE);
    }

    private void initNewGame() {
        score = 0;
        life = 3;

        hud = new HUD(this);

        highScore = 3000;
        highLevel = 1;

        gameLevel = new GameLevel(this);

        changeScene(GAME_VIEW);
        currentScene = GAME_VIEW;
        runGame();
    }

    private void runGame() {
        new AnimationTimer() {
            private static final float timeStep = 1.0f / 60;   //FPS = 60, 1.0f is one second, 0.01667f
            private float accumulatedTime = 0;

            private long previousTime = 0;
            final float maxDelta = 0.05f;

            @Override
            public void handle(long currentTime) {
                System.out.println(accumulatedTime);
                if (previousTime == 0) {
                    previousTime = currentTime;
                    return;
                }

                float secondsElapsed = (currentTime - previousTime) / 1e9f;
                float secondsElapsedCapped = Math.min(secondsElapsed, maxDelta);
                accumulatedTime += secondsElapsedCapped;
                previousTime = currentTime;

                while (accumulatedTime >= timeStep) {
                    updateGame();
                    accumulatedTime -= timeStep;
                }

                gameView.render();
            }

            @Override
            public void stop() {
                previousTime = 0;
                accumulatedTime = 0;
                super.stop();
            }
        }.start();
    }


    private void updateGame() {
        if(currentScene == GAME_VIEW) {
            keyHandler();
            if((gameLevel.currentState() == LevelState.LEVEL_STARTED && (gameLevel.currentEvent() != GameEvent.PAC_DIED && gameLevel.currentEvent() != GameEvent.GAME_WIN) || gameLevel.currentState() == LevelState.LEVEL_PAUSED)) {
                updateAnimator();
            }
            if (gameLevel.currentState() == LevelState.LEVEL_LOST) {
                changeScene(SCORE_SCENE);
                scoreScene.showNewScoreScene();
                currentScene = SCORE_SCENE;
            }

            if(gameLevel.currentState() == LevelState.LEVEL_WON) {
                if(gameLevel.levelNum() == GameModel.MAX_LEVEL) {          //reached max level, show score scene
                    changeScene(SCORE_SCENE);
                    scoreScene.showNewScoreScene();
                } else {
                    gameLevel = gameLevel.nextLevel();                      //come to next level
                }
            }

            gameLevel.update();
            hud.update();
        }
    }


    private void updateAnimator() {
        gameLevel.pacman().animatorUpdate();
        gameLevel.world().animatorUpdate();
        Arrays.stream(gameLevel.ghosts()).forEach(Ghost::animatorUpdate);
    }

    public void keyHandler() {

        if(gameLevel.currentState() == LevelState.LEVEL_READY) {
            return;
        }

        gameViewKL.keyListening();
        for (KeyType key : gameViewKL.getPressedKey()) {
            switch (key) {
                    case PAUSE -> gameLevel.switchPause();
                    case GRID_SWITCH -> gameView.switchGridDisplay();
                    case TURN_UP -> gameLevel.applyPacDirKey(Direction.UP);
                    case TURN_DOWN -> gameLevel.applyPacDirKey(Direction.DOWN);
                    case TURN_LEFT -> gameLevel.applyPacDirKey(Direction.LEFT);
                    case TURN_RIGHT -> gameLevel.applyPacDirKey(Direction.RIGHT);
                }
            }
        gameViewKL.clearKey();
    }


    public long score() {
        return score;
    }

    public int lives() {
        return life;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void removeLife() {
        life = life == 0 ? 0 : life - 1;
    }

    public void addLife() {
        life++;
    }

    public long highScore() {
        return highScore;
    }

    public int highLevel() {
        return highLevel;
    }

    public GameLevel gameLevel() {
        return gameLevel;
    }

    public HUD hud() {
        return hud;
    }


    public void changeScene(byte scene) {
        if(scene == GAME_VIEW) {
            if(gameLevel == null || Global.WINDOW_WIDTH != window.getWidth() || Global.WINDOW_HEIGHT != window.getHeight()) {
                Global.WINDOW_WIDTH = (int) window.getWidth();
                Global.WINDOW_HEIGHT = (int) window.getHeight();
                gameView = new GameView(this);

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
            }
            window.setScene(gameView);
            currentScene = GAME_VIEW;



        } else if(scene == SCORE_SCENE) {
            if (scoreScene == null || Global.WINDOW_WIDTH != window.getWidth() || Global.WINDOW_HEIGHT != window.getHeight()) {
                Global.WINDOW_WIDTH = (int) window.getWidth();
                Global.WINDOW_HEIGHT = (int) window.getHeight();
                scoreScene = new ScoreScene(this);
            }
            window.setScene(scoreScene);
            currentScene = SCORE_SCENE;
        } else if(scene == WELCOME_SCENE) {
            if (welcomeScene == null || Global.WINDOW_WIDTH != window.getWidth() || Global.WINDOW_HEIGHT != window.getHeight()) {
                Global.WINDOW_WIDTH = (int) window.getWidth();
                Global.WINDOW_HEIGHT = (int) window.getHeight();
                welcomeScene = new WelcomeScene();


                startBtn = new MouseListener(welcomeScene.getStartButton());
                startBtn.setMouseAction(
                        mouseEvent -> {
                            initNewGame();
                        },
                        mouseEvent -> welcomeScene.getStartButton().setTextFill(Color.color(1, 0.71, 1)),
                        mouseEvent -> welcomeScene.getStartButton().setTextFill(Color.WHITE)
                );



                exitBtn = new MouseListener(welcomeScene.getExitButton());
                exitBtn.setMouseAction(
                        mouseEvent -> System.exit(0),
                        mouseEvent -> welcomeScene.getExitButton().setTextFill(Color.color(1, 0.71, 1)),
                        mouseEvent -> welcomeScene.getExitButton().setTextFill(Color.WHITE)
                );

                scoreBtn = new MouseListener(welcomeScene.getScoreButton());
                scoreBtn.setMouseAction(
                        mouseEvent -> changeScene(SCORE_SCENE),
                        mouseEvent -> welcomeScene.getScoreButton().setTextFill(Color.color(1, 0.71, 1)),
                        mouseEvent -> welcomeScene.getScoreButton().setTextFill(Color.WHITE)
                );
            }
            window.setScene(welcomeScene);
            currentScene = WELCOME_SCENE;


        }
    }


}
