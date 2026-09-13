package input;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import java.util.Random;

import simulation.Simulation;
import model.Direction;

public class KeyPressedHandler {
    private Simulation manager;
    private Random random = new Random();

    public KeyPressedHandler(Simulation manager) {
        this.manager = manager;
    }

    public void handle(KeyEvent e){
        switch(e.getCode()){
            case LEFT -> manager.geCarSpawner().trySpawn(Direction.EAST);
            case RIGHT -> manager.geCarSpawner().trySpawn(Direction.WEST);
            case UP -> manager.geCarSpawner().trySpawn(Direction.SOUTH);
            case DOWN -> manager.geCarSpawner().trySpawn(Direction.NORTH);
            case R -> manager.geCarSpawner().trySpawn(Direction.random(random));
            case C -> manager.reset();
            case ESCAPE -> System.exit(0);
            default -> {}
        }
    }

    public void attachTo(Scene scene){
        scene.setOnKeyPressed(this::handle);
    }
}