package traffic;

import model.Direction;
import model.Lane;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class LightController {

    private static final double GREEN_DURATION_SECONDS = 2.0;
    private static final double ALL_RED_DURATION_SECONDS = 0.5;

    private final Map<Direction, Lane> lanes;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private final TrafficControlStrategy strategy;

    private LightPhase phase = LightPhase.ALL_RED;
    private double phaseStartedSeconds = 0.0;
    private Direction lastServedDirection = Direction.WEST;

    public LightController(Map<Direction, Lane> lanes) {
        this(lanes, new RoundRobinStrategy());
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

    public void update(double elapsedSeconds, boolean intersectionEmpty) {
        double phaseDuration = elapsedSeconds - phaseStartedSeconds;

        if (phase == LightPhase.GREEN) {
            if (phaseDuration >= GREEN_DURATION_SECONDS) {
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
        for (TrafficLight light : lights.values()) {
            light.setColor(color);
        }
    }

    private void startGreenPhase(Direction direction, double elapsedSeconds) {
        lights.get(direction).setColor(LightColor.GREEN);
        lastServedDirection = direction;
        phase = LightPhase.GREEN;
        phaseStartedSeconds = elapsedSeconds;
    }

    private void startAllRedPhase(double elapsedSeconds) {
        setAllLights(LightColor.RED);
        phase = LightPhase.ALL_RED;
        phaseStartedSeconds = elapsedSeconds;
    }
}
