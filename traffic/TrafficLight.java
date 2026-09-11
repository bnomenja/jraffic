package traffic;

import model.Direction;

// class responsibilty (store the state of one traffic light) .
public class TrafficLight {

    private final Direction direction;

    private LightColor color;

    // constructor create light
    public TrafficLight(Direction direction, LightColor initialColor) {
        this.direction = direction;
        this.color = initialColor;
    }

    public Direction getDirection() {
        return direction;
    }

    public LightColor getColor(){
        return this.color;
    }

    public boolean isGreen() {
        return color == LightColor.GREEN;
    }

    public void setColor(LightColor color) {
        this.color = color;
    }
}
