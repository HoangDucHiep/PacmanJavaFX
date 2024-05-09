package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.GameModel;

import java.util.Arrays;

import static utc.hiep.pacmanjavafx.model.entity.GhostState.LEAVING_HOUSE;
import static utc.hiep.pacmanjavafx.model.entity.GhostState.LOCKED;

public class GhostHouseControl {
    private static final byte UNLIMITED = -1;

    private final byte[] globalLimits = {UNLIMITED, 7, 17, UNLIMITED};
    private final int[]  counters = {0, 0, 0, 0};
    private final int    pacStarvingLimitTicks;
    private int          globalCounter = 0;
    private boolean      globalCounterEnabled = false;

    public GhostHouseControl(int levelNumber) {
        pacStarvingLimitTicks = levelNumber < 5 ? 240 : 180; // 4 sec : 3 sec
    }

    /**
     * Get private dot limit for the given ghost
     * @param levelNumber current level
     * @param ghost ghost
     * @return private dot limit
     */
    private byte privateDotLimit(int levelNumber, Ghost ghost) {
        if (levelNumber == 1 && ghost.id() == GameModel.CYAN_GHOST)   return 30;
        if (levelNumber == 1 && ghost.id() == GameModel.ORANGE_GHOST) return 60;
        if (levelNumber == 2 && ghost.id() == GameModel.ORANGE_GHOST) return 50;
        return 0;
    }

    /**
     * Reset global dot counter and set enabled
     * @param enabled true if enabled, false otherwise
     */
    public void resetGlobalCounterAndSetEnabled(boolean enabled) {
        globalCounter = 0;
        globalCounterEnabled = enabled;
    }


    /**
     * Update dot counters - both global and private
     * @param level current level
     */
    public void updateDotCount(GameLevel level) {
        if (globalCounterEnabled) {
            if (level.ghost(GameModel.ORANGE_GHOST).is(LOCKED) && globalCounter == 32) {
                resetGlobalCounterAndSetEnabled(false);
            } else {
                globalCounter++;
            }
        } else {
            Arrays.stream(level.ghosts()).filter(ghost -> ghost.is(LOCKED))
                    .filter(Ghost::insideHouse)
                    .findFirst().ifPresent(ghost -> counters[ghost.id()]++);
        }
    }


    /**
     * Unlock ghosts if conditions are met
     * @param level current level
     */
    public void unlockGhost(GameLevel level) {
        if (level.ghost(GameModel.RED_GHOST).is(LOCKED)) {
            level.ghost(GameModel.RED_GHOST).setState(LEAVING_HOUSE);
        }

        Ghost[] lockedGhosts = Arrays.stream(level.ghosts()).filter(ghost -> ghost.is(LOCKED)).toArray(Ghost[]::new);

        if (lockedGhosts.length == 0) {
            return;
        }

        for (var ghost : lockedGhosts) {
            // check private dot counter first (if enabled)
            if (!globalCounterEnabled && counters[ghost.id()] >= privateDotLimit(level.levelNum(), ghost)) {
                ghost.setState(LEAVING_HOUSE);
            }
            // check global dot counter
            if (globalLimits[ghost.id()] != UNLIMITED && globalCounter >= globalLimits[ghost.id()]) {
                ghost.setState(LEAVING_HOUSE);
            }
        }


        // check Pac-Man starving time
        if (level.pacman().starvingTicks() >= pacStarvingLimitTicks) {
            lockedGhosts[0].setState(LEAVING_HOUSE);
            level.pacman().endStarving();
        }
    }
}
