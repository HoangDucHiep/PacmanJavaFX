package utc.hiep.pacmanjavafx.lib;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * Moving directions
 * Use for entity movement, velocity, etc.
 */
public enum Direction {

    LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);

    private static final Direction[] OPPOSITE = {RIGHT, LEFT, DOWN, UP};        //opposite direction

    public static Stream<Direction> stream() {
        return Stream.of(values());
    }

    public static List<Direction> shuffled() {
        List<Direction> dirs = Arrays.asList(values());
        Collections.shuffle(dirs);
        return dirs;
    }

    private final Vector2i vector;

    Direction(int x, int y) {
        vector = new Vector2i(x, y);
    }

    public Vector2i vector() {
        return vector;
    }

    public Direction opposite() {
        return OPPOSITE[ordinal()];
    }

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
