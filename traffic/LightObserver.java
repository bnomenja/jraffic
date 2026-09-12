package traffic;

import model.Direction;

@FunctionalInterface
public interface LightObserver {

    void onLightChanged(Direction direction, LightColor color);
    
}
