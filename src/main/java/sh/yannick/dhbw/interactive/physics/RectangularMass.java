package sh.yannick.dhbw.interactive.physics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangularMass extends Mass {
    private final double width;
    private final double height;

    public RectangularMass(double x, double y, double width, double height, Color color) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }
}
