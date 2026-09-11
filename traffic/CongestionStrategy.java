package traffic;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import model.Direction;

// Chooses the non-empty direction that has the most waiting cars.
public class CongestionStrategy implements TrafficControlStrategy {

    private static final int MAX_WAITING_TURNS = 3;

    private final Map<Direction, Integer> waitingTurns = new EnumMap<>(Direction.class);

    @Override
    public Optional<Direction> chooseNext(
            Map<Direction, Integer> waitingCars,
            Direction lastServedDirection
    ) {

        Objects.requireNonNull(waitingCars, "waitingCars cannot be null");

        Objects.requireNonNull(lastServedDirection, "lastServedDirection cannot be null");

        updateWaitingTurns(waitingCars);

        Direction chosenDirection = findLongestWaitingDirection(
                waitingCars,
                lastServedDirection
        );

        if (chosenDirection == null) {
            chosenDirection = findBusiestDirection(
                    waitingCars,
                    lastServedDirection
            );
        }

        if (chosenDirection != null) {
            waitingTurns.put(chosenDirection, 0);
        }

        return Optional.ofNullable(chosenDirection);

    }

    private void updateWaitingTurns(Map<Direction, Integer> waitingCars) {

        for (Direction direction : Direction.values()) {

            int carCount = waitingCars.getOrDefault(direction, 0);

            if (carCount > 0) {
                waitingTurns.put(direction, waitingTurns.getOrDefault(direction, 0) + 1);
            } else {
                waitingTurns.put(direction, 0);
            }

        }

    }

    private Direction findLongestWaitingDirection(
            Map<Direction, Integer> waitingCars,
            Direction lastServedDirection
    ) {

        Direction[] directions = Direction.values();

        int lastIndex = lastServedDirection.ordinal();

        Direction longestWaitingDirection = null;

        int longestWait = MAX_WAITING_TURNS - 1;

        for (int offset = 1; offset <= directions.length; offset++) {
            Direction candidate = directions[(lastIndex + offset) % directions.length];
            int carCount = waitingCars.getOrDefault(candidate, 0);
            int wait = waitingTurns.getOrDefault(candidate, 0);

            if (carCount > 0 && wait > longestWait) {
                longestWaitingDirection = candidate;
                longestWait = wait;
            }
        }

        return longestWaitingDirection;
    }

    private Direction findBusiestDirection(
            Map<Direction, Integer> waitingCars,
            Direction lastServedDirection
    ) {

        Direction[] directions = Direction.values();
        int lastIndex = lastServedDirection.ordinal();
        Direction busiestDirection = null;
        int highestCarCount = 0;
            
        for (int offset = 1; offset <= directions.length; offset++) {
            Direction candidate = directions[(lastIndex + offset) % directions.length];
            int carCount = waitingCars.getOrDefault(candidate, 0);

            if (carCount > highestCarCount) {
                busiestDirection = candidate;
                highestCarCount = carCount;
            }
        }

        return busiestDirection;
    }
}
