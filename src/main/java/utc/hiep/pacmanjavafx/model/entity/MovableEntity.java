package utc.hiep.pacmanjavafx.model.entity;

import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Global;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.Optional;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public abstract class MovableEntity extends Entity {

    private Direction movingDir;
    private Direction nextDir;
    private Vector2i targetTile;

    private float defaultSpeed;
    private float currentSpeed;


    protected boolean newTileEntered;
    protected boolean gotReverseCommand;
    protected boolean canTeleport;
    protected float corneringSpeedUp;


    private float velX;                 //velocity
    private float velY;
    private float accX;                 //acceleration
    private float accY;

    private boolean isStanding;
    private boolean isAligned;

    public MovableEntity() {
        super();
        velX = 0;
        velY = 0;
        accX = 0;
        accY = 0;
    }

    public void

    reset() {
        super.reset();
        movingDir = Direction.LEFT;
        nextDir = Direction.LEFT;
        targetTile = null;
        newTileEntered = true;
        gotReverseCommand = false;
        canTeleport = true;
        isStanding = false;
    }


    public void setDefaultSpeed(float pixelsPerTick) {
        defaultSpeed = pixelsPerTick;
    }

    public Vector2f velocity() {
        return new Vector2f(velX, velY);
    }

    public void setVelocity(Vector2f velocity) {
        checkNotNull(velocity);
        velX = velocity.x();
        velY = velocity.y();
    }


    public void setVelocity(float x, float y) {
        velX = x;
        velY = y;
    }


    public Vector2f acceleration() {
        return new Vector2f(accX, accY);
    }

    public void setAcceleration(Vector2f acceleration) {
        checkNotNull(acceleration, "Acceleration of entity must not be null");
        accX = acceleration.x();
        accY = acceleration.y();
    }

    public void setAcceleration(float ax, float ay) {
        accX = ax;
        accY = ay;
    }


    public void move() {
        Vector2i prevTile = atTile();
        setPosition(posX() + velX, posY() + velY);
        velX += accX;
        velY += accY;
        setIsStanding(false);
        if(!atTile().equals(prevTile)) {
            newTileEntered = true;
        }
    }



    public abstract String name();


    public abstract boolean canTurnBack();


    /**
     * @param tile some tile inside or outside the world
     * @return if this creature can access the given tile
     */
    public abstract boolean canAccessTile(Vector2i tile, World world);


    public void setTargetTile(Vector2i tile) {
        targetTile = tile;
    }


    public Optional<Vector2i> targetTile() {
        return Optional.ofNullable(targetTile);
    }


    public void placeAtTile(float tx, float ty, float ox, float oy) {
        var prevTile = atTile();
        setPosition(tx * TILE_SIZE + ox, ty * TILE_SIZE + oy);
        newTileEntered = !atTile().equals(prevTile);
        isAligned = true;
    }


    public void placeAtTile(Vector2f tile) {
        placeAtTile(tile.x(), tile.y(), 0, 0);
    }


    public void setMovingDir(Direction dir) {
        checkNotNull(dir);

        if(movingDir != dir) {
            movingDir = dir;
            setIsStanding(false);
            setVelocity(movingDir.vector().toFloatVec().scaled(currentSpeed));
            System.out.println("new move dir: " + movingDir + " " + this);
        }
    }


    public Direction movingDir() {
        return movingDir;
    }


    public void setNextDir(Direction dir) {
        checkNotNull(dir);
        nextDir = nextDir != dir ? dir : nextDir;
        System.out.println("next move dir: " + nextDir + " " + this);
    }

    public Direction nextDir() {
        return nextDir;
    }


    public Vector2i tilesAhead(int numTiles) {
        Vector2i ahead = atTile().plus(movingDir.vector().scaled(numTiles));
        return ahead;
    }


    public Vector2i tilesAheadWithOverflowBug(int numTiles) {
        Vector2i ahead = atTile().plus(movingDir.vector().scaled(numTiles));
        return movingDir == Direction.UP ? ahead.minus(numTiles, 0) : ahead;
    }



    public void turnBackInstantly() {
       gotReverseCommand = true;
       setIsStanding(false);
       newTileEntered = false;
       setMovingDir(nextDir);
       System.out.println("Turn back instantly: " + this);
    }


    public void setPercentageSpeed(byte percentage) {
        if (percentage < 0) {
            throw new IllegalArgumentException("Negative speed percentage: " + percentage);
        }
        currentSpeed = (float) 0.01 * percentage * defaultSpeed;
        setSpeed(currentSpeed);
    }

    private void setSpeed(float pixelSpeed) {
        if (pixelSpeed < 0) {
            throw new IllegalArgumentException("Negative pixel speed: " + pixelSpeed);
        }
        setVelocity(pixelSpeed == 0 ? Vector2f.ZERO : movingDir.vector().toFloatVec().scaled(pixelSpeed));
    }

    public boolean isNewTileEntered() {
        return newTileEntered;
    }

    public void setNewTileEntered(boolean entered) {
        newTileEntered = entered;
    }

    public boolean isStanding() {
        return isStanding;
    }

    public void setIsStanding(boolean isStanding) {
        this.isStanding = isStanding;
    }

    public boolean isAlignedToTile() {
        return center().almostEquals(centerOfTile(atTile()), 0, 0);
    }

    public float currentSpeed() {
        return currentSpeed;
    }


}
