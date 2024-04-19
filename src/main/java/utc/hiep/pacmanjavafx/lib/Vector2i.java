package utc.hiep.pacmanjavafx.lib;

import java.util.Objects;
import java.util.stream.Stream;


/**
 * A 2D vector use interger x and y.
 */

public final class Vector2i {

    public static final Vector2i ZERO = new Vector2i(0, 0);             //Vector zero

    private final int x;                                //x coordinate
    private final int y;                                //y coordinate

    /**
     * @param x x coordinate
     * @param y y coordinate
     * Constructor with 2 parameters x and y
     */
    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Getter X
     * @return x coordinate
     */
    public int x() {
        return x;
    }


    /**
     * Getter Y
     * @return y coordinate
     */
    public int y() {
        return y;
    }


    /**
     * @param s scale
     * @return new vector with x and y scaled by s
     */
    public Vector2i scaled(int s) {
        return new Vector2i(s * x, s * y);
    }


    /**
     * @param v other vector
     * @return new vector with x and y added by other vector
     */
    public Vector2i plus(Vector2i v) {
        return new Vector2i(x + v.x, y + v.y);
    }


    /**
     * @param dx x coordinate
     * @param dy y coordinate
     * @return new vector with x and y added by dx and dy
     */
    public Vector2i plus(int dx, int dy) {
        return new Vector2i(x + dx, y + dy);
    }


    /**
     * @param v other vector
     * @return new vector with x and y subtracted by other vector
     */
    public Vector2i minus(Vector2i v) {
        return new Vector2i(x - v.x, y - v.y);
    }


    /**
     * @param dx x coordinate
     * @param dy y coordinate
     * @return new vector with x and y subtracted by dx and dy
     */
    public Vector2i minus(int dx, int dy) {
        return new Vector2i(x - dx, y - dy);
    }


    /**
     * @return length of vector
     */
    public float euclideanDistance(Vector2i v) {
        return (float) Math.hypot(x - v.x, y - v.y);
    }


    /**
     * @param dx difference x
     * @param x1 x coordinate 1
     * @param x2 x coordinate 2
     * @return true if x1 and x2 are different at most dx
     */
    public float manhattanDistance(Vector2i v) {
        return Math.abs(x - v.x) + Math.abs(y - v.y);
    }


    /**
     * @return Stream of 4 neighbors of this vector (up, right, down, left)
     */
    public Stream<Vector2i> neighbors() {
        return Stream.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT).map(dir -> this.plus(dir.vector()));
    }

    @Override
    public String toString() {
        return String.format("(%2d,%2d)", x, y);
    }

    /**
     * @return new vector with x and y in float
     */
    public Vector2f toFloatVec() {
        return new Vector2f(x, y);
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector2i other = (Vector2i) obj;
        return x == other.x && y == other.y;
    }
}
