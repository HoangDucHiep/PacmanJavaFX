package utc.hiep.pacmanjavafx.model.entity;

import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;
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


    public MovableEntity() {
        super();
    }

    public void reset() {
        super.reset();
        movingDir = Direction.RIGHT;
        nextDir = Direction.RIGHT;
        targetTile = null;
        newTileEntered = true;
        gotReverseCommand = false;
        canTeleport = true;
    }


    public void setDefaultSpeed(float pixelsPerTick) {
        defaultSpeed = pixelsPerTick;
    }


    public abstract String name();


    public abstract boolean canTurnBack();


    public abstract boolean canAccessTile(Vector2i tile, World world);


    public void setTargetTile(Vector2i tile) {
        targetTile = tile;
    }


    public Optional<Vector2i> targetTile() {
        return Optional.ofNullable(targetTile);
    }


    public void placeAtTile(int tx, int ty, float ox, float oy) {
        var prevTile = atTile();
        setPosition(tx * TILE_SIZE + ox, ty * TILE_SIZE + oy);
        newTileEntered = !atTile().equals(prevTile);
    }


    public void placeAtTile(Vector2i tile) {
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
