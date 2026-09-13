package simulation;

import config.Config;
import model.Car;
import model.Direction;
import model.Lane;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Intersection {

    private final EnumMap<Direction, Lane> inLanes;
    private final EnumMap<Direction, Lane> outLanes;

    private final List<Car> carsInside = new ArrayList<>();

    private final Map<Direction, Double> lastEntrySeconds = new EnumMap<>(Direction.class);

    private static final double MIN_ENTRY_GAP_SECONDS =
            (Config.CAR_LENGTH + Config.SAFETY_GAP) / Config.CAR_SPEED;

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

    public void transferCars(double nowSeconds) {
        collectFromInLanes(nowSeconds);
        dispatchToOutLanes();
    }

    private void collectFromInLanes(double nowSeconds) {
        for (Direction dir : Direction.values()) {
            Lane inLane = inLanes.get(dir);
            Iterator<Car> it = inLane.getCars().iterator();

            while (it.hasNext()) {
                Car car = it.next();
                if (hasLeftInLane(car)) {
                    it.remove();
                    carsInside.add(car);
                    lastEntrySeconds.put(dir, nowSeconds);
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
            case WEST  -> car.getX() >= centerX + stopLineOffset;
        };
    }
}