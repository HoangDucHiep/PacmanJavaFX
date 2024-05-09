package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import utc.hiep.pacmanjavafx.lib.fVector2D;
import utc.hiep.pacmanjavafx.lib.iVector2D;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public abstract class Entity {
    protected boolean visible = false;
    private float posX;                     //coordinate - by pixel
    private float posY;                     //coordinate - by pixel

    public void reset() {
        visible = false;
        posX = 0;
        posY = 0;
    }

    /**
     * Check if the entity is visible
     * @return true if the entity is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Show the entity
     */
    public void show() {
        visible = true;
    }

    /**
     * Hide the entity
     */
    public void hide() {
        visible = false;
    }

    /**
     * Get the x coordinate of the entity
     * @return the x coordinate of the entity
     */
    public float posX() {
        return posX;
    }

    /**
     * Get the y coordinate of the entity
     * @return the y coordinate of the entity
     */
    public float posY() {
        return posY;
    }

    public fVector2D position() {
        return new fVector2D(posX, posY);
    }

    /**
     * set the x coordinate of the entity
     * @param posX the x coordinate
     */
    public void setPosX(float posX) {
        this.posX = posX;
    }

    /**
     * set the y coordinate of the entity
     * @param posY the y coordinate
     */
    public void setPosY(float posY) {
        this.posY = posY;
    }

    /**
     * Set the position of the entity
     * @param position the position of the entity
     */
    public void setPosition(fVector2D position) {
        checkNotNull(position);
        posX = position.x();
        posY = position.y();
    }

    /**
     * Set the position of the entity
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

    /**
     * position of a center of the entity
     * @return the position of the center of the entity
     */
    public fVector2D center() {
        return new fVector2D(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }

    /**
     * Get the tile that the entity is at
     * @return the tile that the entity is at
     */
    public iVector2D atTile() {
        return tileAt(posX + HALF_TILE_SIZE, posY + HALF_TILE_SIZE);
    }

    /**
     * Get the offset of the entity from the center of the tile
     * @return the offset of the entity from the center of the tile, if the entity is at a center of tile, return VECTOR_ZERO
     */
    public fVector2D offset() {
        var tile = atTile();
        return new fVector2D(posX - TILE_SIZE * tile.x(), posY - TILE_SIZE * tile.y());
    }

    /**
     * Check if the entity is at the same tile as another entity
     * Used for collision detection
     * @param other another entity
     * @return true if the two entities are at the same tile
     */
    public boolean sameTile(Entity other) {
        return atTile().equals(other.atTile());
    }


    /**
     * Render the entity to the canvas
     * @param gc the graphics context
     */
    public abstract void render(GraphicsContext gc);
}
