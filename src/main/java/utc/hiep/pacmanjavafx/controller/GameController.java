package utc.hiep.pacmanjavafx.controller;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.World;
import utc.hiep.pacmanjavafx.scene.GameView;
import utc.hiep.pacmanjavafx.model.Timer;

import java.util.ArrayList;
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
                kl.keyListening();
                timer.updateTimer(now);
                kl.clearKey();
                gameView.render();
                //System.out.println("Current tick: " + timer.getTick());
                //System.out.println("Current second: " + timer.getSecondTimer());
            }
        }.start();
    }


    public GameView getGameView() {
        return gameView;
    }
}
