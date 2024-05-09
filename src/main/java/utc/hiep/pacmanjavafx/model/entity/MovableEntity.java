package utc.hiep.pacmanjavafx.model.entity;

import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.Optional;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public abstract class MovableEntity extends Entity {

    private Direction movingDir;                //current moving direction
    private Direction nextDir;                  //next (wish) moving a direction
    private iVector2D targetTile;               //target tile to move to

    private float defaultSpeed;                 //Base speed (per tick)
    private float currentSpeed;                 //Speed after percentage adjustment (per tick)
    private byte currentPercentageSpeed;

    public boolean newTileEntered;
    protected boolean gotTurnBackCommand;
    protected boolean canTeleport;


    private float velX;                 //velocity
    private float velY;
    private float accX;                 //acceleration
    private float accY;

    //UI part
    protected Animator animator;

    public MovableEntity() {
        super();
        velX = 0;
        velY = 0;
        accX = 0;
        accY = 0;
    }

    /**
     * Init default state for entity
     * Things that setted:
     * @see Entity#reset()
     * movingDir = Direction. LEFT
     * nextDir = Direction. LEFT
     * targetTile = null
     * newTileEntered = true
     * gotTurnBackCommand = false
     * canTeleport = true
     * */
    public void reset() {
        super.reset();
        movingDir = Direction.LEFT;
        nextDir = Direction.LEFT;
        targetTile = null;
        newTileEntered = true;
        gotTurnBackCommand = false;
        canTeleport = true;
    }

    /**
     * Set base speed for entity
     * @param pixelsPerTick speed in pixel per tick
     */
    public void setDefaultSpeed(float pixelsPerTick) {
        defaultSpeed = pixelsPerTick;
    }


    /**
     * Get current velocity
     * @return a Velocity in Vector
     */
    public fVector2D velocity() {
        return new fVector2D(velX, velY);
    }


    /**
     * Set velocity
     * @param velocity a Velocity in Vector
     */
    public void setVelocity(fVector2D velocity) {
        checkNotNull(velocity);
        velX = velocity.x();
        velY = velocity.y();
    }


    /**
     * Set velocity
     * @param x velocity in X
     * @param y velocity in Y
     */
    public void setVelocity(float x, float y) {
        velX = x;
        velY = y;
    }


    /**
     * Get acceleration
     * @return an Acceleration in Vector
     */
    public fVector2D acceleration() {
        return new fVector2D(accX, accY);
    }


    /**
     * Set acceleration
     * @param acceleration an Acceleration in Vector
     */
    public void setAcceleration(fVector2D acceleration) {
        checkNotNull(acceleration, "Acceleration of entity must not be null");
        accX = acceleration.x();
        accY = acceleration.y();
        velX += accX;
        velY += accY;
    }


    /**
     * Set acceleration
     * @param ax acceleration in X
     * @param ay acceleration in Y
     */
    public void setAcceleration(float ax, float ay) {
        accX = ax;
        accY = ay;
        velX += accX;
        velY += accY;
    }


    /**
     * Move entity to next pos in current direction with velocity in one frame
     */
    public void move() {
        iVector2D prevTile = atTile();
        setPosition(posX() + velX, posY() + velY);
        velX += accX;
        velY += accY;
        if(!atTile().equals(prevTile)) {
            newTileEntered = true;
        }
    }


    /**
     * Get entity name
     * @return name of entity
     */
    public abstract String name();

    /**
     * Check if an entity can reverse a direction
     * @return true if it can turn back, false otherwise
     */
    public abstract boolean canTurnBack();


    /**
     * @param tile some tile inside or outside the world
     * @return if this creature can access the given tile
     */
    public abstract boolean canAccessTile(iVector2D tile, World world);


    /**
     * Set tile to move to
     * @param tile tile to move to
     */
    public void setTargetTile(iVector2D tile) {
        targetTile = tile;
    }


    public Optional<iVector2D> targetTile() {
        return Optional.ofNullable(targetTile);
    }


    /**
     * Place entity at given position
     * @param tx position X in tile map system
     * @param ty position Y in tile map system
     * @param ox offset of entity and tile in X
     * @param oy offset of entity and tile in Y
     */
    public void placeAtTile(float tx, float ty, float ox, float oy) {
        var prevTile = atTile();
        setPosition(tx * TILE_SIZE + ox, ty * TILE_SIZE + oy);
        newTileEntered = !atTile().equals(prevTile);
    }


    /**
     * Place entity at given tile
     * @param tile tile to place entity at
     */
    public void placeAtTile(fVector2D tile) {
        placeAtTile(tile.x(), tile.y(), 0, 0);
    }


    /**
     * Places this creature centered over the given tile.
     *
     * @param tile tile where creature is placed
     */
    public void centerOverTile(iVector2D tile) {
        placeAtTile(tile.x(), tile.y(), 0, 0);
    }

    /**
     * Set moving direction - turn entity to move in this direction
     * @param dir direction to move
     */
    public void setMovingDir(Direction dir) {
        checkNotNull(dir);

        if(movingDir != dir) {
            movingDir = dir;
            setVelocity(movingDir.vector().toFloatVec().scaled(velocity().length()));
            //System.out.println("new move dir: " + movingDir + " " + this);
        }
    }


    /**
     * Get current moving direction
     * @return current moving direction
     */
    public Direction movingDir() {
        return movingDir;
    }


    /**
     * Set next direction to move to
     * @param dir direction to move to
     */
    public void setNextDir(Direction dir) {
        checkNotNull(dir);
        nextDir = nextDir != dir ? dir : nextDir;
        //System.out.println("next move dir: " + nextDir + " " + this);
    }

    /**
     * Get next direction to move to
     * @return next direction to move to
     */
    public Direction nextDir() {
        return nextDir;
    }


    /**
     * Get tile ahead of entity - use for moving
     * @param numTiles number of tiles ahead
     * @return tile ahead of entity
     */
    public iVector2D tilesAhead(int numTiles) {
        return atTile().plus(movingDir.vector().scaled(numTiles));
    }

    public iVector2D tilesAhead(int numTiles, Direction dir) {
        return atTile().plus(dir.vector().scaled(numTiles));
    }


    /**
     * Get tile ahead of entity - use for getting tile ahead of entity with overflow bug
     * Help to get target for ghost
     * @param numTiles number of tiles ahead
     * @return tile ahead of entity
     */
    public iVector2D tilesAheadWithOverflowBug(int numTiles) {
        iVector2D ahead = atTile().plus(movingDir.vector().scaled(numTiles));
        return movingDir == Direction.UP ? ahead.minus(numTiles, 0) : ahead;
    }


    /**
     * Turn back instantly - no need to be at intersection or to be blocked to turn back
     */
    public void turnBackInstantly() {
       gotTurnBackCommand = true;
       newTileEntered = false;
       setNextDir(movingDir.opposite());
    }

    /**
     * Set speed percentage
     * @param percentage speed percentage
     */
    public void setPercentageSpeed(byte percentage) {
        if (percentage < 0) {
            throw new IllegalArgumentException("Negative speed percentage: " + percentage);
        }
        currentPercentageSpeed = percentage;
        currentSpeed = (float) 0.01 * percentage * defaultSpeed;
        setSpeed(currentSpeed);
    }

    public void updateDefaultSpeed(float pixelsPerTick) {
        setDefaultSpeed(pixelsPerTick);
        setPercentageSpeed(currentPercentageSpeed);
    }

    /**
     * Calculate speed with base speed and percentage speed
     * @param pixelSpeed speed in pixel per tick
     */
    protected void setSpeed(float pixelSpeed) {
        if (pixelSpeed < 0) {
            throw new IllegalArgumentException("Negative pixel speed: " + pixelSpeed);
        }
        setVelocity(pixelSpeed == 0 ? fVector2D.ZERO : movingDir.vector().toFloatVec().scaled(pixelSpeed));
    }

    public boolean isNewTileEntered() {
        return newTileEntered;
    }

    public boolean isAlignedToTile() {
        return center().almostEquals(centerOfTile(atTile()), 0, 0);
    }

    public float currentSpeed() {
        return currentSpeed;
    }

    public boolean canTeleport() {
        return canTeleport;
    }

    public boolean gotTurnBackCommand() {
        return gotTurnBackCommand;
    }

    public void setGotTurnBackCommand(boolean gotTurnBackCommand) {
        this.gotTurnBackCommand = gotTurnBackCommand;
    }

    public void animatorUpdate() {
        animator.update(movingDir());
    }

    public void setAnimator(Animator animator) {
        System.out.println(animator);
        this.animator = animator;
    }


    public void resetAnimator() {
        animator.reset();
    }
}
