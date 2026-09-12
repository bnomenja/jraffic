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
        return false;
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
        cars.sort(Comparator.comparingDouble(this::progress).reversed());

        double minimumSpacing = Config.CAR_LENGTH + Config.SAFETY_GAP;
        Car carAhead = null;
        for (Car car : cars) {
            if (carAhead == null) {
                car.move(dt);
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
}
