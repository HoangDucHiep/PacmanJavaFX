package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;

import java.util.Objects;

import static utc.hiep.pacmanjavafx.lib.Global.HALF_TILE_SIZE;

public class House {

    private Vector2i minTile;       // top left tile
    private Vector2i size;          // size in tiles of the house
    private Door door;              //house's door

    public void setTopLeftTile(Vector2i minTile) {
        Objects.requireNonNull(minTile);
        this.minTile = minTile;
    }

    public void setSize(Vector2i size) {
        Objects.requireNonNull(size);
        if (size.x() < 1 || size.y() < 1) {
            throw new IllegalArgumentException("House size must be larger than one square tile but is: " + size);
        }
        this.size = size;
    }

    public void setDoor(Door door) {
        Objects.requireNonNull(door);
        this.door = door;
    }

    public Vector2i topLeftTile() {
        return minTile;
    }

    public Vector2i size() {
        return size;
    }

    public Door door() {
        return door;
    }


    public Vector2f center() {
        return minTile.toFloatVec().scaled(HALF_TILE_SIZE).plus(size.toFloatVec().scaled(HALF_TILE_SIZE));
    }


    /**
     * @param tile some tile
     * @return true if the given tile is part of this house
     */
    public boolean contains(Vector2i tile) {
        Vector2i max = minTile.plus(size().minus(1, 1));
        return tile.x() >= minTile.x() && tile.x() <= max.x() //
                && tile.y() >= minTile.y() && tile.y() <= max.y();
    }
}
