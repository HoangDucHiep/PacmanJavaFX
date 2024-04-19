package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
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
                timer.updateTimer(now);
                keyHander();




                gameView.render();
                //System.out.println("Current tick: " + timer.getTick());
                //System.out.println("Current second: " + timer.getSecondTimer());
            }
        }.start();
    }

//    public void update(long now) {
//        timer.updateTimer(now);
//        kl.keyListening();
//    }


    public void keyHander() {
        kl.keyListening();
        pressedKey = kl.getPressedKey();

        for (KeyType key : pressedKey) {
            switch (key) {
                case TURN_UP -> pacman.setMovingDir(Direction.UP);
                case TURN_DOWN -> pacman.setMovingDir(Direction.DOWN);
                case TURN_LEFT -> pacman.setMovingDir(Direction.LEFT);
                case TURN_RIGHT -> pacman.setMovingDir(Direction.RIGHT);
                case GRID_SWITCH -> gameView.switchGridDisplay();
            }
        }

        kl.clearKey();
    }

    public GameView getGameView() {
        return gameView;
    }
}
