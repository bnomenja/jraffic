package render;

import javafx.animation.AnimationTimer;
import simulation.Simulation;


public class AnimationLoop extends AnimationTimer {

    private final Simulation simulation;
    private final SimulationRenderer renderer;

    public AnimationLoop(Simulation simulation, SimulationRenderer renderer) {
        this.simulation = simulation;
        this.renderer = renderer;
    }

    @Override
    public void handle(long now) {
        simulation.update(now);
        renderer.render();
    }
}
