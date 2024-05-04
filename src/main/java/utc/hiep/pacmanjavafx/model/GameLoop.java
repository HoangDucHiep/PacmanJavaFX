package utc.hiep.pacmanjavafx.model;

import javafx.animation.AnimationTimer;

import java.util.function.Consumer;

import static utc.hiep.pacmanjavafx.model.level.GameModel.FPS;

//public class GameLoop extends AnimationTimer {
//
//    private static final float timeStep = 1.0f / 60;   //FPS = 60, 1.0f is one second, 0.01667f
//    private float accumulatedTime = 0;
//
//    private long previousTime = 0;
//    final float maxDelta = 0.05f;
//    @Override
//    public void handle(long now) {
//
//    }            @Override
//    public void handle(long currentTime)
//    {
//        if (previousTime == 0) {
//            previousTime = currentTime;
//            return;
//        }
//
//        float secondsElapsed = (currentTime - previousTime) / 1e9f;
//        float secondsElapsedCapped = Math.min(secondsElapsed, maxDelta);
//        accumulatedTime += secondsElapsedCapped;
//        previousTime = currentTime;
//
//        while (accumulatedTime >= timeStep) {
//            update();
//            accumulatedTime -= timeStep;
//        }
//
//        gameView.render();
//    }
//
//    @Override
//    public void stop()
//    {
//        previousTime = 0;
//        accumulatedTime = 0;
//        super.stop();
//    }
//}.start();
//}