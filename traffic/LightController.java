package traffic;

import config.Config;
import model.Direction;
import model.Lane;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class LightController {

    private static final double CHANGE_INTERVAL_SECONDS = 2.0;

    private final Map<Direction, Lane> lanes;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private final Random random = new Random();

    private double lastChangeSeconds = 0.0;

    public LightController(Map<Direction, Lane> lanes) {
        this.lanes = lanes;

        for (Direction d : Direction.values()) {
            lights.put(d, new TrafficLight(d, LightColor.RED));
        }
    }

    public LightColor getColor(Direction direction) {
        return lights.get(direction).getColor();
    }

    public void update(double elapsedSeconds) {
        if (elapsedSeconds - lastChangeSeconds < CHANGE_INTERVAL_SECONDS) {
            return;
        }

        for (TrafficLight light : lights.values()) {
            LightColor randomColor = random.nextBoolean() ? LightColor.RED : LightColor.GREEN;
            light.setColor(randomColor);
        }

        lastChangeSeconds = elapsedSeconds;
    }
}