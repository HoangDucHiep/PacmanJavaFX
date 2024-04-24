package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.model.Timer;

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import static utc.hiep.pacmanjavafx.event.KeyType.GRID_SWITCH;
import static utc.hiep.pacmanjavafx.model.level.GameModel.*;

public class GameController {
    private final GameView gameView;

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
        map = gameView.getWorld();
        pacman = gameView.getPacman();
        kl = new KeyListener(gameView);
        gameLevel = new GameLevel(new GameLevel.Data(1, false, RAW_LEVEL_DATA[1]),  map, pacman, kl);
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
        if (timer.isPaused()) {
            return;
        }
        gameLevel.update();
    }



    public void movePacman() {
        Vector2i currentTile = pacman.atTile();

        /* Handle turn back instantly */
        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
            pacman.turnBackInstantly();
            return;
        }

        /*  handle pacman be blocked by wall or smth */
        if(!pacman.canAccessTile(pacman.tilesAhead(1), map) && pacman.offset().almostEquals(Vector2f.ZERO, pacman.currentSpeed(),  pacman.currentSpeed())) {
            if(!pacman.isStanding()) {
                pacman.placeAtTile(currentTile.toFloatVec());
                pacman.standing();
            }
        }
        /*  handle pacman at intersection */
        else if(map.isIntersection(currentTile)) {
            //if pacman haven't aligned to tile, but almost aligned, then aligned it
            if(pacman.isNewTileEntered() && pacman.offset().almostEquals(Vector2f.ZERO, pacman.currentSpeed(), pacman.currentSpeed())) {
                pacman.placeAtTile(currentTile.toFloatVec());
            }
        }
        //Handle if pacman gothrough portal
        else if(map.belongsToPortal(currentTile)) {
            if(!map.belongsToPortal(pacman.tilesAhead(1))) {
                Vector2i teleportTo = map.portals().otherTunnel(currentTile);
                pacman.placeAtTile(teleportTo.toFloatVec());
            }
        }

        /* Handle if pacman be blocked in next turn, it'll keep moving in current direction*/
        if(pacman.isAlignedToTile()) {
            if(pacman.canAccessTile(currentTile.plus(pacman.nextDir().vector()), map)) {
                pacman.setMovingDir(pacman.nextDir());
            }
        }


        // If pacman is not standing, it can move :)))
        if(!pacman.isStanding()) {
            pacman.move();
        }
    }

    public void updateAnimator() {
        pacman.animatorUpdate();
        map.animatorUpdate();
    }

    private void handlePacmanEatFoot() {
        Vector2i currentTile = pacman.atTile();
        if(map.hasFoodAt(currentTile) && !map.hasEatenFoodAt(currentTile)) {
            map.removeFood(currentTile);
            if(map.isEnergizerTile(currentTile)) {
                score += POINTS_NORMAL_PELLET;
            } else {
                score += POINTS_ENERGIZER;
            }

            System.out.println("Current score: " + score);
        }
    }




    public void keyHandler() {
        kl.keyListening();
        for (KeyType key : kl.getPressedKey()) {
            if(key == KeyType.PAUSE) {
                timer.switchPause();
            }
            if( key == GRID_SWITCH) { gameView.switchGridDisplay();}
        }
    }

    public GameView getGameView() {
        return gameView;
    }
}
