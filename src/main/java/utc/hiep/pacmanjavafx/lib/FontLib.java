package utc.hiep.pacmanjavafx.lib;

import javafx.scene.text.Font;

public class FontLib {
    public static final String FONT_PATH = "asserts/fonts/emulogic.ttf";

    public static Font EMULOGIC(int fontSize) {
        return Font.loadFont(FontLib.class.getResourceAsStream(FONT_PATH), fontSize);
    }
}
