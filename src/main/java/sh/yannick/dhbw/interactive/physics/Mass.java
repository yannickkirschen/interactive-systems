package sh.yannick.dhbw.interactive.physics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Mass {
    protected Color color;

    @Setter
    protected double x;

    @Setter
    protected double y;
    
    @Setter
    protected double velocity = 0;

    public abstract double getWidth();

    public abstract void draw(GraphicsContext gc);
}
