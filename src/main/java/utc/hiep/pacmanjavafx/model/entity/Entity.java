package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public abstract class Entity {
    protected boolean visible = false;
    private float posX;                     //coordinate in world
    private float posY;                     //coordinate in world



    public void reset() {
        visible = false;
        posX = 0;
        posY = 0;
    }

    public boolean isVisible() {
        return visible;
    }

//    public void setVisible(boolean visible) {
//        this.visible = visible;
//    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public float posX() {
        return posX;
    }

    public float posY() {
        return posY;
    }


    public Vector2f position() {
        return new Vector2f(posX, posY);
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setPosition(Vector2f position) {
        checkNotNull(position);
        posX = position.x();
        posY = position.y();
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

    public Vector2f center() {
        return new Vector2f(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }


    public Vector2i atTile() {
        return tileAt(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }



    public Vector2f offset() {
        var tile = atTile();
        return new Vector2f(posX - TILE_SIZE * tile.x(), posY - TILE_SIZE * tile.y());
    }


    public boolean sameTile(Entity other) {
        return atTile().equals(other.atTile());
    }



    public abstract void render(GraphicsContext gc);
}
