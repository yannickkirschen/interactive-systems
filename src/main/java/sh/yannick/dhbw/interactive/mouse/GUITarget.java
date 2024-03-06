package sh.yannick.dhbw.interactive.mouse;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * Mouse Target: Mouse-clickable object
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
class GUITarget {
    private final Group guiTargetNode;

    GUITarget(int no) {
        guiTargetNode = new Group();

        Rectangle r = new Rectangle(0, 0, 80, 50);
        r.setStroke(Color.WHITE);
        r.setFill(Color.BURLYWOOD);
        guiTargetNode.getChildren().add(r);

        Text t = new Text(10, 30, "Nr. " + no);
        t.setFont(new Font(25.));
        t.setFill(Color.WHITE);
        guiTargetNode.getChildren().add(t);
    }

    Node getGuiObject() {
        return guiTargetNode;
    }

    void reset(double width, double height) {
        Random rnd = new Random();

        guiTargetNode.setTranslateX(rnd.nextDouble() * (width - 120) + 20);
        guiTargetNode.setTranslateY(rnd.nextDouble() * (height - 70) + 10);
        guiTargetNode.setEffect(new ColorAdjust());
    }
}

