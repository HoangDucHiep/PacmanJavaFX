package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;

import java.util.Objects;

import static utc.hiep.pacmanjavafx.lib.Global.HALF_TILE_SIZE;

public class House {

    private iVector2D minTile;       // top left tile
    private iVector2D size;          // size in tiles of the house
    private Door door;              //house's door

    public void setTopLeftTile(iVector2D minTile) {
        Objects.requireNonNull(minTile);
        this.minTile = minTile;
    }

    public void setSize(iVector2D size) {
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

    public iVector2D topLeftTile() {
        return minTile;
    }

    public iVector2D size() {
        return size;
    }

    public Door door() {
        return door;
    }


    public fVector2D center() {
        return minTile.toFloatVec().scaled(HALF_TILE_SIZE).plus(size.toFloatVec().scaled(HALF_TILE_SIZE));
    }


    /**
     * @param tile some tile
     * @return true if the given tile is part of this house
     */
    public boolean contains(iVector2D tile) {
        iVector2D max = minTile.plus(size().minus(1, 1));
        return tile.x() >= minTile.x() && tile.x() <= max.x() //
                && tile.y() >= minTile.y() && tile.y() <= max.y();
    }
}
