package sh.yannick.dhbw.interactive.physics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class Gravity {
    private final List<Mass> masses = new LinkedList<>();

    public void addRandomMass() {
        masses.add(randomMass());
    }

    public void calculateForces() {
        for (Mass mass : masses) {
            mass.setVelocity(
                ((mass.getVelocity() + mass.getY()) * 9.81) / (mass.getWidth()));
        }
    }

    public void updatePositions() {
        for (Mass mass : masses) {
            mass.setY(mass.getY() + mass.getVelocity() * 0.1);
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (Mass mass : masses) {
            mass.draw(gc);
        }
    }

    private double randomNumber(int min, int max) {
        return ((Math.random() * (max - min)) + min);
    }

    private Mass randomMass() {
        if (randomNumber(0, 1) > 0.5) {
            return new CircularMass(
                randomNumber(100, 1500),
                randomNumber(100, 100),
                randomNumber(5, 50),
                Color.color(Math.random(), Math.random(), Math.random())
            );
        } else {
            return new RectangularMass(
                randomNumber(100, 1500),
                randomNumber(100, 100),
                randomNumber(5, 200),
                randomNumber(5, 50),
                Color.color(Math.random(), Math.random(), Math.random())
            );
        }
    }
}
