package traffic;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import model.Direction;

// gives every non-empty direction a turn in a fixed order
public class RoundRobinStrategy implements TrafficControlStrategy {

    @Override
    public Optional<Direction> chooseNext(
            Map<Direction, Integer> waitingCars,
            Direction lastServedDirection
    ) {
        Objects.requireNonNull(waitingCars, "waitingCars cannot be null");
        Objects.requireNonNull(lastServedDirection, "lastServedDirection cannot be null");

        Direction[] directions = Direction.values();
        int lastIndex = lastServedDirection.ordinal();

        for (int offset = 1; offset <= directions.length; offset++) {
            Direction candidate = directions[(lastIndex + offset) % directions.length];

            if (waitingCars.getOrDefault(candidate, 0) > 0) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }
}
