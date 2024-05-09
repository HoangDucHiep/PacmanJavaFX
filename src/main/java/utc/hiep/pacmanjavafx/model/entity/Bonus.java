package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.lib.Global;

public class Bonus extends Entity{
    private final int pointWorth;
    public Bonus(int pointWorth) {
        this.pointWorth = 100 * pointWorth;
    }

    public int getPointWorth() {
        return pointWorth;
    }
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(GameController.rm().getImage(GameController.SPRITE_SHEET), 32, 48, 16, 16, posX(), posY(), Global.TILE_SIZE * 2, Global.TILE_SIZE * 2);
    }
}
