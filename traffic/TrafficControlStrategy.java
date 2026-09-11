package traffic;

import java.util.Map;
import java.util.Optional;
import model.Direction;

// Chooses which incoming direction should receive the next green light.
 
public interface TrafficControlStrategy {

    Optional<Direction> chooseNext(
            Map<Direction, Integer> waitingCars,
            Direction lastServedDirection
    );
    
}
