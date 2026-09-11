package model;

import java.util.Random;

public enum Turn {
    STRAIGHT, LEFT, RIGHT;

    public static Turn random(Random random) {
        Turn[] values = values();
        return values[random.nextInt(values.length)];
    }

    public Direction exitDirection(Direction origin) {
        return switch (this) {
            case STRAIGHT -> origin;
            case RIGHT -> origin.turnRight();
            case LEFT -> origin.turnLeft();
        };
    }
}
