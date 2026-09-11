package traffic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import model.Direction;
import model.Lane;

public class LightController {

    private static final double NORMAL_GREEN_DURATION_SECONDS = 2.0;
    private static final double FULL_LANE_GREEN_DURATION_SECONDS = 4.0;
    private static final double ALL_RED_DURATION_SECONDS = 0.5;

    private final Map<Direction, Lane> lanes;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private final List<LightObserver> observers = new ArrayList<>();
    private final TrafficControlStrategy strategy;

    private LightPhase phase = LightPhase.ALL_RED;
    private double phaseStartedSeconds = 0.0;
    private Direction lastServedDirection = Direction.WEST;
    private Direction greenDirection;
    private double currentGreenDurationSeconds = NORMAL_GREEN_DURATION_SECONDS;

    public LightController(Map<Direction, Lane> lanes) {
        this(lanes, new CongestionStrategy());
    }

    public LightController(
            Map<Direction, Lane> lanes,
            TrafficControlStrategy strategy
    ) {
        this.lanes = Objects.requireNonNull(lanes, "lanes cannot be null");
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");

        for (Direction direction : Direction.values()) {
            Objects.requireNonNull(
                    lanes.get(direction),
                    "Missing incoming lane for " + direction
            );
            lights.put(direction, new TrafficLight(direction, LightColor.RED));
        }
    }

    public LightColor getColor(Direction direction) {
        return lights.get(direction).getColor();
    }

    public boolean isGreen(Direction direction) {
        return lights.get(direction).isGreen();
    }

    public LightPhase getPhase() {
        return phase;
    }

    public void addObserver(LightObserver observer) {
        observers.add(Objects.requireNonNull(observer, "observer cannot be null"));
    }

    public void removeObserver(LightObserver observer) {
        observers.remove(observer);
    }

    public void update(double elapsedSeconds, boolean intersectionEmpty) {
        double phaseDuration = elapsedSeconds - phaseStartedSeconds;

        if (phase == LightPhase.GREEN) {
            extendGreenTimeIfLaneIsFull();

            if (phaseDuration >= currentGreenDurationSeconds) {
                startAllRedPhase(elapsedSeconds);
            }

            return;
        }

        if (phaseDuration < ALL_RED_DURATION_SECONDS || !intersectionEmpty) {
            return;
        }

        Map<Direction, Integer> waitingCars = countWaitingCars();

        strategy.chooseNext(waitingCars, lastServedDirection)
                .ifPresent(direction -> startGreenPhase(direction, elapsedSeconds));
    }

    private Map<Direction, Integer> countWaitingCars() {
        Map<Direction, Integer> waitingCars = new EnumMap<>(Direction.class);

        for (Direction direction : Direction.values()) {
            waitingCars.put(direction, lanes.get(direction).getCars().size());
        }

        return waitingCars;
    }

    private void setAllLights(LightColor color) {
        for (Direction direction : Direction.values()) {
            setLightColor(direction, color);
        }
    }

    // include observer
    private void setLightColor(Direction direction, LightColor color) {
        TrafficLight light = lights.get(direction);

        if (light.getColor() == color) {
            return;
        }

        light.setColor(color);

       // observer
         notifyObservers(direction, color);
    }

    private void notifyObservers(Direction direction, LightColor color) {

        for (LightObserver observer : List.copyOf(observers)) {
            observer.onLightChanged(direction, color);
        }


    }

    private void startGreenPhase(Direction direction, double elapsedSeconds) {
        setLightColor(direction, LightColor.GREEN);
        lastServedDirection = direction;
        greenDirection = direction;
        currentGreenDurationSeconds = lanes.get(direction).isFull()
                ? FULL_LANE_GREEN_DURATION_SECONDS
                : NORMAL_GREEN_DURATION_SECONDS;
        phase = LightPhase.GREEN;
        phaseStartedSeconds = elapsedSeconds;
    }

    private void extendGreenTimeIfLaneIsFull() {
        if (lanes.get(greenDirection).isFull()) {
            currentGreenDurationSeconds = FULL_LANE_GREEN_DURATION_SECONDS;
        }
    }

    private void startAllRedPhase(double elapsedSeconds) {
        setAllLights(LightColor.RED);
        greenDirection = null;
        currentGreenDurationSeconds = NORMAL_GREEN_DURATION_SECONDS;
        phase = LightPhase.ALL_RED;
        phaseStartedSeconds = elapsedSeconds;
    }
}
