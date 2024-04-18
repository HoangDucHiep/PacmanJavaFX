package utc.hiep.pacmanjavafx.lib;

import javafx.scene.image.Image;

import java.util.Objects;

public class IMAGE_LIB {
    private static String BACKGROUND_IMAGE_PATH = "asserts/graphics/background.png";
    private static String FLASHING_MAZE_PATH = "asserts/graphics/maze_flashing.png";
    private static String PACMAN_SPRITE_PATH = "asserts/graphics/pacman_spritesheet.png";
    private static String MAP_EMPTY_PATH = "asserts/graphics/map.png";

    public static Image BACKGROUND_IMAGE = new Image(Objects.requireNonNull(IMAGE_LIB.class.getResourceAsStream(BACKGROUND_IMAGE_PATH)));;
    public static Image FLASHING_MAZE = new Image(Objects.requireNonNull(IMAGE_LIB.class.getResourceAsStream(FLASHING_MAZE_PATH)), 448, 496, false, true);
    public static Image PACMAN_SPRITE = new Image(Objects.requireNonNull(IMAGE_LIB.class.getResourceAsStream(PACMAN_SPRITE_PATH)));
    public static Image MAP_EMPTY = new Image(Objects.requireNonNull(IMAGE_LIB.class.getResourceAsStream(MAP_EMPTY_PATH)));
}
