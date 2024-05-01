package utc.hiep.pacmanjavafx.lib;

import java.util.Objects;

import static utc.hiep.pacmanjavafx.lib.Global.centerOfTile;

public final class fVector2D {

    public static final fVector2D ZERO = new fVector2D(0, 0);             //Vector zero
    public static final float EPSILON = 1e-6f;                                //epsilon

    private final float x;                          //x coordinate
    private final float y;                          //y coordinate

    /**
     * @param x x coordinate
     * @param y y coordinate
     * Constructor with 2 parameters x and y
     */
    public fVector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Getter X
     * @return x coordinate
     */
    public float x() {
        return x;
    }


    /**
     * Getter Y
     * @return y coordinate
     */
    public float y() {
        return y;
    }


    /**
     * @param v other vector
     * @return new vector with x and y added by other vector
     */
    public fVector2D plus(fVector2D v) {
        return new fVector2D(x + v.x, y + v.y);
    }


    /**
     * @param vx x coordinate
     * @param vy y coordinate
     * @return new vector with x and y added by vx and vy
     */
    public fVector2D plus(float vx, float vy) {
        return new fVector2D(x + vx, y + vy);
    }


    /**
     * @param v other vector
     * @return new vector with x and y subtracted by other vector
     */
    public fVector2D minus(fVector2D v) {
        return new fVector2D(x - v.x, y - v.y);
    }


    /**
     * @param vx x coordinate
     * @param vy y coordinate
     * @return new vector with x and y subtracted by vx and vy
     */
    public fVector2D minus(float vx, float vy) {
        return new fVector2D(x - vx, y - vy);
    }


    /**
     * @param s scale
     * @return new vector with x and y scaled by s
     */
    public fVector2D scaled(float s) {
        return new fVector2D(s * x, s * y);
    }


    /**
     * @return new vector with x and y negated
     */
    public fVector2D inverse() {
        return new fVector2D(-x, -y);
    }


    /**
     * @return length of vector
     */
    public float length() {
        return (float) Math.hypot(x, y);
    }


    /**
     * @return new vector with x and y normalized
     */
    public fVector2D normalized() {
        float len = length();
        return new fVector2D(x / len, y / len);
    }


    /**
     * @param v other vector
     * @return dot product of 2 vectors
     */
    public float sqrEuclideanDistance(iVector2D v) {
        return (v.x() - x()) * (v.x() - x()) + (v.y() - y()) * (v.y() - y());
    }



    /**
     * @param v other vector
     * @param dx x coordinate
     * @param dy y coordinate
     * @return true if 2 vectors are almost equal
     */
    public boolean almostEquals(fVector2D v, float dx, float dy) {
        return differsAtMost(dx, x, v.x) && differsAtMost(dy, y, v.y);
    }


    /**
     * @param delta difference
     * @param value value
     * @param target target
     * @return true if value is in range of target +- delta
     */
    public static boolean differsAtMost(double delta, double value, double target) {
        if (delta < 0) {
            throw new IllegalArgumentException(String.format("Difference must not be negative but is %f", delta));
        }
        return value >= (target - delta) && value <= (target + delta);
    }


    /**
     * @return hash code of vector
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        fVector2D v = (fVector2D) other;
        return Math.abs(v.x - x) <= EPSILON && Math.abs(v.y - y) <= EPSILON;
    }


    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }
}
