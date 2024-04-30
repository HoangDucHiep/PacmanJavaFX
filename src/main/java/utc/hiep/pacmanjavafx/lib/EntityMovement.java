package utc.hiep.pacmanjavafx.lib;

import utc.hiep.pacmanjavafx.model.entity.MovableEntity;
import utc.hiep.pacmanjavafx.model.world.Portal;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.lib.Direction.*;
import static utc.hiep.pacmanjavafx.lib.Direction.LEFT;
import static utc.hiep.pacmanjavafx.lib.Global.*;

public interface EntityMovement {

    Direction[] DIRECTION_PRIORITY = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};


    static void chaseTarget(MovableEntity entity, World world) {
        var currentTile = entity.atTile();
        if((world.isIntersection(currentTile) || !entity.canAccessTile(currentTile.plus(entity.movingDir().vector()), world)) && entity.newTileEntered) {
            entity.setNextDir(computeTargetDir(entity, world));
        }
        move(entity, world);
    }

    static Direction computeTargetDir(MovableEntity entity, World world) {
        final var currentTile = entity.atTile();
        iVector2D targetTile;

        if(entity.targetTile().isPresent())
            targetTile = entity.targetTile().get();
        else
            throw new IllegalStateException("No target tile set for entity");

        Direction targetDir = null;
        float minDistance = Float.MAX_VALUE;

        for (Direction dir : DIRECTION_PRIORITY) {
            final var neighborTile = currentTile.plus(dir.vector());
            if (entity.canAccessTile(neighborTile, world) && !dir.equals(entity.movingDir().opposite())) {
                final float distance = neighborTile.sqrEuclideanDistance(targetTile);
                if (distance < minDistance) {
                    minDistance = distance;
                    targetDir = dir;
                }
            }
        }
        return targetDir;

    }

    static Direction computeRandomDir(MovableEntity entity, World world) {
        final var currentTile = entity.atTile();
        Direction randomDir = null;

        randomDir = pseudoRandomDirection();

        while(!entity.canAccessTile(currentTile.plus(randomDir.vector()), world)
                || entity.movingDir().opposite().equals(randomDir)) {
            randomDir = randomDir.nextClockwise();
        }
        return randomDir;
    }



    /**
     * Tries moving through the game world.
     * <p>
     * First checks if the MovableEntity can teleport, then if the MovableEntity can move to its wish direction. If this is not
     * possible, it keeps moving to its current move direction.
     *
     * @param entity a MovableEntity (ghost, moving bonus)
     * @param world the world/maze
     */
    static void move(MovableEntity entity, World world) {
        if(!tryTeleport(entity, world))
            return;

        if (entity.gotTurnBackCommand() && entity.canTurnBack()) {
            entity.setMovingDir(entity.movingDir().opposite());
            entity.setGotTurnBackCommand(false);
        }
        if(move(entity, world, entity.nextDir()))
            entity.setMovingDir(entity.nextDir());
        else
            move(entity, world, entity.movingDir());
    }



    /**
     * Tries to teleport a MovableEntity through a portal.
     *
     * @param entity a MovableEntity (ghost, moving bonus)
     * @param world a world
     */
    static boolean tryTeleport(MovableEntity entity, World world) {

        final var currentTile = entity.atTile();

        Portal portals = world.portals();

        if(!entity.canTeleport()) {
            if(((currentTile.equals(portals.leftTunnelEnd()) && entity.movingDir().equals(LEFT)) || (currentTile.equals(portals.rightTunnelEnd()) && entity.movingDir().equals(RIGHT)))
                    && entity.offset().almostEquals(fVector2D.ZERO, entity.currentSpeed(), entity.currentSpeed())
                    && !entity.movingDir().equals(entity.nextDir().opposite())) {
                return false;
            }
        }

        if(world.belongsToPortal(currentTile)) {
            if(!world.belongsToPortal(entity.tilesAhead(1))) {
                iVector2D teleportTo = world.portals().otherTunnel(currentTile);
                entity.centerOverTile(teleportTo);
            }
        }
        return true;
    }

    /**
     * Tries to move a MovableEntity towards the given directory. Handles collisions with walls and moving around corners.
     *
     * @param entity (ghost, moving bonus)
     * @param world the world/maze
     * @param dir the direction to move
     */

    static boolean move(MovableEntity entity, World world, Direction dir) {
        final iVector2D tileBeforeMove = entity.atTile();
        final iVector2D nextTile = entity.tilesAhead(1, dir);
        final fVector2D newVelocity = dir.vector().toFloatVec().scaled(entity.velocity().length());

        final boolean willTurn = !dir.equals(entity.movingDir());

        if(!entity.canAccessTile(nextTile, world) && entity.offset().almostEquals(fVector2D.ZERO, entity.currentSpeed(), entity.currentSpeed())) {
            if(!willTurn) {
                entity.centerOverTile(tileBeforeMove);
            }
            return false;
        }

        if(willTurn) {
            if(entity.offset().almostEquals(fVector2D.ZERO, entity.currentSpeed(), entity.currentSpeed()))
                entity.placeAtTile(tileBeforeMove.toFloatVec());
            else
                return false;
        }

        entity.setVelocity(newVelocity);
        entity.move();

        iVector2D newTile = entity.atTile();
        entity.newTileEntered = !tileBeforeMove.equals(newTile);
        return true;
    }



    private static Direction pseudoRandomDirection() {
        int rnd = randomInt(0, 4);
        if (rnd == 0) return UP;
        if (rnd == 1) return RIGHT;
        if (rnd == 2) return DOWN;
        return LEFT;
    }


}
