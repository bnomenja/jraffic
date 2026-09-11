package traffic;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import model.Direction;

// Chooses the non-empty direction that has the most waiting cars.
public class CongestionStrategy implements TrafficControlStrategy {

    @Override
    public Optional<Direction> chooseNext(
            Map<Direction, Integer> waitingCars,
            Direction lastServedDirection
    ) {
        Objects.requireNonNull(waitingCars, "waitingCars cannot be null");
        Objects.requireNonNull(lastServedDirection, "lastServedDirection cannot be null");

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

        return Optional.ofNullable(busiestDirection);
    }
}
