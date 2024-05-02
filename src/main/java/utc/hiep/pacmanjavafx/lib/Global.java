package utc.hiep.pacmanjavafx.lib;

import javafx.stage.Screen;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

public class Global {
    public static final double WINDOW_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();                //window width
    public static final double WINDOW_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();                //window height

    public static final int TILE_SIZE = 16;                     //tile size
    public static final int HALF_TILE_SIZE = TILE_SIZE / 2;     //half tile size

    public static final int ORIGINAL_TILE_SIZE = 8;             //unscaled tile size

    public static final int TILES_X = 28;
    public static final int TILES_Y = 36;

    public static final int GAME_SCALE = TILE_SIZE / ORIGINAL_TILE_SIZE;

    public static final int SPRITE_SHEET_WIDTH = 224;


    public static void checkNotNull(Object value) {
        Objects.requireNonNull(value, "");
    }

    public static final Random RND = new Random();

    private static final String MSG_GAME_NULL = "Game model must not be null";
    private static final String MSG_LEVEL_NULL = "Game level must not be null";
    private static final String MSG_TILE_NULL = "Tile must not be null";
    private static final String MSG_DIR_NULL = "Direction must not be null";


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
     * @return position half tile right of tile origin
     */
    public static fVector2D halfTileRightOf(int tileX, int tileY) {
        return new fVector2D((float) (TILE_SIZE * tileX + HALF_TILE_SIZE) / TILE_SIZE, tileY);
    }

    /**
     * @param tileX tile x Index
     * @param tileY tile y Index
     * @return position half tile right of tile origin
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

//    public static void checkGameNotNull(GameModel game) {
//        checkNotNull(game, MSG_GAME_NULL);
//    }
//
    public static void checkGhostID(byte id) {
        if (id < 0 || id > 3) {
            throw new IllegalArgumentException("Illegal ghost ID: " + id);
        }
    }
//
//    public static void checkLevelNumber(int number) {
//        if (number < 1) {
//            throw new IllegalLevelNumberException(number);
//        }
//    }


//    public static void checkLevelNotNull(GameLevel level) {
//        checkNotNull(level, MSG_LEVEL_NULL);
//    }
//
//    public static void checkDirectionNotNull(Direction dir) {
//        checkNotNull(dir, MSG_DIR_NULL);
//    }

    public static double requirePositive(double value, String messageFormat) {
        if (value < 0) {
            throw new IllegalArgumentException(String.format(messageFormat, value));
        }
        return value;
    }

    public static double requirePositive(double value) {
        return requirePositive(value, "%f must be positive");
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
     * @param percent percentage value
     * @return {@code true} with the given probability
     */
    public static boolean inPercentOfCases(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException(String.format("Percent value must be in range [0, 100] but is %d", percent));
        }
        if (percent == 0) {
            return false;
        }
        if (percent == 100) {
            return true;
        }
        return randomInt(0, 100) < percent;
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
     * @param value some integer
     * @return value divided by 100
     */
    public static float percent(int value) {
        return value / 100f;
    }

    public static float positive(float value) {
        return value < 0 ? -value : value;
    }

    /**
     * @param value1 value1
     * @param value2 value2
     * @param t      "time" between 0 and 1
     * @return linear interpolation between {@code value1} and {@code value2} values
     */
    public static double lerp(double value1, double value2, double t) {
        return (1 - t) * value1 + t * value2;
    }

    /**
     * @param value some value
     * @param min   lower bound of interval
     * @param max   upper bound of interval
     * @return the value if inside the interval, the lower bound if the value is smaller, the upper bound if the value is
     * larger
     */
    public static float clamp(float value, float min, float max) {
        return (value < min) ? min : Math.min(value, max);
    }

    /**
     * @param value some value
     * @param min   lower bound of interval
     * @param max   upper bound of interval
     * @return the value if inside the interval, the lower bound if the value is smaller, the upper bound if the value is
     * larger
     */
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
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

    public static byte[][] copyByteArray2D(byte[][] array) {
        return Arrays.stream(array).map(byte[]::clone).toArray(byte[][]::new);
    }

    @SafeVarargs
    public static <T> boolean oneOf(T value, T... alternatives) {
        switch (alternatives.length) {
            case 0:
                return false;
            case 1:
                return value.equals(alternatives[0]);
            default:
                return Stream.of(alternatives).anyMatch(value::equals);
        }
    }
}
