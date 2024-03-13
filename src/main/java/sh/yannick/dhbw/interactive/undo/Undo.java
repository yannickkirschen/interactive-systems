package sh.yannick.dhbw.interactive.undo;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Random;

/*
 * Exercise 8: Editable Objects with undo/redo-function.
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
public class Undo extends Application {
    private Group actState;
    private Node selectedObject;
    private Point2D selectRel;

    private UndoBuffer undo;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.WHITE);

        actState = new Group();
        root.getChildren().add(actState);

        undo = new UndoBuffer();

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, evt -> keyPressed(evt, root));

        stage.setTitle("Interactive Systems - Exercise 8 - Undo");

        stage.setScene(scene);
        stage.show();
    }

    private void mousePressed(MouseEvent evt) {
        if (evt.getButton() == MouseButton.PRIMARY) {
            for (Node n : actState.getChildren()) {
                selectRel = n.parentToLocal(evt.getX(), evt.getY());
                if (n.contains(selectRel)) {
                    selectedObject = n;
                    n.setEffect(new Glow(1.0));
                    return;
                }
            }
        } else if (evt.getButton() == MouseButton.SECONDARY) {
            for (Node n : actState.getChildren()) {
                if (n.contains(n.parentToLocal(evt.getX(), evt.getY()))) {
                    actState.getChildren().remove(n);
                    undo.storeAction(actState);
                    return;
                }
            }

            Rectangle r = new Rectangle(200, 100, new Color(0.4, 0.6 * (new Random()).nextFloat(), 0.9, 1.0));
            r.setStroke(Color.WHITE);
            r.setTranslateX(evt.getX());
            r.setTranslateY(evt.getY());
            actState.getChildren().add(r);
            undo.storeAction(actState);
        }
    }

    private void mouseDragged(MouseEvent evt) {
        if (selectedObject != null) {
            selectedObject.setTranslateX(evt.getX() - selectRel.getX());
            selectedObject.setTranslateY(evt.getY() - selectRel.getY());
        }
    }

    private void mouseReleased(MouseEvent evt) {
        if (selectedObject != null) {
            selectedObject.setEffect(new Glow(0));
            selectedObject = null;
            undo.storeAction(actState);
        }
    }

    private void keyPressed(KeyEvent evt, Group root) {
        if (evt.getCode() == KeyCode.Z && evt.isShortcutDown() && !evt.isShiftDown()) {
            root.getChildren().remove(actState);
            actState = undo.undo();
            if (actState != null) {
                root.getChildren().add(actState);
            }
        } else if (evt.getCode() == KeyCode.Z && evt.isShortcutDown() && evt.isShiftDown()) {
            root.getChildren().remove(actState);
            actState = undo.redo();
            if (actState != null) {
                root.getChildren().add(actState);
            }
        }
    }
}
