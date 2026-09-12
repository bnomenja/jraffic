package model;

import config.Config;

public class Car {
    private static int nextId = 1;

    private final int id;
    private final Direction origin;
    private final Turn turn;
    private final Direction exitDirection;

    private final double maxSpeed = Config.CAR_SPEED;
    private boolean moving = true;

    private double x;
    private double y;

    private final double[] turnPoint;
    private boolean turned = false;

    public Car(Direction origin, Turn turn, Direction exitDirection) {
        this.id = nextId++;
        this.origin = origin;
        this.turn = turn;
        this.exitDirection = exitDirection;

        double[] spawnPoint = computeSpawnPoint(origin);
        this.x = spawnPoint[0];
        this.y = spawnPoint[1];

        this.turnPoint = (turn == Turn.STRAIGHT) ? null : computeTurnPoint(origin, exitDirection);
    }

    private double[] computeSpawnPoint(Direction origin) {
        double centerX = Config.WINDOW_WIDTH / 2.0;
        double centerY = Config.WINDOW_HEIGHT / 2.0;
        double laneOffset = Config.ROAD_WIDTH / 2.0;

        return switch (origin) {
            case NORTH -> new double[]{ centerX - laneOffset, 0 };
            case SOUTH -> new double[]{ centerX + laneOffset, Config.WINDOW_HEIGHT };
            case EAST  -> new double[]{ Config.WINDOW_WIDTH, centerY - laneOffset };
            case WEST  -> new double[]{ 0, centerY + laneOffset };
        };
    }


    private double laneCoordinate(Direction d) {
        double centerX = Config.WINDOW_WIDTH / 2.0;
        double centerY = Config.WINDOW_HEIGHT / 2.0;
        double laneOffset = Config.ROAD_WIDTH / 2.0;

        return switch (d) {
            case NORTH -> centerX - laneOffset;
            case SOUTH -> centerX + laneOffset;
            case EAST  -> centerY - laneOffset;
            case WEST  -> centerY + laneOffset;
        };
    }

    private boolean isVertical(Direction d) {
        return d == Direction.NORTH || d == Direction.SOUTH;
    }


    private double[] vectorFor(Direction d) {
        return switch (d) {
            case NORTH -> new double[]{ 0,  1 };
            case SOUTH -> new double[]{ 0, -1 };
            case EAST  -> new double[]{ -1, 0 };
            case WEST  -> new double[]{  1, 0 };
        };
    }

    private double[] computeTurnPoint(Direction origin, Direction exitDirection) {
        if (isVertical(origin)) {
            double x = laneCoordinate(origin);       
            double y = laneCoordinate(exitDirection);  
            return new double[]{ x, y };
        } else {
            double y = laneCoordinate(origin);         
            double x = laneCoordinate(exitDirection);  
            return new double[]{ x, y };
        }
    }

    private boolean reachedTurnPoint() {
        if (turnPoint == null) return false;

        double[] v = vectorFor(origin);
        if (isVertical(origin)) {
            return v[1] > 0 ? y >= turnPoint[1] : y <= turnPoint[1];
        } else {
            return v[0] > 0 ? x >= turnPoint[0] : x <= turnPoint[0];
        }
    }

    public int getId() {
        return id;
    }

    public Direction getOrigin() {
        return origin;
    }

    public Turn getTurn() {
        return turn;
    }

    public Direction getExitDirection() {
        return exitDirection;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public boolean isMoving() {
        return moving;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void move(double dt) {
        move(dt, Double.POSITIVE_INFINITY);
    }

    /**
     * Moves this car for at most {@code maxDistance} pixels.  A lane uses
     * this overload to keep a following car from reaching the car ahead.
     */
    public void move(double dt, double maxDistance) {
        double distance = Math.min(maxSpeed * dt, Math.max(0, maxDistance));

        if (!turned && reachedTurnPoint()) {
            x = turnPoint[0];
            y = turnPoint[1];
            turned = true;
        }

        Direction activeDirection = turned ? exitDirection : origin;
        double[] v = vectorFor(activeDirection);

        x += v[0] * distance;
        y += v[1] * distance;
    }
}
