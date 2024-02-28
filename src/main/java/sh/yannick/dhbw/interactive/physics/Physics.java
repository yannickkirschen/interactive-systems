package sh.yannick.dhbw.interactive.physics;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Exercise 5 - Physics simulation
 *
 * @author Yannick Kirschen
 */
public class Physics extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Canvas canvas = new Canvas();
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(root, 1500, 1000);

        Gravity gravity = new Gravity();

        scene.widthProperty().addListener(evt -> gravity.draw(gc));
        scene.heightProperty().addListener(evt -> gravity.draw(gc));

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), evt -> {
            gravity.calculateForces();
            gravity.updatePositions();
            gravity.draw(gc);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Timeline randomAdding = new Timeline(new KeyFrame(Duration.millis(100), evt -> {
            gravity.addRandomMass();
        }));
        randomAdding.setCycleCount(Animation.INDEFINITE);
        randomAdding.play();

        stage.setTitle("Interactive Systems - Exercise 5 - Physics");
        stage.setScene(scene);
        stage.show();
    }
}
