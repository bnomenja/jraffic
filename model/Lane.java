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

    /**
     * Maximum number of cars that fit between the spawn point and stop line.
     * Each car consumes its length plus the required safety gap.
     */
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

    /**
     * Advances cars from front to back while preserving the required
     * bumper-to-bumper clearance.  Cars are sorted by their current progress
     * rather than insertion order, so this also remains correct after a lane
     * receives cars from the intersection.
     */
    public void moveCars(double dt) {
        moveCars(dt, true);
    }

    /**
     * Moves an incoming lane while respecting its stop line. When entry is
     * not allowed, the lead car stops before the line and the following cars
     * remain separated by the normal safe distance.
     */
    public void moveCars(double dt, boolean mayEnterIntersection) {
        cars.sort(Comparator.comparingDouble(this::progress).reversed());

        double minimumSpacing = Config.CAR_LENGTH + Config.SAFETY_GAP;
        Car carAhead = null;
        for (Car car : cars) {
            if (carAhead == null) {
                double maxDistance = Double.POSITIVE_INFINITY;
                if (!mayEnterIntersection) {
                    // Car positions are their centres, so preserve a
                    // half-length margin before the painted stop line.
                    maxDistance = stopProgress() - Config.CAR_LENGTH / 2.0 - progress(car);
                }
                car.move(dt, maxDistance);
            } else {
                double availableDistance = progress(carAhead) - minimumSpacing - progress(car);
                car.move(dt, availableDistance);
            }
            carAhead = car;
        }
    }

    // Larger progress always means farther along this lane's direction.
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

    private double getLengthToStopLine() {
        return switch (dir) {
            case NORTH, SOUTH -> Config.WINDOW_HEIGHT / 2.0 - Config.STOP_LINE_OFFSET;
            case EAST, WEST -> Config.WINDOW_WIDTH / 2.0 - Config.STOP_LINE_OFFSET;
        };
    }
}
