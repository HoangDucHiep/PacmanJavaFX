package utc.hiep.pacmanjavafx.lib;

import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLibrary {
    private static final String BACKGROUND_IMAGE_PATH = "asserts/graphics/background.png";
    private static final String FLASHING_MAZE_PATH = "asserts/graphics/maze_flashing.png";
    private static final String SPRITE_SHEET_PATH = "asserts/graphics/sprite_sheet.png";
    private static final String MAP_EMPTY_PATH = "asserts/graphics/map.png";


    public static Image BACKGROUND_IMAGE = new Image(Objects.requireNonNull(ImageLibrary.class.getResourceAsStream(BACKGROUND_IMAGE_PATH)));;
    public static Image FLASHING_MAZE = new Image(Objects.requireNonNull(ImageLibrary.class.getResourceAsStream(FLASHING_MAZE_PATH)), 448, 496, true, true);
    public static Image SPRITE_SHEET = new Image(Objects.requireNonNull(ImageLibrary.class.getResourceAsStream(SPRITE_SHEET_PATH)), 224, 248, true, true);
    public static Image MAP_EMPTY = new Image(Objects.requireNonNull(ImageLibrary.class.getResourceAsStream(MAP_EMPTY_PATH)));
}
