package utc.hiep.pacmanjavafx.lib;


import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


/**
 * Global constants and utility methods.
 */
public class Global {
    public static double WINDOW_WIDTH = 1024;                //window width
    public static double WINDOW_HEIGHT = 768;                //window height

    public static final int TILE_SIZE = 16;                     //tile size
    public static final int HALF_TILE_SIZE = TILE_SIZE / 2;     //half-tile size

    public static final int ORIGINAL_TILE_SIZE = 8;             //unscaled tile size

    public static final int TILES_X = 28;
    public static final int TILES_Y = 36;

    public static final int GAME_SCALE = TILE_SIZE / ORIGINAL_TILE_SIZE;



    public static void checkNotNull(Object value) {
        Objects.requireNonNull(value, "");
    }

    public static final Random RND = new Random();

    /**
     * @param position a position
     * @return tile containing given position
     */
    public static iVector2D tileAt(fVector2D position) {
        checkNotNull(position);
        return tileAt(position.x(), position.y());
    }


    /**
     * @param x x position
     * @param y y position
     * @return tile containing given position
     */
    public static iVector2D tileAt(float x, float y) {
        return new iVector2D((int) (x / TILE_SIZE), (int) (y / TILE_SIZE));
    }


    /**
     * @param tileX tile x Index
     * @param tileY tile y Index
     * @return position half-tile right of tile origin
     */
    public static fVector2D halfTileRightOf(int tileX, int tileY) {
        return new fVector2D((float) (TILE_SIZE * tileX + HALF_TILE_SIZE) / TILE_SIZE, tileY);
    }

    /**
     * @param tileX tile x Index
     * @param tileY tile y Index
     * @return position half-tile right of tile origin
     */
    public static fVector2D halfTileLeftOf(int tileX, int tileY) {
        return new fVector2D((float) (TILE_SIZE * tileX - HALF_TILE_SIZE) / TILE_SIZE, tileY);
    }


    /**
     * @param tileX tile x index
     * @param tileY tile y index
     * @return position the center of the given tile in coordinate base
     */
    public static fVector2D centerOfTile(int tileX, int tileY) {
        return new fVector2D(TILE_SIZE * tileX + HALF_TILE_SIZE, TILE_SIZE * tileY + HALF_TILE_SIZE);
    }


    /**
     * @param tile tile in grid
     * @return position of the given tile in coordinate base
     */
    public static fVector2D centerOfTile(iVector2D tile) {
        return new fVector2D(TILE_SIZE * tile.x() + HALF_TILE_SIZE, TILE_SIZE * tile.y() + HALF_TILE_SIZE);
    }


    /**
     * @param tiles index of tile
     * @return position of the given tile
     */
    public static double t(double tiles) {
        return tiles * TILE_SIZE;
    }

    public static void checkNotNull(Object value, String message) {
        Objects.requireNonNull(value, message);
    }

    public static void checkGhostID(byte id) {
        if (id < 0 || id > 3) {
            throw new IllegalArgumentException("Illegal ghost ID: " + id);
        }
    }

    /**
     * @param a left interval bound
     * @param b right interval bound
     * @return Random integer number from right-open interval <code>[a; b[</code>. Interval bounds are rearranged to
     * guarantee <code>a<=b</code>
     */
    public static int randomInt(int a, int b) {
        if (a > b) {
            var tmp = a;
            a = b;
            b = tmp;
        }
        return a + RND.nextInt(b - a);
    }

    /**
     * @param a left interval bound
     * @param b right interval bound
     * @return Random floating-point number from right-open interval <code>[a; b[</code>. Interval bounds are rearranged
     * to guarantee <code>a<=b</code>
     */
    public static float randomFloat(float a, float b) {
        if (a > b) {
            var tmp = a;
            a = b;
            b = tmp;
        }
        return a + (b - a) * RND.nextFloat();
    }

    /**
     * @param a left interval bound
     * @param b right interval bound
     * @return Random double-precision floating-point number from right-open interval <code>[a; b[</code>. Interval bounds
     * are rearranged to guarantee <code>a<=b</code>
     */
    public static double randomDouble(double a, double b) {
        if (a > b) {
            var tmp = a;
            a = b;
            b = tmp;
        }
        return a + (b - a) * RND.nextDouble();
    }


    /**
     * @param n some integer
     * @return {@code true} if the given number is even
     */
    public static boolean isEven(int n) {
        return n % 2 == 0;
    }

    /**
     * @param n some integer
     * @return {@code true} if the given number is odd
     */
    public static boolean isOdd(int n) {
        return n % 2 != 0;
    }


    /**
     * @param delta  maximum allowed deviation (non-negative number)
     * @param value  value
     * @param target target value
     * @return {@code true} if the given values differ at most by the given difference
     */
    public static boolean differsAtMost(double delta, double value, double target) {
        if (delta < 0) {
            throw new IllegalArgumentException(String.format("Difference must not be negative but is %f", delta));
        }
        return value >= (target - delta) && value <= (target + delta);
    }

    @SafeVarargs
    public static <T> boolean oneOf(T value, T... alternatives) {
        return switch (alternatives.length) {
            case 0 -> false;
            case 1 -> value.equals(alternatives[0]);
            default -> Arrays.asList(alternatives).contains(value);
        };
    }
}
