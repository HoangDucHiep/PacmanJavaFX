package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public record Door(iVector2D leftWing, iVector2D rightWing) {

    public Door {
        checkNotNull(leftWing);
        checkNotNull(rightWing);
    }

    /**
     * @param tile tile
     * @return true if the given tile is occupied by this door, false otherwise.
     */
    public boolean occupies(iVector2D tile) {
        return leftWing.equals(tile) || rightWing.equals(tile);
    }

    /**
     * @return position where ghost can enter the door
     */
    public fVector2D entryPosition() {
        return new fVector2D(TILE_SIZE * rightWing.x() - HALF_TILE_SIZE, TILE_SIZE * (rightWing.y() - 1));
    }
}
