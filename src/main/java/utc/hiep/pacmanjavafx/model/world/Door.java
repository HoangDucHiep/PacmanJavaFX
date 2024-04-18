package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public record Door(Vector2i leftWing, Vector2i rightWing) {

    public Door {
        checkNotNull(leftWing);
        checkNotNull(rightWing);
    }


    /**
     * @param tile tile
     * @return true if the given tile is occupied by this door, false otherwise.
     */
    public boolean occupies(Vector2i tile) {
        return leftWing.equals(tile) || rightWing.equals(tile);
    }


    /**
     * @return position where ghost can enter the door
     */
    public Vector2f entryPosition() {
        return new Vector2f(TILE_SIZE * rightWing.x() - HALF_TILE_SIZE, TILE_SIZE * (rightWing.y() - 1));
    }
}
