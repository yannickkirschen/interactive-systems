package sh.yannick.dhbw.interactive.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.*;

@ToString
@EqualsAndHashCode
public class Window {
    @Getter
    private final Color backgroundColor;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter(AccessLevel.MODULE)
    private int zIndex;

    @Setter(AccessLevel.MODULE)
    private Point position;

    @Setter(AccessLevel.MODULE)
    private Size size;

    public Window(Point position, Size size, Color backgroundColor) {
        this.zIndex = 0;
        this.position = position;
        this.size = size;
        this.backgroundColor = backgroundColor;
    }

    public void move(int relX, int relY) {
        position = new Point(position.x() + relX, position.y() + relY);
    }

    public void draw(GraphicsContext gc) {
        // Shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(position.x() + 5, position.y() + 5, size.width(), size.height());

        // Window
        gc.setFill(backgroundColor);
        gc.fillRect(position.x(), position.y(), size.width(), size.height());

        // Border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(position.x(), position.y(), size.width(), size.height());
    }

    public boolean isInWindow(double x, double y) {
        return x >= position.x()
            && x <= position.x() + size.width()
            && y >= position.y()
            && y <= position.y() + size.height();
    }
}
