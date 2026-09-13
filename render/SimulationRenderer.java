package render;

import  javafx.scene.layout.Pane;
import  javafx.scene.Scene;

import simulation.Simulation;
import config.Config;

public class SimulationRenderer {
    private final Simulation manager;
    private final LightsRenderer lights;
    private final CarRenderer cars;

    private final Pane root = new Pane();
    private final Scene scene;
    private final TimeRenderer timer;

    public SimulationRenderer(Simulation manager){
        this.scene = new Scene(root, 
         Config.WINDOW_WIDTH,
         Config.WINDOW_HEIGHT,
         Config.BACKGROUND_COLOR
        );

        this.manager = manager;

        this.lights = new LightsRenderer(root, manager);
        this.cars = new CarRenderer(root, manager);

        RoadRenderer.draw(root);
        lights.init();
        
        this.timer = new TimeRenderer(root, manager);
    }

    public Scene getScene(){
        return this.scene;
    }

    
    public void render(){
        lights.update();
        cars.update();
        timer.update();
    }
}
