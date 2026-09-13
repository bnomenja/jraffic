package render;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.EnumMap;
import java.io.File;

import simulation.Simulation;
import model.*;
import config.Config;

public class CarRenderer {
    private final Simulation manager;
    private final Pane root;
    private final HashMap<Integer, ImageView> cars;

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

            ImageView view = cars.get(c.getId());
            if (view == null) {
                view = createCarShape(c);
                cars.put(c.getId(), view);
                root.getChildren().add(view);
            }

            view.setX(c.getX() - view.getFitWidth() / 2.0);
            view.setY(c.getY() - view.getFitHeight() / 2.0);
            view.setRotate(getAngleRotation(c.getCurrentDir()));
        }
    }

    private double getAngleRotation(Direction d){
        return switch (d) {
            case SOUTH -> 0;
            case WEST  -> 90;
            case NORTH -> 180;
            case EAST  -> 270;
        };
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

    private ImageView createCarShape(Car c) {
        double w = Config.CAR_WIDTH;
        double h = Config.CAR_LENGTH;

        Image img = new Image(new File("assets/" + colorForTurn(c.getTurn())).toURI().toString());

        ImageView view = new ImageView(img);
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setPreserveRatio(false); 
        view.setSmooth(true);

        return view;
    }

    private String colorForTurn(Turn turn) {
        return switch (turn) {
            case STRAIGHT -> "green_car.png";
            case LEFT     -> "red_car.png";
            case RIGHT    -> "yellow_car.png";
        };
    }
}