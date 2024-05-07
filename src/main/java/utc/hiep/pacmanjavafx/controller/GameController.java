package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.AudioPlayer;
import utc.hiep.pacmanjavafx.model.DatabaseControl;
import utc.hiep.pacmanjavafx.PacManApplication;
import utc.hiep.pacmanjavafx.event.GameEvent;
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

    private int currentScene = WELCOME_SCENE;

    private final Stage window = PacManApplication.getStage();

    private GameView gameView;
    private ScoreScene scoreScene;
    private WelcomeScene welcomeScene;

    private GameLevel gameLevel;

    public static ResourcesManeger rm = new ResourcesManeger();
    static {
        loadRS();
    }


    private HUD hud;

    //private DatabaseControl db;

    private long score;
    private int life;

    private long highScore;
    private int highLevel;


    private static void loadRS() {

        rm.addSound("munch", "doublemunch.wav");
        rm.addSound("eat_ghost", "eat_ghost.wav");
        rm.addSound("extend", "extend.wav");
        rm.addSound("game_over", "game_over.wav");
        rm.addSound("game_start", "game_start.wav");
        rm.addSound("ghost_turn_to_blue", "ghost_turn_to_blue.wav");
        rm.addSound("level_complete", "level_complete.wav");
        rm.addSound("pacman_die", "pacman_death.wav");
        rm.addSound("siren", "siren_1.wav");
        rm.addSound("retreat", "retreating.wav");

        rm.addImage("back_ground", "background.png");
        rm.addImage("pellet", "pellet.png");
        rm.addImage("energizer", "energizer_sheet.png");
        rm.addImage("menubg", "menuscenebg.png");
        rm.addImage("app_icon", "pacman.png");
        rm.addImage("map_sheet", "map_sheet.png");
        rm.addImage("sprite_sheet", "sprite_sheet.png", 224, 248);
        rm.addImage("game_logo", "game_logo.png", Global.TILE_SIZE * 40, Double.MIN_NORMAL);
        rm.addImage("score_logo", "high_score_logo.png", Global.TILE_SIZE * 40, Double.MIN_NORMAL);
        rm.addImage("pac_and_ghost", "pac_and_ghosts.gif", Global.TILE_SIZE * 20, Double.MIN_NORMAL);


        final int PACMAN_SIZE = 15;  //size of pacman int sprite_sheet.png
        final int GHOST_SIZE = 16;   //size of ghost in sprite_sheet.png
        final int ENERGIZER_SIZE = 8;


        /*For pacman*/
        final Animator.AnimatorPos[] PACMAN_RIGHT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 0),
                new Animator.AnimatorPos(0, 0),
                new Animator.AnimatorPos(16, 0),
                new Animator.AnimatorPos(32, 0)
        };

        final Animator.AnimatorPos[] PACMAN_LEFT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 0),
                new Animator.AnimatorPos(0, 16),
                new Animator.AnimatorPos(16, 16),
                new Animator.AnimatorPos(32, 0)
        };

        final Animator.AnimatorPos[] PACMAN_UP = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 0),
                new Animator.AnimatorPos(0, 32),
                new Animator.AnimatorPos(16, 32),
                new Animator.AnimatorPos(32, 0)
        };

        final Animator.AnimatorPos[] PACMAN_DOWN = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 0),
                new Animator.AnimatorPos(0, 48),
                new Animator.AnimatorPos(16, 48),
                new Animator.AnimatorPos(32, 0)
        };

        final Animator.AnimatorPos[] DIED_PACMAN_POS = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 0),
                new Animator.AnimatorPos(48, 0),
                new Animator.AnimatorPos(64, 0),
                new Animator.AnimatorPos(80, 0),
                new Animator.AnimatorPos(96, 0),
                new Animator.AnimatorPos(112, 0),
                new Animator.AnimatorPos(128, 0),
                new Animator.AnimatorPos(144, 0),
                new Animator.AnimatorPos(160, 0),
                new Animator.AnimatorPos(176, 0),
                new Animator.AnimatorPos(192, 0),
                new Animator.AnimatorPos(208, 0),
                new Animator.AnimatorPos(176, 32),
                new Animator.AnimatorPos(176, 32),
                new Animator.AnimatorPos(176, 32),
        };

        Image spriteSheet = rm.getImage("sprite_sheet");

        final Animator PACMAN_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.PACMAN_ANIMATION_RATE, PACMAN_SIZE, PACMAN_SIZE, PACMAN_LEFT, PACMAN_RIGHT, PACMAN_UP, PACMAN_DOWN);
        final Animator DIED_PACMAN_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.PAC_DIED_ANIMATION_RATE, PACMAN_SIZE, PACMAN_SIZE,  DIED_PACMAN_POS, DIED_PACMAN_POS, DIED_PACMAN_POS, DIED_PACMAN_POS);


        /* For energizer */
        final Animator.AnimatorPos[] ENERGIZER_ANIM_POS = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(0, 0),
                new Animator.AnimatorPos(9, 0),
        };


        /* For Map*/
        final Animator.AnimatorPos[] MAP_ANIM_POS = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(0, 0),
                new Animator.AnimatorPos(224, 0),
        };


        /* For GHOST */
        /* for RED one */
        final Animator.AnimatorPos[] RED_RIGHT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(0, 64),
                new Animator.AnimatorPos(16, 64),
        };

        final Animator.AnimatorPos[] RED_LEFT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 64),
                new Animator.AnimatorPos(48, 64),
        };

        final Animator.AnimatorPos[] RED_UP = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(64, 64),
                new Animator.AnimatorPos(80, 64),
        };

        final Animator.AnimatorPos[] RED_DOWN = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(96, 64),
                new Animator.AnimatorPos(112, 64),
        };
        /* for PINK one */
        final Animator.AnimatorPos[] PINK_LEFT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 80),
                new Animator.AnimatorPos(48, 80),
        };

        final Animator.AnimatorPos[] PINK_RIGHT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(0, 80),
                new Animator.AnimatorPos(16, 80),
        };

        final Animator.AnimatorPos[] PINK_UP = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(64, 80),
                new Animator.AnimatorPos(80, 80),
        };

        final Animator.AnimatorPos[] PINK_DOWN = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(96, 80),
                new Animator.AnimatorPos(112, 80),
        };
        /* for CYAN one */

        final Animator.AnimatorPos[] CYAN_LEFT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 96),
                new Animator.AnimatorPos(48, 96),
        };

        final Animator.AnimatorPos[] CYAN_RIGHT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(0, 96),
                new Animator.AnimatorPos(16, 96),
        };

        final Animator.AnimatorPos[] CYAN_UP = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(64, 96),
                new Animator.AnimatorPos(80, 96),
        };

        final Animator.AnimatorPos[] CYAN_DOWN = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(96, 96),
                new Animator.AnimatorPos(112, 96),
        };

        /* for ORANGE one */
        final Animator.AnimatorPos[] ORANGE_LEFT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(32, 112),
                new Animator.AnimatorPos(48, 112),
        };

        final Animator.AnimatorPos[] ORANGE_RIGHT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(0, 112),
                new Animator.AnimatorPos(16, 112),
        };

        final Animator.AnimatorPos[] ORANGE_UP = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(64, 112),
                new Animator.AnimatorPos(80, 112),
        };

        final Animator.AnimatorPos[] ORANGE_DOWN = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(96, 112),
                new Animator.AnimatorPos(112, 112),
        };

        /* for FRIGHTENED one */
        final Animator.AnimatorPos[] LATE_FRIGHTENED = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(128, 64),
                new Animator.AnimatorPos(144, 64),
                new Animator.AnimatorPos(160, 64),
                new Animator.AnimatorPos(176, 64),
        };

        final Animator.AnimatorPos[] FRIGHTENED = new Animator.AnimatorPos[]{
                new Animator.AnimatorPos(128, 64),
                new Animator.AnimatorPos(144, 64),
        };


        /*  for Eaten one */
        final Animator.AnimatorPos[] EATEN_RIGHT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(128, 80),
        };

        final Animator.AnimatorPos[] EATEN_LEFT = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(144, 80),
        };

        final Animator.AnimatorPos[] EATEN_UP = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(160, 80),
        };

        final Animator.AnimatorPos[] EATEN_DOWN = new Animator.AnimatorPos[] {
                new Animator.AnimatorPos(176, 80),
        };


        /**
         * Animator for Energizer
         */
        final Animator ENERGIZER_ANIMATOR = Animator.getNonDirAnimator(rm.getImage("energizer"), GameModel.ENERGIZER_BLINKING_RATE, ENERGIZER_SIZE, ENERGIZER_SIZE,  ENERGIZER_ANIM_POS);

        /**
         * Animator for Red-Ghosts
         */
        final Animator RED_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  RED_LEFT, RED_RIGHT, RED_UP, RED_DOWN);

        /**
         * Animator for Pink-Ghosts
         */
        final Animator PINK_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  PINK_LEFT, PINK_RIGHT, PINK_UP, PINK_DOWN);

        /**
         * Animator for Cyan-Ghosts
         */
        final Animator CYAN_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  CYAN_LEFT, CYAN_RIGHT, CYAN_UP, CYAN_DOWN);

        /**
         * Animator for Orange-Ghosts
         */
        final Animator ORANGE_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  ORANGE_LEFT, ORANGE_RIGHT, ORANGE_UP, ORANGE_DOWN);

        /**
         * Animator for Frightened-Ghosts
         */
        final Animator LATE_FRIGHTENED_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_FRIGHTENED_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  LATE_FRIGHTENED, LATE_FRIGHTENED, LATE_FRIGHTENED, LATE_FRIGHTENED);

        final Animator FRIGHTENED_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_FRIGHTENED_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  FRIGHTENED, FRIGHTENED, FRIGHTENED, FRIGHTENED);

        /**
         * Animator for Eaten-Ghosts
         */
        final Animator EATEN_ANIMATOR = Animator.getDirAnimator(spriteSheet, GameModel.GHOST_FRIGHTENED_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  EATEN_LEFT, EATEN_RIGHT, EATEN_UP, EATEN_DOWN);


        /**
         * Animator for Map
         */
        final Animator MAP_ANIMATOR = Animator.getNonDirAnimator(rm.getImage("map_sheet"), MAP_FLASHING_RATE, Global.ORIGINAL_TILE_SIZE * 28, Global.ORIGINAL_TILE_SIZE * 31,  MAP_ANIM_POS);

        /**
         * Get animator for ghost, index is one of
         * {@link GameModel#RED_GHOST},
         * {@link GameModel#PINK_GHOST},
         * {@link GameModel#CYAN_GHOST},
         * {@link GameModel#ORANGE_GHOST},
         * {@link GameModel#FRIGHTENED_GHOST}
         */
        final Animator[] GHOST_ANIMATOR = {RED_ANIMATOR, PINK_ANIMATOR, CYAN_ANIMATOR, ORANGE_ANIMATOR, FRIGHTENED_ANIMATOR, LATE_FRIGHTENED_ANIMATOR, EATEN_ANIMATOR};

        rm.addAnimator(GameModel.RED_GHOST, RED_ANIMATOR);
        rm.addAnimator(GameModel.PINK_GHOST, PINK_ANIMATOR);
        rm.addAnimator(GameModel.CYAN_GHOST, CYAN_ANIMATOR);
        rm.addAnimator(GameModel.ORANGE_GHOST, ORANGE_ANIMATOR);
        rm.addAnimator(GameModel.FRIGHTENED_GHOST, FRIGHTENED_ANIMATOR);
        rm.addAnimator(GameModel.LATE_FRIGTHENED_GHOST, LATE_FRIGHTENED_ANIMATOR);
        rm.addAnimator(GameModel.EATEN_GHOST, EATEN_ANIMATOR);
        rm.addAnimator(GameModel.PAC, PACMAN_ANIMATOR);
        rm.addAnimator(GameModel.DIED_PAC, DIED_PACMAN_ANIMATOR);
        rm.addAnimator(GameModel.MAP, MAP_ANIMATOR);
        rm.addAnimator(GameModel.ENERGIZEER, ENERGIZER_ANIMATOR);
    }



    public GameController() {
//        try {
//            db = new DatabaseControl();
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        //DatabaseControl.HighScore bestScore = db.scoreboard().get(0);
//        highScore = bestScore.score();
//        highLevel = bestScore.level();
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


    public static ResourcesManeger rm() {
        return rm;
    }



    public void playSound(String sound) {
        rm.getSound(sound).play();
    }

    public double playedSec(String sound) {
        return rm.getSound(sound).playedSec();
    }

    public void stopSound(String sound) {
        rm.getSound(sound).stop();
    }

}
