package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.EntityMovement;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.SceneController;
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

public class GameController {
    private final GameView gameView;
    private final ScoreScene scoreScene = new ScoreScene();
    private final WelcomeScene welcomeScene = new WelcomeScene();
    private SceneController sceneController;

    private List<KeyType> pressedKey = new Stack<>();

    private final World world;
    private Timer timer;
    private Pacman pacman;
    private Ghost[] ghosts;
    private GameLevel gameLevel;


    private KeyListener kl;

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
    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }
}
