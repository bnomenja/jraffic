package render;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import config.Config;

public class RoadRenderer {
    private static final Color LANE_DIVIDER_COLOR = Color.GOLD;
    private static final Color ROAD_EDGE_COLOR = Color.WHITESMOKE;

    public static void draw(Pane root){
        double rw = Config.ROAD_WIDTH;
        double ww = Config.WINDOW_WIDTH;
        double wh = Config.WINDOW_HEIGHT;
        Color lineColor = Config.LINE_COLOR;
        Color roadColor = Config.ROAD_COLOR;

        drawBackground(root, ww, wh);

        drawRectangle(root, 0, wh / 2.0 - rw, ww / 2.0, rw, roadColor);
        drawRectangle(root, 0, wh / 2.0, ww / 2.0, rw, roadColor);

        drawRectangle(root, ww / 2.0, wh / 2.0 - rw, ww / 2.0, rw, roadColor);
        drawRectangle(root, ww / 2.0, wh / 2.0, ww / 2.0, rw, roadColor);

        drawRectangle(root, ww / 2.0 - rw, 0, rw, wh / 2.0, roadColor);
        drawRectangle(root, ww / 2.0, 0, rw, wh / 2.0, roadColor);

        drawRectangle(root, ww / 2.0 - rw, wh / 2.0, rw, wh / 2.0, roadColor);
        drawRectangle(root, ww / 2.0, wh / 2.0, rw, wh / 2.0, roadColor);

        drawLine(root, 0, wh / 2.0 - rw, ww, wh / 2.0 - rw, ROAD_EDGE_COLOR);
        drawLine(root, 0, wh / 2.0 + rw, ww, wh / 2.0 + rw, ROAD_EDGE_COLOR);
        drawLine(root, ww / 2.0 - rw, 0, ww / 2.0 - rw, wh, ROAD_EDGE_COLOR);
        drawLine(root, ww / 2.0 + rw, 0, ww / 2.0 + rw, wh, ROAD_EDGE_COLOR);

        drawLine(root, 0, wh / 2.0, ww, wh / 2.0, lineColor);
        drawLine(root, ww / 2.0, 0, ww / 2.0, wh, lineColor);

        drawDashedLine(root, 0, wh / 2.0 - rw / 2.0, ww, wh / 2.0 - rw / 2.0, LANE_DIVIDER_COLOR);
        drawDashedLine(root, 0, wh / 2.0 + rw / 2.0, ww, wh / 2.0 + rw / 2.0, LANE_DIVIDER_COLOR);
        drawDashedLine(root, ww / 2.0 - rw / 2.0, 0, ww / 2.0 - rw / 2.0, wh, LANE_DIVIDER_COLOR);
        drawDashedLine(root, ww / 2.0 + rw / 2.0, 0, ww / 2.0 + rw / 2.0, wh, LANE_DIVIDER_COLOR);

        drawRectangle(root, ww / 2.0 - rw, wh / 2.0 - rw, 2 * rw, 2 * rw, roadColor);
    }

    private static void drawBackground(Pane root, double ww, double wh) {
        Image img = new Image(new File("assets/background.png").toURI().toString());

        ImageView bg = new ImageView(img);
        bg.setX(0);
        bg.setY(0);
        bg.setFitWidth(ww);
        bg.setFitHeight(wh);
        bg.setPreserveRatio(false);
        bg.setSmooth(true);

        root.getChildren().add(bg);
    }

    private static void drawLine(Pane root, double x1, double y1, double x2, double y2, Color color) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(color);
        root.getChildren().add(l);
    }

    private static void drawDashedLine(Pane root, double x1, double y1, double x2, double y2, Color color) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(color);
        l.getStrokeDashArray().addAll(10.0, 10.0);
        root.getChildren().add(l);
    }

    private static void drawRectangle(Pane root, double x, double y, double w, double h, Color color) {
        Rectangle rect = new Rectangle(x, y, w, h);
        rect.setFill(color);
        root.getChildren().add(rect);
    }
}