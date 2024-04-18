package utc.hiep.pacmanjavafx.model.entity;

import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public class Entity {
    protected boolean visible = false;
    private float posX;
    private float posY;
    private float velX;
    private float velY;
    private float accX;
    private float accY;


    public void reset() {
        visible = false;
        posX = 0;
        posY = 0;
        velX = 0;
        velY = 0;
        accX = 0;
        accY = 0;
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

    public Vector2f velocity() {
        return new Vector2f(velX, velY);
    }

    public void setVelocity(Vector2f velocity) {
        checkNotNull(velocity);
        velX = velocity.x();
        velY = velocity.y();
    }


    public void setVelocity(float x, float y) {
        velX = x;
        velY = y;
    }


    public Vector2f acceleration() {
        return new Vector2f(accX, accY);
    }

    public void setAcceleration(Vector2f acceleration) {
        checkNotNull(acceleration, "Acceleration of entity must not be null");
        accX = acceleration.x();
        accY = acceleration.y();
    }

    public void setAcceleration(float ax, float ay) {
        accX = ax;
        accY = ay;
    }


    public void move() {
        posX += velX;
        posY += velY;
        velX += accX;
        velY += accY;
    }


    public Vector2i atTile() {
        return tileAt(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }



    public Vector2f offset() {
        var tile = atTile();
        return new Vector2f(posX - TILE_SIZE + tile.x(), posY - TILE_SIZE + tile.y());
    }


    public boolean sameTile(Entity other) {
        return atTile().equals(other.atTile());
    }
}
