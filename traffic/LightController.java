package traffic;

import config.Config;
import model.Direction;
import model.Lane;

import java.util.EnumMap;
import java.util.Map;

public class LightController {

    private final Map<Direction, Lane> lanes;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);

    private Direction activeDirection;
    private double greenStartedSeconds = 0.0;

    public LightController(Map<Direction, Lane> lanes) {
        this.lanes = lanes;

        for (Direction d : Direction.values()) {
            lights.put(d, new TrafficLight(d, LightColor.RED));
        }
        setActiveDirection(Direction.NORTH, 0.0);
    }

    public LightColor getColor(Direction direction) {
        return lights.get(direction).getColor();
    }

    /**
     * Keeps exactly one approach green. A phase is never changed while a car
     * occupies the junction, so two conflicting routes cannot be admitted.
     * Once clear, the most congested waiting lane has priority.
     */
    public void update(double elapsedSeconds, boolean intersectionEmpty) {
        double greenAge = elapsedSeconds - greenStartedSeconds;
        if (greenAge < Config.MIN_GREEN_SECONDS || !intersectionEmpty) {
            return;
        }

        Direction candidate = mostCongestedDirection(greenAge >= Config.MAX_GREEN_SECONDS);
        if (candidate == activeDirection || lanes.get(candidate).getCars().isEmpty()) {
            return;
        }

        Lane activeLane = lanes.get(activeDirection);
        boolean activeIsEmpty = activeLane.getCars().isEmpty();
        boolean candidateIsMoreCongested = lanes.get(candidate).getCongestionRatio()
                > activeLane.getCongestionRatio();

        // Keep serving the current approach briefly, but let a more crowded
        // lane pre-empt it and force rotation after the maximum green time.
        if (activeIsEmpty || candidateIsMoreCongested
                || greenAge >= Config.MAX_GREEN_SECONDS) {
            setActiveDirection(candidate, elapsedSeconds);
        }
    }

    private Direction mostCongestedDirection(boolean requireAnotherWaitingLane) {
        Direction best = activeDirection;
        double bestRatio = -1.0;
        int bestQueue = -1;
        boolean foundWaitingLane = false;

        for (Direction direction : Direction.values()) {
            Lane lane = lanes.get(direction);
            if (requireAnotherWaitingLane && direction == activeDirection) {
                continue;
            }
            double ratio = lane.getCongestionRatio();
            int queue = lane.getCars().size();
            if (direction != activeDirection && queue > 0) {
                foundWaitingLane = true;
            }
            if (ratio > bestRatio || (ratio == bestRatio && queue > bestQueue)
                    || (ratio == bestRatio && queue == bestQueue
                    && isEarlierAfterActive(direction, best))) {
                best = direction;
                bestRatio = ratio;
                bestQueue = queue;
            }
        }
        return requireAnotherWaitingLane && !foundWaitingLane ? activeDirection : best;
    }

    private boolean isEarlierAfterActive(Direction candidate, Direction currentBest) {
        int size = Direction.values().length;
        int candidateDistance = Math.floorMod(candidate.ordinal() - activeDirection.ordinal(), size);
        int bestDistance = Math.floorMod(currentBest.ordinal() - activeDirection.ordinal(), size);
        // Treat the active direction as last in a tie, enabling round-robin.
        if (candidateDistance == 0) candidateDistance = size;
        if (bestDistance == 0) bestDistance = size;
        return candidateDistance < bestDistance;
    }

    private void setActiveDirection(Direction direction, double elapsedSeconds) {
        activeDirection = direction;
        greenStartedSeconds = elapsedSeconds;
        for (Direction lightDirection : Direction.values()) {
            lights.get(lightDirection).setColor(lightDirection == direction
                    ? LightColor.GREEN : LightColor.RED);
        }
    }
}
