package render;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import config.Config;

public class RoadRenderer {

    public static void draw(Pane root){
        double rw = Config.ROAD_WIDTH;
        double ww = Config.WINDOW_WIDTH;
        double wh = Config.WINDOW_HEIGHT;
        Color lineColor = Config.LINE_COLOR;
        Color roadColor = Config.ROAD_COLOR;

        drawRectangle(root, 0, wh / 2.0 - rw, ww / 2.0, rw, roadColor);
        drawRectangle(root, 0, wh / 2.0, ww / 2.0, rw, roadColor);

        drawRectangle(root, ww / 2.0, wh / 2.0 - rw, ww / 2.0, rw, roadColor);
        drawRectangle(root, ww / 2.0, wh / 2.0, ww / 2.0, rw, roadColor);

        drawRectangle(root, ww / 2.0 - rw, 0, rw, wh / 2.0, roadColor);
        drawRectangle(root, ww / 2.0, 0, rw, wh / 2.0, roadColor);

        drawRectangle(root, ww / 2.0 - rw, wh / 2.0, rw, wh / 2.0, roadColor);
        drawRectangle(root, ww / 2.0, wh / 2.0, rw, wh / 2.0, roadColor);

        drawLine(root, 0, wh / 2.0, ww, wh / 2.0, lineColor);
        drawLine(root, ww / 2.0, 0, ww / 2.0, wh, lineColor);

        drawRectangle(root, ww / 2.0 - rw, wh / 2.0 - rw, 2 * rw, 2 * rw, roadColor);
        // Draw these last so the central junction surface cannot hide them.
        drawStopLines(root, ww, wh, rw, lineColor);
    }

    private static void drawStopLines(Pane root, double ww, double wh, double rw, Color color) {
        double centerX = ww / 2.0;
        double centerY = wh / 2.0;
        double offset = Config.STOP_LINE_OFFSET;

        drawLine(root, centerX - rw, centerY - offset, centerX, centerY - offset, color);
        drawLine(root, centerX, centerY + offset, centerX + rw, centerY + offset, color);
        drawLine(root, centerX + offset, centerY, centerX + offset, centerY + rw, color);
        drawLine(root, centerX - offset, centerY - rw, centerX - offset, centerY, color);
    }

    private static void drawLine(Pane root, double x1, double y1, double x2, double y2, Color color) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(color);
        root.getChildren().add(l);
    }

    private static void drawRectangle(Pane root, double x, double y, double w, double h, Color color) {
        Rectangle rect = new Rectangle(x, y, w, h);
        rect.setFill(color);
        root.getChildren().add(rect);
    }
}
