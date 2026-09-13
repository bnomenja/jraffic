package model;

import config.Config;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class Lane{
    private Direction dir;
    private List<Car> cars;

    public Lane(Direction dir){
        this.dir = dir;
        this.cars = new ArrayList<>();
    }

    public boolean isFull(){
        return cars.size() >= getCapacity();
    }

    public Direction getDir() {
        return this.dir;
    }

    public int getCapacity() {
        return (int) Math.floor(getLengthToStopLine() /
                (Config.CAR_LENGTH + Config.SAFETY_GAP));
    }

    public double getCongestionRatio() {
        return cars.size() / (double) getCapacity();
    }

    public List<Car> getCars(){
        return this.cars;
    }

    public void addCar(Car c){
        cars.add(c);
    }

    public void moveCars(double dt) {
        moveCars(dt, true);
    }

    public void moveCars(double dt, boolean mayEnterIntersection) {
        cars.sort(Comparator.comparingDouble(this::progress).reversed());

        double minimumSpacing = Config.CAR_LENGTH + Config.SAFETY_GAP;
        Car carAhead = null;
        for (Car car : cars) {
            if (carAhead == null) {
                double maxDistance = Double.POSITIVE_INFINITY;
                if (!mayEnterIntersection) {
                    maxDistance = stopProgress() - Config.CAR_LENGTH- progress(car);
                }
                car.move(dt, maxDistance);
            } else {
                double availableDistance = progress(carAhead) - minimumSpacing - progress(car);
                car.move(dt, availableDistance);
            }
            carAhead = car;
        }
    }

    private double progress(Car car) {
        return switch (dir) {
            case NORTH -> car.getY();
            case SOUTH -> -car.getY();
            case EAST -> -car.getX();
            case WEST -> car.getX();
        };
    }

    private double stopProgress() {
        double centerX = Config.WINDOW_WIDTH / 2.0;
        double centerY = Config.WINDOW_HEIGHT / 2.0;
        double offset = Config.STOP_LINE_OFFSET;

        return switch (dir) {
            case NORTH -> centerY - offset;
            case SOUTH -> -(centerY + offset);
            case EAST -> -(centerX + offset);
            case WEST -> centerX - offset;
        };
    }

    private double spawnProgress() {
            return stopProgress() - getLengthToStopLine();
        }

        public boolean canSpawn() {
        if (cars.isEmpty()) {
            return true;
        }

        double closestToSpawn = cars.stream()
                .mapToDouble(this::progress)
                .min()
                .orElse(Double.POSITIVE_INFINITY);

        double minimumSpacing = Config.CAR_LENGTH + Config.SAFETY_GAP;
        return closestToSpawn - spawnProgress() >= minimumSpacing;
    }

    private double getLengthToStopLine() {
        return switch (dir) {
            case NORTH, SOUTH -> Config.WINDOW_HEIGHT / 2.0 - Config.STOP_LINE_OFFSET;
            case EAST, WEST -> Config.WINDOW_WIDTH / 2.0 - Config.STOP_LINE_OFFSET;
        };
    }
}
