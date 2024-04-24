package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;

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


    public fVector2D position() {
        return new fVector2D(posX, posY);
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setPosition(fVector2D position) {
        checkNotNull(position);
        posX = position.x();
        posY = position.y();
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

    public fVector2D center() {
        return new fVector2D(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }


    public iVector2D atTile() {
        return tileAt(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }



    public fVector2D offset() {
        var tile = atTile();
        return new fVector2D(posX - TILE_SIZE * tile.x(), posY - TILE_SIZE * tile.y());
    }


    public boolean sameTile(Entity other) {
        return atTile().equals(other.atTile());
    }


    public abstract void render(GraphicsContext gc);
}
