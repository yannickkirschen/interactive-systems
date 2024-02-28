package sh.yannick.dhbw.interactive.physics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class CircularMass extends Mass {
    private final double radius;
    private final Color color;

    public CircularMass(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public double getWidth() {
        return radius * 2;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }
}
