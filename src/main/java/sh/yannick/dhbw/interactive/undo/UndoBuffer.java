package sh.yannick.dhbw.interactive.undo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

class UndoBuffer {
    private final int maxUndo = 10;
    private final Group[] buffer;

    private int actIndex = 0;
    private int maxIndex = 0;

    UndoBuffer() {
        buffer = new Group[maxUndo];
    }

    Group clone(Group in) {
        Group c = new Group();
        if (in != null)
            for (Node n : in.getChildren()) {
                Rectangle r = (Rectangle) n;
                Rectangle cr = new Rectangle(r.getWidth(), r.getHeight());
                cr.setFill(r.getFill());
                cr.setStroke(r.getStroke());
                cr.setTranslateX(r.getTranslateX());
                cr.setTranslateY(r.getTranslateY());
                c.getChildren().add(cr);
            }
        return c;
    }

    void storeAction(Group g) {
        if (actIndex == maxUndo - 1) {
            actIndex = 0;
            maxIndex = 1;
        } else {
            actIndex++;
            maxIndex = actIndex + 1;
        }

        buffer[actIndex] = clone(g);
    }

    Group undo() {
        if (actIndex == 0) {
            actIndex = maxUndo - 1;
        } else {
            actIndex--;
        }
        return buffer[actIndex];
    }

    Group redo() {
        if (actIndex == maxIndex - 1) {
            actIndex = 0;
        } else {
            actIndex++;
        }
        return buffer[actIndex];
    }
}
