package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.Optional;

import static utc.hiep.pacmanjavafx.lib.Global.TILE_SIZE;
import static utc.hiep.pacmanjavafx.lib.Global.checkNotNull;

public abstract class MovableEntity extends Entity {

    private Direction movingDir;
    private Direction nextDir;
    private Vector2i targetTile;

    private float defaultSpeed;

    protected boolean newTileEntered;
    protected boolean gotReverseCommand;
    protected boolean canTeleport;
    protected float corneringSpeedUp;


    private float velX;
    private float velY;
    private float accX;
    private float accY;







    public MovableEntity() {
        super();
        velX = 0;
        velY = 0;
        accX = 0;
        accY = 0;
    }

    public void reset() {
        super.reset();
        movingDir = Direction.LEFT;
        nextDir = Direction.LEFT;
        targetTile = null;
        newTileEntered = true;
        gotReverseCommand = false;
        canTeleport = true;
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
        setPosition(posX() + velX, posY() + velY);
        velX += accX;
        velY += accY;
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
    }


    public void placeAtTile(Vector2f tile) {
        placeAtTile(tile.x(), tile.y(), 0, 0);
    }


    public void setMovingDir(Direction dir) {
        checkNotNull(dir);

        if(movingDir != dir) {
            movingDir = dir;
            setVelocity(movingDir.vector().toFloatVec().scaled(velocity().length()));
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


    public Vector2i tilesAheadWithOverflowBug(int numTiles) {
        Vector2i ahead = atTile().plus(movingDir.vector().scaled(numTiles));
        return movingDir == Direction.UP ? ahead.minus(numTiles, 0) : ahead;
    }


    public void turnBackInstantly() {
       gotReverseCommand = true;

       newTileEntered = false;
       System.out.println("Turn back instantly: " + this);
    }


    public void setPercentageSpeed(byte percentage) {
        if (percentage < 0) {
            throw new IllegalArgumentException("Negative speed percentage: " + percentage);
        }
        setSpeed((float) 0.01 * percentage * defaultSpeed);
    }

    public void setSpeed(float pixelSpeed) {
        if (pixelSpeed < 0) {
            throw new IllegalArgumentException("Negative pixel speed: " + pixelSpeed);
        }
        setVelocity(pixelSpeed == 0 ? Vector2f.ZERO : movingDir.vector().toFloatVec().scaled(pixelSpeed));
    }

    public boolean isNewTileEntered() {
        return newTileEntered;
    }


}
