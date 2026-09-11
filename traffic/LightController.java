package traffic;

import model.Direction;
import model.Lane;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class LightController {

    private static final double CHANGE_INTERVAL_SECONDS = 2.0;

    private final Map<Direction, Lane> lanes;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private final TrafficControlStrategy strategy;

    private double lastChangeSeconds = 0.0;
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

    public void update(double elapsedSeconds) {
        if (elapsedSeconds - lastChangeSeconds < CHANGE_INTERVAL_SECONDS) {
            return;
        }

        Map<Direction, Integer> waitingCars = countWaitingCars();

        setAllLights(LightColor.RED);
        strategy.chooseNext(waitingCars, lastServedDirection)
                .ifPresent(this::giveGreenLightTo);

        lastChangeSeconds = elapsedSeconds;
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

    private void giveGreenLightTo(Direction direction) {
        lights.get(direction).setColor(LightColor.GREEN);
        lastServedDirection = direction;
    }
}
