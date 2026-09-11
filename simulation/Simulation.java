package simulation;

import config.Config;
import model.*;
import traffic.*;

import java.util.Map;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;

public class Simulation{
    private EnumMap<Direction, Lane> inLanes = new EnumMap<>(Direction.class);
    private EnumMap<Direction, Lane> outLanes = new EnumMap<>(Direction.class);
    private CarSpawner spawner;
    private LightController scheduler;
    private Intersection intersection;

    private double timeOfLastFrame = System.nanoTime() / 1_000_000_000.0;
    private double elapsedSeconds = 0.0;

    public Simulation(){
        for (Direction dir: Direction.values()){
            inLanes.put(dir, new Lane(dir));
            outLanes.put(dir, new Lane(dir));
        }

        spawner = new CarSpawner(inLanes);
        scheduler = new LightController(inLanes);
        intersection = new Intersection(inLanes, outLanes);
    }

    public EnumMap<Direction, Lane> getInLanes(){
        return this.inLanes;
    }

    public EnumMap<Direction, Lane> getOutLanes(){
        return this.outLanes;
    }

    public CarSpawner geCarSpawner(){
        return this.spawner;
    }

    public LightController getLightController(){
        return this.scheduler;
    }

    public Intersection getIntersection(){
        return this.intersection;
    }

    public void update(long now){
        double nowSeconds = now / 1_000_000_000.0;
        double dt = nowSeconds - timeOfLastFrame;
        elapsedSeconds += dt;

        scheduler.update(elapsedSeconds);
        updateCars(dt);
        timeOfLastFrame = nowSeconds;
    }

    private void updateCars(double dt){
        moveCars(dt);
        intersection.transferCars();
        removeExitedCars();
    }


    private void moveCars(double dt) {
        for (Direction dir : Direction.values()) {
            syncLane(inLanes.get(dir), dt);
            syncLane(outLanes.get(dir), dt);
        }

        for (Car c : intersection.getCarsInside()) {
            c.move(dt);
        }
    }

    private void syncLane(Lane lane, double dt) {
        for (Car c : lane.getCars()) {
            c.move(dt);
        }
    }


    private void removeExitedCars() {
        for (Direction dir : Direction.values()) {
            outLanes.get(dir).getCars().removeIf(this::hasLeftField);
        }
    }

    private boolean hasLeftField(Car c) {
        double x = c.getX();
        double y = c.getY();
        return x < 0 || x > Config.WINDOW_WIDTH || y < 0 || y > Config.WINDOW_HEIGHT;
    }

}