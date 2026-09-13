package render;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import simulation.Simulation;
import config.Config;
import model.Direction;
import traffic.*;

import java.util.EnumMap;
import java.util.Map;

public class LightsRenderer {
    private static final Color COLOR_OFF = Color.web("#4A4A4E");
    private static final Color COLOR_RED = Color.web("#FF3B30");
    private static final Color COLOR_GREEN = Color.web("#34C759");
    private static final Color HOUSING_COLOR = Color.web("#3A3D45");
    private static final Color HOUSING_STROKE = Color.web("#1A1A1C");

    private static final double SIZE_SCALE = 0.5;

    private final Simulation manager;
    private final Pane root;
    private final Map<Direction, Circle> redLights = new EnumMap<>(Direction.class);
    private final Map<Direction, Circle> greenLights = new EnumMap<>(Direction.class);

    public LightsRenderer(Pane root, Simulation manager){
        this.manager = manager;
        this.root = root;
    }

    public void init(){
        double radius = Config.ROAD_WIDTH * Config.LIGHT_RADIUS_RATIO * SIZE_SCALE;
        double gap = radius * 0.6;      
        double padding = radius * 0.4;

        double housingWidth = radius * 2 + padding * 2;
        double housingHeight = radius * 4 + gap + padding * 2;

        for (Direction dir : Direction.values()) {
            double[] pos = getLightPosition(dir);
            double centerX = pos[0];
            double centerY = pos[1];

            Rectangle housing = new Rectangle(
                    centerX - housingWidth / 2,
                    centerY - housingHeight / 2,
                    housingWidth,
                    housingHeight
            );
            housing.setArcWidth(housingWidth * 0.4);
            housing.setArcHeight(housingWidth * 0.4);
            housing.setFill(HOUSING_COLOR);
            housing.setStroke(HOUSING_STROKE);
            housing.setStrokeWidth(1);

            double redCy = centerY - housingHeight / 2 + padding + radius;
            Circle red = new Circle(centerX, redCy, radius, COLOR_OFF);

            double greenCy = centerY + housingHeight / 2 - padding - radius;
            Circle green = new Circle(centerX, greenCy, radius, COLOR_OFF);

            root.getChildren().addAll(housing, red, green);

            redLights.put(dir, red);
            greenLights.put(dir, green);
        }
    }

    private double[] getLightPosition(Direction dir){
        double rw = Config.ROAD_WIDTH;
        double offset = rw * Config.LIGHT_OFFSET_RATIO;
        int ww = Config.WINDOW_WIDTH;
        int wh = Config.WINDOW_HEIGHT;

        double x, y;
        switch (dir) {
            case NORTH -> { x = ww / 2.0 - rw - offset; y = wh / 2.0 - rw - offset; }
            case SOUTH -> { x = ww / 2.0 + rw + offset; y = wh / 2.0 + rw + offset; }
            case EAST  -> { x = ww / 2.0 + rw + offset; y = wh / 2.0 - rw - offset; }
            case WEST  -> { x = ww / 2.0 - rw - offset; y = wh / 2.0 + rw + offset; }
            default    -> { x = ww / 2.0; y = wh / 2.0; }
        }

        return new double[]{x, y};
    }

    public void update(){
        LightController lc = manager.getLightController();

        for (Direction dir : Direction.values()) {
            LightColor color = lc.getColor(dir);

            if (color == LightColor.GREEN){
                redLights.get(dir).setFill(COLOR_OFF);
                greenLights.get(dir).setFill(COLOR_GREEN);
            } else {
                redLights.get(dir).setFill(COLOR_RED);
                greenLights.get(dir).setFill(COLOR_OFF);
            }
        }
    }
}