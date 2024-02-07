package sh.yannick.dhbw.interactive.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class WindowManager {
    private final List<Window> windows = new LinkedList<>();
    private final GraphicsContext gc;

    private Window selectedWindow;
    private double selectedX;
    private double selectedY;

    public WindowManager(GraphicsContext gc) {
        this.gc = gc;
    }

    public void addWindow(Window window) {
        windows.add(window);
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        windows.sort(Comparator.comparingInt(Window::getZIndex));
        for (Window window : windows) {
            window.draw(gc);
        }
    }

    void mousePressed(MouseEvent evt) {
        Window window = findWindow(evt.getX(), evt.getY());

        if (window != null) {
            selectedX = evt.getX();
            selectedY = evt.getY();

            window.setZIndex(windows.stream().map(Window::getZIndex).max(Comparator.comparingInt(i -> i)).orElse(0) + 1);
            draw(gc);
            selectedWindow = window;
        }
    }

    void mouseReleased(MouseEvent evt) {
        if (selectedWindow != null) {
            double newX = evt.getX();
            double newY = evt.getY();

            if (newX != selectedX || newY != selectedY) {
                selectedWindow.move((int) (newX - selectedX), (int) (newY - selectedY));
                draw(gc);
            }

            selectedWindow = null;
        }
    }

    void mouseMoved(MouseEvent evt) {
        if (selectedWindow != null) {
            double newX = evt.getX();
            double newY = evt.getY();

            if (newX != selectedX || newY != selectedY) {
                selectedWindow.move((int) (newX - selectedX), (int) (newY - selectedY));
                selectedX = newX;
                selectedY = newY;
                draw(gc);
            }
        }
    }

    private Window findWindow(double x, double y) {
        List<Window> candidates = new LinkedList<>();

        for (Window window : windows) {
            if (window.isInWindow(x, y)) {
                candidates.add(window);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.stream().max(Comparator.comparingInt(Window::getZIndex)).orElse(null);
    }
}
