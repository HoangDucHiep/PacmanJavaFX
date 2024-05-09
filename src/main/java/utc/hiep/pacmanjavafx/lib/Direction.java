package utc.hiep.pacmanjavafx.lib;
/**
 * Moving directions
 * Use it for entity movement, velocity, etc.
 */
public enum Direction {

    UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0);

    private static final Direction[] OPPOSITE = {DOWN, RIGHT, UP, LEFT};        //opposite direction

    private final iVector2D vector;
    Direction(int x, int y) {
        vector = new iVector2D(x, y);
    }
    public iVector2D vector() {
        return vector;
    }
    public Direction opposite() {
        return OPPOSITE[ordinal()];
    }

    /**
     * @return Next direction in anti-clockwise order
     */
    public Direction nextAntiClockwise() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }
    public Direction nextClockwise() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }
    public boolean sameOrientation(Direction other) {
        return isHorizontal() && other.isHorizontal() || isVertical() && other.isVertical();
    }

}
