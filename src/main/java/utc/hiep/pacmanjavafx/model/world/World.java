package utc.hiep.pacmanjavafx.model.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.IMAGE_LIB;
import utc.hiep.pacmanjavafx.lib.Vector2i;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public class World {

    private static Image mapImage = IMAGE_LIB.MAP_EMPTY;
    private static Image mapFlashingImage = IMAGE_LIB.FLASHING_MAZE;

    byte TILES_X = 28;
    byte TILES_Y = 36;

    public static final byte T_SPACE = 0;
    public static final byte T_WALL = 1;
    public static final byte T_TUNNEL = 2;
    public static final byte T_PELLET = 3;
    public static final byte T_ENERGIZER = 4;


    private static byte[][] validateTileMapData(byte[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("Map data missing");
        }
        if (data.length == 0) {
            throw new IllegalArgumentException("Map data empty");
        }
        var firstRow = data[0];
        if (firstRow.length == 0) {
            throw new IllegalArgumentException("Map data empty");
        }
        for (var row : data) {
            if (row.length != firstRow.length) {
                throw new IllegalArgumentException("Map has differently sized rows");
            }
        }
        for (int row = 0; row < data.length; ++row) {
            for (int col = 0; col < data[row].length; ++col) {
                byte d = data[row][col];
                if (d < T_SPACE || d > T_ENERGIZER) {
                    throw new IllegalArgumentException(String.format("Map data at row=%d, col=%d are illegal: %d", row, col, d));
                }
            }
        }
        return data;
    }


    byte[][] PACMAN_MAP_SOURCE = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
            {1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1},
            {1, 4, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 4, 1},
            {1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1},
            {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
            {1, 3, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 3, 1},
            {1, 3, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 3, 1},
            {1, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 3, 3, 1},
            {1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 1, 3, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 3, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 3, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 3, 1, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 3, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 3, 1, 1, 1, 1, 1, 1},
            {2, 2, 2, 2, 2, 2, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 3, 2, 2, 2, 2, 2, 2},
            {1, 1, 1, 1, 1, 1, 3, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 3, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 1, 3, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 3, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 3, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 3, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 3, 1, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 3, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 3, 1, 1, 1, 1, 1, 1},
            {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
            {1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1},
            {1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1},
            {1, 4, 3, 3, 1, 1, 3, 3, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 4, 1},
            {1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 1},
            {1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 1},
            {1, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 3, 3, 1},
            {1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1},
            {1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1},
            {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };


    private final byte[][] tileMap;
    private final List<Vector2i> energizerTiles;
    private final BitSet eaten;
    private final List<Portal> portals;
    private final int totalFoodCount;
    private House house;
    private int uneatenFoodCount;


    private byte[][] loadedMap;

    public World() {
        tileMap = validateTileMapData(PACMAN_MAP_SOURCE);

        // build portals
        var portalList = new ArrayList<Portal>();
        int lastColumn = numCols() - 1;
        for (int row = 0; row < numRows(); ++row) {
            var leftBorderTile = new Vector2i(0, row);
            var rightBorderTile = new Vector2i(lastColumn, row);
            if (tileMap[row][0] == T_TUNNEL && tileMap[row][lastColumn] == T_TUNNEL) {
                portalList.add(new Portal(leftBorderTile, rightBorderTile, 2));
            }
        }

        portalList.trimToSize();
        portals = Collections.unmodifiableList(portalList);
        eaten = new BitSet(numCols() * numRows());
        totalFoodCount = (int) tiles().filter(this::isFoodTile).count();
        uneatenFoodCount = totalFoodCount;

        energizerTiles = tiles().filter(this::isEnergizerTile).collect(Collectors.toList());


    }


    public House house() {
        return house;
    }

    public void setHouse(House house) {
        Objects.requireNonNull(house);
        this.house = house;
    }

    public Stream<Vector2i> energizerTiles() {
        return energizerTiles.stream();
    }


    private byte content(Vector2i tile) {
        return insideBounds(tile) ? tileMap[tile.y()][tile.x()] : T_SPACE;
    }

    public boolean insideBounds(Vector2i tile) {
        return 0 <= tile.x() && tile.x() < numCols() && 0 <= tile.y() && tile.y() < numRows();
    }

    public boolean insideBounds(double x, double y) {
        return 0 <= x && x < numCols() * TILE_SIZE && 0 <= y && y < numRows() * TILE_SIZE;
    }


    private int index(Vector2i tile) {
        return numCols() * tile.y() + tile.x();
    }


    public List<Portal> portals() {
        return portals;
    }


    public boolean belongsToPortal(Vector2i tile) {
        Objects.requireNonNull(tile);
        return portals.stream().anyMatch(portal -> portal.contains(tile));
    }


    public boolean isWall(Vector2i tile) {
        Objects.requireNonNull(tile);
        return content(tile) == T_WALL;
    }

    public boolean isTunnel(Vector2i tile) {
        Objects.requireNonNull(tile);
        return content(tile) == T_TUNNEL;
    }

    public boolean isEnergizerTile(Vector2i tile) {
        Objects.requireNonNull(tile);
        return content(tile) == T_ENERGIZER;
    }

    public boolean isFoodTile(Vector2i tile) {
        Objects.requireNonNull(tile);
        return content(tile) == T_PELLET || content(tile) == T_ENERGIZER;
    }

    public boolean isIntersection(Vector2i tile) {
        Objects.requireNonNull(tile);

        if(tile.x() <= 0 || tile.x() >= numCols() - 1) {
            return false;
        }

        if(house.contains(tile)) {
            return false;
        }

        long numWallNeighbors = tile.neighbors().filter(this::isWall).count();
        long numDoorNeighbors = tile.neighbors().filter(house.door()::occupies).count();
        return numWallNeighbors + numDoorNeighbors < 2;
    }


    public int totalFoodCount() {
        return totalFoodCount;
    }

    public int uneatenFoodCount() {
        return uneatenFoodCount;
    }

    public int eatenFoodCount() {
        return totalFoodCount - uneatenFoodCount;
    }

    public void removeFood(Vector2i tile) {
        if (hasFoodAt(tile)) {
            eaten.set(index(tile));
            --uneatenFoodCount;
        }
    }


    public boolean hasFoodAt(Vector2i tile) {
        checkNotNull(tile);
        if (insideBounds(tile)) {
            byte data = tileMap[tile.y()][tile.x()];
            return (data == T_PELLET || data == T_ENERGIZER) && !eaten.get(index(tile));
        }
        return false;
    }

    public boolean hasEatenFoodAt(Vector2i tile) {
        checkNotNull(tile);
        return insideBounds(tile) && eaten.get(index(tile));
    }


    public int numCols() {
        return tileMap[0].length;
    }

    public int numRows() {
        return tileMap.length;
    }

    public Stream<Vector2i> tiles() {
        return IntStream.range(0, numCols() * numRows()).mapToObj(this::tile);
    }

    public Vector2i tile(int index) {
        return new Vector2i(index % numCols(), index / numCols());
    }




    public void drawMap(GraphicsContext gc) {
        gc.drawImage(mapImage, 0, TILE_SIZE * 3, MAP_WIDTH, MAP_HEIGHT);
    }
    private void resetMap() {
        loadedMap = new byte[TILES_X][TILES_Y];
        for (int i = 0; i < TILES_X; i++) {
            for (int j = 0; j < TILES_Y; j++) {
                loadedMap[i][j] = PACMAN_MAP_SOURCE[j][i];
            }
        }
    }


}
