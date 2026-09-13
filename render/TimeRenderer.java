package render;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

import simulation.Simulation;

public class TimeRenderer{
    private final Simulation manager;
    private final Label elapsedLabel = new Label("Simulation duration: 0.0 s");

    public TimeRenderer(Pane root, Simulation manager){
        elapsedLabel.setLayoutX(10);
        elapsedLabel.setLayoutY(10);
        elapsedLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        this.manager = manager;
        root.getChildren().add(elapsedLabel);
    }

    public void update(){
        elapsedLabel.setText(String.format("Simulation duration: %.1f s", manager.getElapsedSeconds()));
    }
}