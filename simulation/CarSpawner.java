package simulation;

import config.Config;
import model.Direction;
import model.Lane;
import model.Turn;
import model.Car;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class CarSpawner {

    private final Map<Direction, Lane> lanes;
    private final Random random = new Random();
    private final Map<Direction, Long> lastSpawnMillis = new EnumMap<>(Direction.class);

    public CarSpawner(Map<Direction, Lane> lanes) {
        this.lanes = lanes;
    }

    public void trySpawn(Direction origin) {
        Long now = System.currentTimeMillis();
        Long last = lastSpawnMillis.getOrDefault(origin, 0l);

        if (last != 0 && now - last < Config.SPAWN_COOLDOWN_MS) {
            return;
        }

        Lane lane = lanes.get(origin);
        if (lane.isFull()) {
            return;
        }

        Turn turn = Turn.random(random);
        Direction exitDirection = turn.exitDirection(origin);

        Car car = new Car(
                origin,
                turn,
                exitDirection
        );

        lane.addCar(car);
        lastSpawnMillis.put(origin, now);
    }
}
