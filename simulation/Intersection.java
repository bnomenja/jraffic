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

    /** Timestamp (elapsedSeconds) at which each direction last admitted a car. */
    private final Map<Direction, Double> lastEntrySeconds = new EnumMap<>(Direction.class);

    /**
     * Minimum delay between two cars of the same direction entering the
     * intersection, so that they keep the usual CAR_LENGTH + SAFETY_GAP
     * clearance even though nothing clamps their speed once inside.
     * Cars move at a constant speed, so a time gap at entry is enough to
     * guarantee the equivalent distance gap for as long as they're inside.
     */
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

    /**
     * A direction may send another car in as long as no *other* direction
     * is currently inside (that case is already excluded by the light
     * controller, which only starts a green phase once the intersection is
     * fully empty) and enough time has passed since that direction's last
     * admitted car to preserve the usual same-lane safety gap.
     */
    public boolean canAcceptEntry(Direction dir, double nowSeconds) {
        if (!isOnlyOccupiedBy(dir)) {
            return false;
        }

        Double lastEntry = lastEntrySeconds.get(dir);
        return lastEntry == null || (nowSeconds - lastEntry) >= MIN_ENTRY_GAP_SECONDS;
    }

    private boolean isOnlyOccupiedBy(Direction dir) {
        for (Car car : carsInside) {
            if (car.getOrigin() != dir) {
                return false;
            }
        }
        return true;
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
                if (hasLeftInLane(car) && canAcceptEntry(dir, nowSeconds)) {
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