package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.GameModel;

import java.util.Optional;
import java.util.stream.Stream;

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

    private byte privateDotLimit(int levelNumber, Ghost ghost) {
        if (levelNumber == 1 && ghost.id() == GameModel.CYAN_GHOST)   return 30;
        if (levelNumber == 1 && ghost.id() == GameModel.ORANGE_GHOST) return 60;
        if (levelNumber == 2 && ghost.id() == GameModel.ORANGE_GHOST) return 50;
        return 0;
    }

    public void resetGlobalCounterAndSetEnabled(boolean enabled) {
        globalCounter = 0;
        globalCounterEnabled = enabled;
        Logger.trace("Global dot counter set to 0 and {}", enabled ? "enabled" : "disabled");
    }

    public void updateDotCount(GameLevel level) {
        if (globalCounterEnabled) {
            if (level.ghost(GameModel.ORANGE_GHOST).is(LOCKED) && globalCounter == 32) {
                Logger.trace("{} inside house when global counter reached 32", level.ghost(GameModel.ORANGE_GHOST).name());
                resetGlobalCounterAndSetEnabled(false);
            } else {
                globalCounter++;
                Logger.trace("Global dot counter = {}", globalCounter);
            }
        } else {
            level.ghosts(LOCKED)
                    .filter(ghost -> ghost.insideHouse(level.world().house()))
                    .findFirst().ifPresent(ghost -> {
                        counters[ghost.id()]++;
                        Logger.trace("{} dot counter = {}", ghost.name(), counters[ghost.id()]);
                    });
        }
    }

    public Optional<GhostUnlockInfo> unlockGhost(GameLevel level) {
        if (level.ghost(GameModel.RED_GHOST).is(LOCKED)) {
            return Optional.of(new GhostUnlockInfo(level.ghost(GameModel.RED_GHOST), "Gets unlocked immediately"));
        }
        Ghost candidate = Stream.of(GameModel.PINK_GHOST, GameModel.CYAN_GHOST, GameModel.ORANGE_GHOST)
                .map(level::ghost).filter(ghost -> ghost.is(LOCKED)).findFirst().orElse(null);
        if (candidate == null) {
            return Optional.empty();
        }
        // check private dot counter first (if enabled)
        if (!globalCounterEnabled && counters[candidate.id()] >= privateDotLimit(level.number(), candidate)) {
            return Optional.of(new GhostUnlockInfo(candidate,
                    "Private dot counter at limit (%d)", privateDotLimit(level.number(), candidate)));
        }
        // check global dot counter
        if (globalLimits[candidate.id()] != UNLIMITED && globalCounter >= globalLimits[candidate.id()]) {
            return Optional.of(new GhostUnlockInfo(candidate,
                    "Global dot counter at limit (%d)", globalLimits[candidate.id()]));
        }
        // check Pac-Man starving time
        if (level.pac().starvingTicks() >= pacStarvingLimitTicks) {
            level.pac().endStarving();
            return Optional.of(new GhostUnlockInfo(candidate,
                    "%s reached starving limit (%d ticks)", level.pac().name(), pacStarvingLimitTicks));
        }
        return Optional.empty();
    }
}
