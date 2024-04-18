package utc.hiep.pacmanjavafx.lib;

import java.util.Objects;

public class Global {
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;

    public static final int TILE_SIZE = 16;
    public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
    public static final int ORIGINAL_TILE_SIZE = 8;
    public static final int MAP_WIDTH = TILE_SIZE * 28;
    public static final int MAP_HEIGHT = TILE_SIZE * 31;


    public static void checkNotNull(Object value) {
        Objects.requireNonNull(value, "");
    }

}
