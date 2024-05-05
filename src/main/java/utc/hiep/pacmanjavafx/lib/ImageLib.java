package utc.hiep.pacmanjavafx.lib;

import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLib {
    private static final String BACKGROUND_IMAGE_PATH = "asserts/graphics/background.png";
    private static final String FLASHING_MAZE_PATH = "asserts/graphics/maze_flashing.png";
    private static final String SPRITE_SHEET_PATH = "asserts/graphics/sprite_sheet.png";
    private static final String MAP_EMPTY_PATH = "asserts/graphics/map.png";
    private static final String PELLET_PATH = "asserts/graphics/pellet.png";
    private static final String ENERGIZER_PATH = "asserts/graphics/energizer_sheet.png";
    private static final String MENU_SCENE_BG_PATH = "asserts/graphics/menuscenebg.png";
    private static final String LOGO_PATH = "asserts/graphics/game_logo.png";
    private static final String APP_ICON_PATH = "asserts/graphics/icons/pacman.png";
    private static final String MAP_SHEET_PATH = "asserts/graphics/map_sheet.png";
    private static final String SCORE_SCENE_LOGO_PATH = "asserts/graphics/high_score_logo.png";

    public static Image BACKGROUND_IMAGE = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(BACKGROUND_IMAGE_PATH)));;
    public static Image FLASHING_MAZE = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(FLASHING_MAZE_PATH)), 448, 496, true, true);
    public static Image SPRITE_SHEET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(SPRITE_SHEET_PATH)), 224, 248, true, true);
    public static Image MAP_EMPTY = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(MAP_EMPTY_PATH)));
    public static Image PELLET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(PELLET_PATH)));
    public static Image ENERGIZER_SHEET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(ENERGIZER_PATH)));
    public static Image MENU_SCENE_BG = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(MENU_SCENE_BG_PATH)));
    public static Image GAME_LOGO = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(LOGO_PATH)));
    public static Image APP_ICON = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(APP_ICON_PATH)));
    public static Image MAP_SHEET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(MAP_SHEET_PATH)));
    public static Image SCORE_SCENE_LOGO = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream(SCORE_SCENE_LOGO_PATH)));
}
