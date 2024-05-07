package utc.hiep.pacmanjavafx.lib;

import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLib {
    public static Image BACKGROUND_IMAGE = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/background.png")));
    public static Image SPRITE_SHEET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/sprite_sheet.png")), 224, 248, true, true);
    public static Image PELLET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/pellet.png")));
    public static Image ENERGIZER_SHEET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/energizer_sheet.png")));
    public static Image MENU_SCENE_BG = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/menuscenebg.png")));
    public static Image GAME_LOGO = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/game_logo.png")), Global.TILE_SIZE * 40, Double.MIN_NORMAL, true, true);
    public static Image APP_ICON = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/icons/pacman.png")));
    public static Image MAP_SHEET = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/map_sheet.png")));
    public static Image SCORE_SCENE_LOGO = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/high_score_logo.png")), Global.TILE_SIZE * 40, Double.MIN_NORMAL, true, true);
    public static Image PAC_AND_GHOST = new Image(Objects.requireNonNull(ImageLib.class.getResourceAsStream("asserts/graphics/pac_and_ghosts.gif")),Global.TILE_SIZE * 20, Double.MIN_NORMAL, true, true);
}
