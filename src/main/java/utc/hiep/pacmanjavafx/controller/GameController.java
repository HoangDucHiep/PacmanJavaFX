package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.model.Timer;

import java.util.List;
import java.util.Stack;

import static utc.hiep.pacmanjavafx.lib.Global.centerOfTile;
import static utc.hiep.pacmanjavafx.lib.Global.tileAt;

public class GameController {
    private final GameView gameView;
    private List<KeyType> pressedKey = new Stack<>();

    private final World map;
    private Timer timer;
    private Pacman pacman;


    private KeyListener kl;

    public GameController() {
        this.gameView = new GameView();
        timer = new Timer();
        map = gameView.getWorld();
        pacman = gameView.getPacman();
        pacman.setTicker(timer);
        kl = new KeyListener(gameView);
        running();
    }



    public void running() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                keyHander();
                if(!timer.isPaused()) {
                    timer.updateTimer();
                    movePacman();
                    gameView.render();
//                System.out.println("Current tick: " + timer.getTick());
//                System.out.println("Current second: " + timer.getSecondTimer());
                }

            }
        }.start();
    }



    public void movePacman() {
        Vector2i currentTile = pacman.atTile();

        /* Handle turn back instantly */
        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
            pacman.turnBackInstantly();
        }

        /*  handle pacman be blocked by wall or smth */
        Direction lastDir;
        if(!pacman.canAccessTile(pacman.tilesAhead(1), map) && pacman.center().almostEquals(centerOfTile(currentTile), pacman.currentSpeed(),  pacman.currentSpeed())) {
            if(!pacman.isStanding()) {
                pacman.placeAtTile(currentTile.toFloatVec());
                pacman.standing();
            }
        }


        /*  handle pacman at intersection */
        if(map.isIntersection(currentTile)) {
            //if pacman haven't aligned to tile, but almost aligned, then aligned it
            if(pacman.isNewTileEntered() && pacman.offset().almostEquals(Vector2f.ZERO, pacman.currentSpeed(), pacman.currentSpeed())) {
                pacman.placeAtTile(currentTile.toFloatVec());
            }
        }

        /* Handle if pacman would be blocked in next turn*/
        if(pacman.isAlignedToTile()) {
            lastDir = pacman.movingDir();
            pacman.setMovingDir(pacman.nextDir());
            if(!pacman.canAccessTile(pacman.tilesAhead(1), map)) {
                pacman.setMovingDir(lastDir);
            }
        }

        // If pacman is not standing, it can move :)))
        if(!pacman.isStanding()) {
            pacman.move();
        }
    }




    public void keyHander() {
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
