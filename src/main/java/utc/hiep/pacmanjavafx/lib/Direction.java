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

    UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0);

    private static final Direction[] OPPOSITE = {DOWN, RIGHT, UP, LEFT};        //opposite direction

    public static Stream<Direction> stream() {
        return Stream.of(values());
    }

    public static List<Direction> shuffled() {
        List<Direction> dirs = Arrays.asList(values());
        Collections.shuffle(dirs);
        return dirs;
    }

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

    public static Direction randomDirection(Direction currentDir) {
        List<Direction> dirs = shuffled();
        int nextDir = Global.RND.nextInt(4);
//        while (dirs.get(nextDir) == currentDir.opposite()) {
//            nextDir = Global.RND.nextInt(4);
//        }
        return dirs.get(nextDir);
    }

    public boolean isHigherPriority(Direction other) {
        return this.ordinal() >= other.ordinal();
    }

}
