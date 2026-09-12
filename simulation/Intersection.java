package simulation;

import config.Config;
import model.Car;
import model.Direction;
import model.Lane;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class Intersection {

    private final EnumMap<Direction, Lane> inLanes;
    private final EnumMap<Direction, Lane> outLanes;

    private final List<Car> carsInside = new ArrayList<>();

    private final double centerX = Config.WINDOW_WIDTH / 2.0;
    private final double centerY = Config.WINDOW_HEIGHT / 2.0;
    private final double stopLineOffset = Config.STOP_LINE_OFFSET;

    public Intersection(EnumMap<Direction, Lane> inLanes, EnumMap<Direction, Lane> outLanes) {
        this.inLanes = inLanes;
        this.outLanes = outLanes;
    }

    public List<Car> getCarsInside() {
        return carsInside;
    }

    /**
     * The junction is intentionally single-occupancy: any turn combination
     * is safe because a second route cannot enter until the first has left.
     */
    public boolean canAcceptEntry() {
        return carsInside.isEmpty();
    }

    public void transferCars() {
        collectFromInLanes();
        dispatchToOutLanes();
    }

    private void collectFromInLanes() {
        for (Direction dir : Direction.values()) {
            Lane inLane = inLanes.get(dir);
            Iterator<Car> it = inLane.getCars().iterator();

            while (it.hasNext()) {
                Car car = it.next();
                if (canAcceptEntry() && hasLeftInLane(car)) {
                    it.remove();
                    carsInside.add(car);
                }
            }
        }
    }

    private void dispatchToOutLanes() {
        Iterator<Car> it = carsInside.iterator();

        while (it.hasNext()) {
            Car car = it.next();
            if (isReoriented(car)) {
                it.remove();
                outLanes.get(car.getExitDirection()).addCar(car);
            }
        }
    }

    private boolean hasLeftInLane(Car car) {
        return switch (car.getOrigin()) {
            case NORTH -> car.getY() >= centerY - stopLineOffset;
            case SOUTH -> car.getY() <= centerY + stopLineOffset;
            case EAST  -> car.getX() <= centerX + stopLineOffset;
            case WEST  -> car.getX() >= centerX - stopLineOffset;
        };
    }

    private boolean isReoriented(Car car) {
        return switch (car.getExitDirection()) {
            case NORTH -> car.getY() >= centerY + stopLineOffset;
            case SOUTH -> car.getY() <= centerY - stopLineOffset;
            case EAST  -> car.getX() <= centerX - stopLineOffset;
            case WEST  -> car.getX() >= centerX - stopLineOffset;
        };
    }
}
