package sh.yannick.dhbw.interactive.mouse;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;


/*
 * Exercise 7: Mouse-Tuning
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
public class MouseTuning extends Application {
    private final int targetCounts = 5;
    private final int msStep = 200;

    private Robot mouseRobot;
    private Group cursor;
    private Shape cursorShape;
    private Text display;

    private double actX;
    private double actY;
    private double difX;
    private double difY;

    private double rotation;
    private int currentTarget;
    private int msTime;
    private double totalDistance;
    private double minDistance;
    private boolean startTimer;

    private int globalTotalTime;
    private double globalTotalDistance;

    private GUITarget[] targets;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        Scene scene = new Scene(root, 1000, 700);
        scene.setFill(new Color(0.2, 0.3, 0.5, 1.0));

        targets = new GUITarget[targetCounts];
        for (int i = 0; i < targetCounts; i++) {
            targets[i] = new GUITarget(i + 1);
            targets[i].reset(scene.getWidth(), scene.getHeight());
            root.getChildren().add(targets[i].getGuiObject());
        }
        initTargets(scene);

        display = new Text(20, 20, "");
        display.setFont(new Font(15));
        display.setFill(Color.WHITE);
        root.getChildren().add(display);

        cursorShape = new Polygon(0, 0, -15, 15, -10, 5, -30, 7, -30, -7, -10, -5, -15, -15);
        cursorShape.setFill(Color.WHITE);
        cursorShape.setStroke(Color.BLACK);
        cursor = new Group();
        cursor.getChildren().add(cursorShape);
        root.getChildren().add(cursor);

        actX = 500;
        actY = 250;
        difX = 0;
        difY = 0;

        scene.addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> mouseClicked(evt, scene));

        WritableImage i = new WritableImage(1, 1);
        i.getPixelWriter().setColor(0, 0, new Color(0.2, 0.3, 0.5, 0.01));
        scene.setCursor(new ImageCursor(i));

        stage.setTitle("Interactive Systems - Exercise 7 - Mouse Tuning");
        stage.setScene(scene);
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(msStep), evt -> displayUpdate(scene)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        try {
            mouseRobot = new Robot();
        } catch (AWTException ignored) {
        }
    }

    private void initTargets(Scene scene) {
        minDistance = 0;
        for (int i = 0; i < targetCounts; i++) {
            targets[i].reset(scene.getWidth(), scene.getHeight());
            if (i > 0) {
                double x = targets[i].getGuiObject().getTranslateX() - targets[i - 1].getGuiObject().getTranslateX();
                double y = targets[i].getGuiObject().getTranslateY() - targets[i - 1].getGuiObject().getTranslateY();
                minDistance += Math.sqrt(x * x + y * y);
            }
        }

        msTime = 0;
        totalDistance = 0;
        currentTarget = 0;
        startTimer = false;
    }

    private void mouseMoved(MouseEvent evt) {
        difX = evt.getX() - 300;
        difY = evt.getY() - 200;

        rotation = rotation + 1; // This would be the place to calculate the rotation angle
        actX -= difX / 2;
        actY += difY / 3;

        if (startTimer) {
            totalDistance += Math.sqrt(difX * difX + difY * difY);
        }
        globalTotalDistance += Math.sqrt(difX * difX + difY * difY);

        mouseRobot.mouseMove((int) (300 + evt.getScreenX() - evt.getX()), (int) (200 + evt.getScreenY() - evt.getY()));
    }

    private void mouseClicked(MouseEvent evt, Scene scene) {
        if (evt.getButton() == MouseButton.PRIMARY && currentTarget < targetCounts) {
            Node o = targets[currentTarget].getGuiObject();
            if (o.contains(o.sceneToLocal(actX, actY))) {
                currentTarget++;
                if (currentTarget == 1)
                    startTimer = true;
                if (currentTarget >= targetCounts)
                    startTimer = false;
                o.setEffect(new ColorAdjust(0, -0.6, -0.4, 0));
            }
        } else if (evt.getButton() == MouseButton.SECONDARY) {
            initTargets(scene);
        }
    }

    private void displayUpdate(Scene scene) {
        if (actX < 0) actX = 0;
        if (actX >= scene.getWidth()) actX = scene.getWidth() - 1;
        if (actY < 0) actY = 0;
        if (actY >= scene.getHeight()) actY = scene.getHeight() - 1;

        cursorShape.setRotate(rotation);

        double size = 1 + (globalTotalDistance / globalTotalTime);
        cursorShape.setScaleX(size / 3);
        cursorShape.setScaleY(size);


        Point2D p = cursorShape.localToParent(0, 0);
        cursor.setTranslateX(actX - p.getX());
        cursor.setTranslateY(actY - p.getY());

        display.setText("Time: " + msTime + " ms   " +
            "Mouse path: " + ((int) totalDistance) + " Pixel   " +
            "Direct Path: " + (int) minDistance +
            "    Speed: " + (int) (totalDistance * 1000 / (msTime + 0.01)) + " pixel/s");

        if (startTimer) {
            msTime += msStep;
        }

        globalTotalTime += msStep;
    }
}
