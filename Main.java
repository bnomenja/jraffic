import javafx.application.Application;
import javafx.stage.Stage;

import render.SimulationRenderer;
import render.AnimationLoop;
import simulation.Simulation;
import input.KeyPressedHandler;
import config.Config;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Simulation simulation = new Simulation();
        SimulationRenderer renderer =  new SimulationRenderer(simulation);
        KeyPressedHandler handler = new KeyPressedHandler(simulation);

        handler.attachTo(renderer.getScene());

        AnimationLoop animation = new AnimationLoop(simulation, renderer);

        stage.setTitle(Config.WINDOW_TITLE);
        stage.setScene(renderer.getScene());
        stage.setResizable(Config.RESIZABLE);
        stage.show();

        animation.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
