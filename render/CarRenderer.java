package render;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.EnumMap;

import simulation.Simulation;
import model.*;
import config.Config;

public class CarRenderer {
    private final Simulation manager;
    private final Pane root;
    private final HashMap<Integer, Rectangle> cars;

    public CarRenderer(Pane root, Simulation manager){
        this.manager = manager;
        this.root = root;
        this.cars = new HashMap<>();
    }

    public void update(){
        EnumMap<Direction, Lane> in = manager.getInLanes();
        EnumMap<Direction, Lane> out = manager.getOutLanes();

        Set<Integer> seenIds = new HashSet<>();

        for (Direction dir : Direction.values()) {
            syncLane(in.get(dir), seenIds);
            syncLane(out.get(dir), seenIds);
        }

        syncCars(manager.getIntersection().getCarsInside(), seenIds);

        removeMissingCars(seenIds);
    }

    private void syncLane(Lane lane, Set<Integer> seenIds) {
        syncCars(lane.getCars(), seenIds);
    }

    private void syncCars(java.util.List<Car> carsToSync, Set<Integer> seenIds) {
        for (Car c : carsToSync) {
            seenIds.add(c.getId());

            Rectangle rect = cars.get(c.getId());
            if (rect == null) {
                rect = createCarShape(c);
                cars.put(c.getId(), rect);
                root.getChildren().add(rect);
            }

            rect.setX(c.getX() - rect.getWidth() / 2.0);
            rect.setY(c.getY() - rect.getHeight() / 2.0);
        }
    }

    private void removeMissingCars(Set<Integer> seenIds) {
        cars.entrySet().removeIf(entry -> {
            if (seenIds.contains(entry.getKey())) {
                return false;
            }
            root.getChildren().remove(entry.getValue());
            return true;
        });
    }

    private Rectangle createCarShape(Car c) {
        boolean vertical = isVertical(c.getOrigin());

        double w = vertical ? Config.CAR_WIDTH : Config.CAR_LENGTH;
        double h = vertical ? Config.CAR_LENGTH : Config.CAR_WIDTH;

        Rectangle rect = new Rectangle(w, h);
        rect.setFill(colorForTurn(c.getTurn()));
        return rect;
    }

    private boolean isVertical(Direction origin) {
        return origin == Direction.NORTH || origin == Direction.SOUTH;
    }

    private Color colorForTurn(Turn turn) {
        return switch (turn) {
            case STRAIGHT -> Color.DODGERBLUE;
            case LEFT     -> Color.ORANGE;
            case RIGHT    -> Color.LIMEGREEN;
        };
    }
}