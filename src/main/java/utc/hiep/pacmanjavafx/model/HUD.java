package utc.hiep.pacmanjavafx.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.lib.FontLib;
import utc.hiep.pacmanjavafx.lib.Global;
import utc.hiep.pacmanjavafx.lib.ImageLib;
import utc.hiep.pacmanjavafx.model.level.GameLevel;

public class HUD {
    private GameLevel level;

    private long currentScore;
    private int lives;
    private int currentLevel;

    private long highScore;
    private int highLevel;

    public HUD(GameLevel level, long highScore, int highLevel) {
        this.level = level;
        this.highScore = highScore;
        this.highLevel = highLevel;
    }

    public void update() {
        currentScore = level.score();
        lives = level.lives();
        currentLevel = level.levelNum();

        if(currentScore > highScore) {
            highScore = currentScore;
            highLevel = currentLevel;
        }
    }


    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(FontLib.EMULOGIC(Global.TILE_SIZE - 1));

        gc.fillText("SCORE", Global.TILE_SIZE,  Global.TILE_SIZE);
        gc.fillText(String.valueOf(currentScore), Global.TILE_SIZE, 2 * Global.TILE_SIZE);


        gc.fillText("L" + currentLevel, Global.TILE_SIZE * 9, 2 * Global.TILE_SIZE);


        gc.fillText("HIGH SCORE", Global.TILE_SIZE * 15, Global.TILE_SIZE);
        gc.fillText(String.valueOf(highScore), Global.TILE_SIZE * 15, 2 * Global.TILE_SIZE);
        gc.fillText("L" + highLevel, Global.TILE_SIZE * 22 + Global.HALF_TILE_SIZE, 2 * Global.TILE_SIZE);


        int i = 0;
        while(i < lives) {
            gc.drawImage(ImageLib.SPRITE_SHEET, 130, 17, 13, 13, Global.TILE_SIZE * (i * 2 + 2) + Global.HALF_TILE_SIZE, (Global.TILES_Y - 2) * Global.TILE_SIZE + (double) Global.HALF_TILE_SIZE / 2, Global.GAME_SCALE * 13, Global.GAME_SCALE * 13);
            i++;
        }

    }





}
