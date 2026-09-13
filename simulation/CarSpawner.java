package simulation;

import config.Config;
import model.Direction;
import model.Lane;
import model.Turn;
import model.Car;

import java.util.Map;
import java.util.Random;

public class CarSpawner {

    private final Map<Direction, Lane> lanes;
    private final Random random = new Random();

    public CarSpawner(Map<Direction, Lane> lanes) {
        this.lanes = lanes;
    }

    public void trySpawn(Direction origin) {
        Lane lane = lanes.get(origin);

        if (lane.isFull()) {
            return;
        }

        if (!lane.canSpawn()) {
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
    }
}