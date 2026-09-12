package config;

import javafx.scene.paint.Color;

public final class Config {

    // --- Window ---
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 800;
    public static final String WINDOW_TITLE = "Jraffic";
    public static final boolean RESIZABLE = false;

    // --- Road ---
    public static final double ROAD_WIDTH = 50.0;
    public static final Color BACKGROUND_COLOR = Color.FORESTGREEN;
    public static final Color ROAD_COLOR = Color.rgb(45, 45, 45);
    public static final Color LINE_COLOR = Color.WHITE;

    /** Distance from the junction centre to each incoming stop line. */
    public static final double STOP_LINE_OFFSET = ROAD_WIDTH / 2.0;

    // --- Junction control ---
    public static final double MIN_GREEN_SECONDS = 1.5;
    public static final double MAX_GREEN_SECONDS = 5.0;

    // --- Traffic light ---
    public static final Color LIGHT_COLOR = Color.DARKGRAY;
    public static final double LIGHT_RADIUS_RATIO = 0.25; // radius = PAD * ratio
    public static final double LIGHT_OFFSET_RATIO = 0.5;  // offset = PAD * ratio

    // --- Car ---
    public static final long SPAWN_COOLDOWN_MS = 450; // int millisecondes
    public static final double CAR_SPEED = 95;
    public static final double CAR_WIDTH = 16;   
    public static final double CAR_LENGTH = 16; 
    /** Empty space kept between the ends of consecutive cars in one lane. */
    public static final double SAFETY_GAP = 10;

}
