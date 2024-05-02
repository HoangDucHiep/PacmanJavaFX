package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class GameController {
    private final GameView gameView;
    private final ScoreScene scoreScene = new ScoreScene();
    private final WelcomeScene welcomeScene = new WelcomeScene();
    private SceneControl sceneControl;

    private List<KeyType> pressedKey = new Stack<>();

    private final World world;
    private Timer timer;
    private Pacman pacman;
    private Ghost[] ghosts;
    private GameLevel gameLevel;


    private KeyListener gameViewKL;


    private MouseListener startBtn;
    private MouseListener scoreBtn;
    private MouseListener exitBtn;

    private int score;

    public GameController() {
        this.gameView = new GameView();
        timer = new Timer();
        score = 0;

        gameLevel = new GameLevel();

        world = gameLevel.world();
        pacman = gameLevel.pacman();
        ghosts = gameLevel.ghosts();

        gameView.setGameEntity(pacman, world, ghosts);
        gameView.setGameLevel(gameLevel);

        gameViewKL = new KeyListener(gameView);
        gameViewKL.setKeyAction(new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent keyEvent) {
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
            }
        });


        startBtn = new MouseListener(welcomeScene.getStartButton());
        startBtn.setMouseAction(new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                sceneControl.setScene(gameView);

            }
        }, new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                welcomeScene.getStartButton().setTextFill(Color.color(1, 0.71, 1));
            }
        }, new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                welcomeScene.getStartButton().setTextFill(Color.WHITE);
            }
        });


        exitBtn = new MouseListener(welcomeScene.getExitButton());
        exitBtn.setMouseAction(new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                sceneControl.exit();
            }
        }, new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                welcomeScene.getExitButton().setTextFill(Color.color(1, 0.71, 1));
            }
        }, new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                welcomeScene.getExitButton().setTextFill(Color.WHITE);
            }
        });

        scoreBtn = new MouseListener(welcomeScene.getScoreButton());
        scoreBtn.setMouseAction(new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                sceneControl.setScene(scoreScene);
            }
        }, new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                welcomeScene.getScoreButton().setTextFill(Color.color(1, 0.71, 1));
            }
        }, new Consumer<MouseEvent>() {
            @Override
            public void accept(MouseEvent mouseEvent) {
                welcomeScene.getScoreButton().setTextFill(Color.WHITE);
            }
        });



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
        movePacman();
        handlePacmanEatFoot();
        gameLevel.update();
    }

    public void movePacman() {
        EntityMovement.move(pacman, world);
//        iVector2D currentTile = pacman.atTile();
//
//        /* Handle turn back instantly */
//        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
//            pacman.turnBackInstantly();
//            return;
//        }
//
//        /*  handle pacman be blocked by wall or smth */
//        if(!pacman.canAccessTile(pacman.tilesAhead(1), world) && pacman.offset().almostEquals(fVector2D.ZERO, pacman.currentSpeed(),  pacman.currentSpeed())) {
//            if(!pacman.isStanding()) {
//                pacman.centerOverTile(currentTile);
//                pacman.standing();
//            }
//        }
//        /*  handle pacman at intersection */
//        else if(world.isIntersection(currentTile)) {
//            //if pacman haven't aligned to tile, but almost aligned, then aligned it
//            if(pacman.isNewTileEntered() && pacman.offset().almostEquals(fVector2D.ZERO, pacman.currentSpeed(), pacman.currentSpeed())) {
//                pacman.placeAtTile(currentTile.toFloatVec());
//            }
//        }
//        //Handle if pacman gothrough portal
//        else if(world.belongsToPortal(currentTile)) {
//            if(!world.belongsToPortal(pacman.tilesAhead(1))) {
//                iVector2D teleportTo = world.portals().otherTunnel(currentTile);
//                pacman.placeAtTile(teleportTo.toFloatVec());
//            }
//        }
//
//        /* Handle if pacman be blocked in next turn, it'll keep moving in current direction*/
//        if(pacman.isAlignedToTile()) {
//            if(pacman.canAccessTile(currentTile.plus(pacman.nextDir().vector()), world)) {
//                pacman.setMovingDir(pacman.nextDir());
//            }
//        }
//
//
//        // If pacman is not standing, it can move :)))
//        if(!pacman.isStanding()) {
//            pacman.move();
//        }
    }


    private void handlePacmanEatFoot() {
        iVector2D currentTile = pacman.atTile();
        if(world.hasFoodAt(currentTile) && !world.hasEatenFoodAt(currentTile)) {
            gameLevel.houseControl().updateDotCount(gameLevel);
            world.removeFood(currentTile);
            pacman.endStarving();
        }
        else {
            pacman.starve();
        }
    }


    private void updateAnimator() {
        pacman.animatorUpdate();
        world.animatorUpdate();
    }


    public void keyHandler() {
        gameViewKL.keyListening();

        for (KeyType key : gameViewKL.getPressedKey()) {
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
