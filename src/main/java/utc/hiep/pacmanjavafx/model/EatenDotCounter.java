package utc.hiep.pacmanjavafx.model;

import utc.hiep.pacmanjavafx.model.world.World;

public class EatenDotCounter {
    private int acumulatedDot;

    private int deActivatePoint;
    private int activatePoint;

    private boolean isActivated;

    private final World world;


    public EatenDotCounter(World world) {
        acumulatedDot = 0;
        isActivated = false;
        this.world = world;
    }

    public int getCounter() {
        if(isActivated) {
            throw new IllegalStateException("Counter haven't been activated");
        }
        return world.eatenFoodCount() - activatePoint + acumulatedDot;
    }

    public void activate() {
        if(isActivated) {
            throw new IllegalStateException("Counter have been activated");
        }
        activatePoint = world.eatenFoodCount();
        isActivated = true;
    }

    public void deActivate() {
        if(!isActivated) {
            throw new IllegalStateException("Counter haven't been activated");
        }
        deActivatePoint = world.eatenFoodCount();
        isActivated = false;
        acumulatedDot += (deActivatePoint -  activatePoint);
    }



}
