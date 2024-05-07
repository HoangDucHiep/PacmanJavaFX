package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.lib.AudioLib;
import utc.hiep.pacmanjavafx.model.AudioPlayer;
import utc.hiep.pacmanjavafx.model.DatabaseControl;
import utc.hiep.pacmanjavafx.PacManApplication;
import utc.hiep.pacmanjavafx.event.GameEvent;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Global;
import utc.hiep.pacmanjavafx.model.HUD;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.level.LevelState;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.scene.ScoreScene;
import utc.hiep.pacmanjavafx.scene.WelcomeScene;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameController implements GameModel {
    public static final byte GAME_VIEW = 0;
    public static final byte SCORE_SCENE = 1;
    public static final byte WELCOME_SCENE = 2;

    public static final String MUNCH = "doublemunch.wav";
    public static final String EAT_GHOST = "eat_ghost.wav";
    public static final String EXTEND = "extend.wav";
    public static final String GAME_OVER = "game_over.wav";
    public static final String GAME_START = "game_start.wav";
    public static final String GHOST_TURN_BLUE = "ghost_turn_to_blue.wav";
    public static final String LEVEL_COMPLETE = "level_complete.wav";
    public static final String PACMAN_DIE = "pacman_death.wav";
    public static final String SIREN = "siren_1.wav";
    public static final String RETREAT = "retreating.wav";

    private int currentScene = WELCOME_SCENE;

    private final Stage window = PacManApplication.getStage();

    private GameView gameView;
    private ScoreScene scoreScene;
    private WelcomeScene welcomeScene;

    private GameLevel gameLevel;

    private HUD hud;

    //private DatabaseControl db;

    private long score;
    private int life;

    private long highScore;
    private int highLevel;

    private Map<String, AudioLib> audioMap = new HashMap<>();


    public GameController() {
//        try {
//            db = new DatabaseControl();
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        //DatabaseControl.HighScore bestScore = db.scoreboard().get(0);
//        highScore = bestScore.score();
//        highLevel = bestScore.level();

        audioMap.put(MUNCH, new AudioLib(MUNCH));
        audioMap.put(EAT_GHOST, new AudioLib(EAT_GHOST));
        audioMap.put(EXTEND, new AudioLib(EXTEND));
        audioMap.put(GAME_OVER, new AudioLib(GAME_OVER));
        audioMap.put(GAME_START, new AudioLib(GAME_START));
        audioMap.put(GHOST_TURN_BLUE, new AudioLib(GHOST_TURN_BLUE));
        audioMap.put(LEVEL_COMPLETE, new AudioLib(LEVEL_COMPLETE));
        audioMap.put(PACMAN_DIE, new AudioLib(PACMAN_DIE));
        audioMap.put(SIREN, new AudioLib(SIREN));
        audioMap.put(RETREAT, new AudioLib(RETREAT));

        changeScene(WELCOME_SCENE);
    }

    public void initNewGame() {
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
        HashSet<KeyCode> pressedKey =  gameView.getPressedKeys();
        for (KeyCode key : pressedKey) {
            switch (key) {
                case P -> gameLevel.switchPause();
                case G -> gameView.switchGridDisplay();
                case W, UP -> gameLevel.applyPacDirKey(Direction.UP);
                case S, DOWN -> gameLevel.applyPacDirKey(Direction.DOWN);
                case A, LEFT -> gameLevel.applyPacDirKey(Direction.LEFT);
                case D, RIGHT -> gameLevel.applyPacDirKey(Direction.RIGHT);
            }
        }
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
            }
            currentScene = GAME_VIEW;
            window.setScene(gameView);

        } else if(scene == SCORE_SCENE) {
            if (scoreScene == null || Global.WINDOW_WIDTH != window.getWidth() || Global.WINDOW_HEIGHT != window.getHeight()) {
                Global.WINDOW_WIDTH = (int) window.getWidth();
                Global.WINDOW_HEIGHT = (int) window.getHeight();
                scoreScene = new ScoreScene(this);
            }
            currentScene = SCORE_SCENE;
            window.setScene(scoreScene);


        } else if(scene == WELCOME_SCENE) {
            if (welcomeScene == null || Global.WINDOW_WIDTH != window.getWidth() || Global.WINDOW_HEIGHT != window.getHeight()) {
                Global.WINDOW_WIDTH = (int) window.getWidth();
                Global.WINDOW_HEIGHT = (int) window.getHeight();
                welcomeScene = new WelcomeScene(this);
            }

            currentScene = WELCOME_SCENE;
            window.setScene(welcomeScene);
        }
    }


    public DatabaseControl db() {
        //return db;
        return null;
    }




    public void playSound(String sound) {
        if(!audioMap.get(sound).isPLaying()) {
            audioMap.get(sound).getAudioClip().setCycleCount(AudioClip.INDEFINITE);
            audioMap.get(sound).play();
        }
    }

    public double playedSec(String sound) {
        return audioMap.get(sound).playedSec();
    }

    public void stopSound(String sound) {
        if(audioMap.get(sound).isPLaying()) {
            audioMap.get(sound).stop();
        }
    }

}
