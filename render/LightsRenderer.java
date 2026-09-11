package render;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import simulation.Simulation;
import config.Config;
import model.Direction;
import traffic.*;

import java.util.EnumMap;
import java.util.Map;

public class LightsRenderer {
    private final Simulation manager;
    private final Pane root;
    private final Map<Direction, Circle> lights = new EnumMap<>(Direction.class);

    public LightsRenderer(Pane root, Simulation manager){
        this.manager = manager;
        this.root = root;
    }

    public void init(){
        double radius = Config.ROAD_WIDTH * Config.LIGHT_RADIUS_RATIO;

        for (Direction dir : Direction.values()) {
            double[] pos = getLightPosition(dir);

            Circle c = new Circle(pos[0], pos[1], radius, Config.LIGHT_COLOR);
            lights.put(dir, c);
            root.getChildren().add(c);
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

        double[] coords = {x, y}; 
        return coords;
    }

    public void update(){
        LightController lc = manager.getLightController();

        for (Direction dir : Direction.values()) {
            LightColor color = lc.getColor(dir);

            if (color == LightColor.GREEN){
                lights.get(dir).setFill(Color.GREEN);
            }else{
                lights.get(dir).setFill(Color.RED);
            }
        }
    }
    
}
