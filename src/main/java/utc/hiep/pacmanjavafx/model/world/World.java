package utc.hiep.pacmanjavafx.model.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.lib.Vector2i;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static utc.hiep.pacmanjavafx.lib.Global.*;


public class World {

    private static Image mapImage = ImageLibrary.MAP_EMPTY;
    private static Image mapFlashingImage = ImageLibrary.FLASHING_MAZE;

    int MAP_WIDTH = TILES_X * TILE_SIZE;
    int MAP_HEIGHT = (TILES_Y - 5) * TILE_SIZE;

    public static final byte T_SPACE = 0;
    public static final byte T_WALL = 1;
    public static final byte T_TUNNEL = 2;
    public static final byte T_PELLET = 3;
    public static final byte T_ENERGIZER = 4;


    private final byte[][] tileMap;                             //tile map data
    private final List<Vector2i> energizerTiles;                //positions of energizer tiles
    private final BitSet eaten;                                 //eaten food
    private Portal portals;                         //positions of portals
    private final int totalFoodCount;                           //total number of food tiles
    private House house;                                        //house
    private int uneatenFoodCount;                               //number of uneaten food tiles


    public World(byte[][] mapSource) {
        tileMap = validateTileMapData(mapSource);

        // build portals
        int lastColumn = numCols() - 1;
        for (int row = 0; row < numRows(); ++row) {
            var leftBorderTile = new Vector2i(0, row);
            var rightBorderTile = new Vector2i(lastColumn, row);
            if (tileMap[row][0] == T_TUNNEL && tileMap[row][lastColumn] == T_TUNNEL) {
                portals = new Portal(leftBorderTile, rightBorderTile, 2);
            }
        }

        ///Init food-stuff
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


    /**
     * @param tile some tile
     * @return type of the given tile
     */
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


    public Portal portals() {
        return portals;
    }


    public boolean belongsToPortal(Vector2i tile) {
        Objects.requireNonNull(tile);
        return portals.contains(tile);
    }


    public boolean isWall(Vector2i tile) {
        Objects.requireNonNull(tile);
        return content(tile) == T_WALL;
    }


    /**
     * @param tile some tile
     * @return true if the given tile is a tunnel, which is a portal, false otherwise.
     */
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


    /**
     * @param tile some tile
     * @return true if the given tile is an intersection, false otherwise.
     */
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



}
