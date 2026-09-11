package model;

import java.util.List;
import java.util.ArrayList;

public class Lane{
    private Direction dir;
    private List<Car> cars;

    public Lane(Direction dir){
        this.dir = dir;
        this.cars = new ArrayList<>();
    }

    public boolean isFull(){
        return false;
    }

    public List<Car> getCars(){
        return this.cars;
    }

    public void addCar(Car c){
        cars.add(c);
    }
}