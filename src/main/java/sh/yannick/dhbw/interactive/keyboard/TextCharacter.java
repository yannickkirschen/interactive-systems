package sh.yannick.dhbw.interactive.keyboard;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Store single character of typed text including rendering hints and render method.
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
class TextCharacter {
    private final String myChar;
    private final Color color;
    private double size;

    TextCharacter(String c) {
        myChar = c;
        size = 20.;
        color = getRandomColor();
    }

    private static Color getRandomColor() {
        return new Color(Math.random(), Math.random(), Math.random(), 1.0);
    }

    void grow() {
        size += 0.5;
    }

    double render(GraphicsContext gc, double x, double y) {
        Font f = new Font(size);
        Text t = new Text(myChar);
        t.setFont(f);

        gc.setFont(f);
        gc.setFill(color);
        gc.fillText(myChar, x, y);

        return x + t.getLayoutBounds().getWidth();
    }
}
