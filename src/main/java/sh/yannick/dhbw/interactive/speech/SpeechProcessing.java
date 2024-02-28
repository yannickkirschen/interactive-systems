package sh.yannick.dhbw.interactive.speech;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * Exercise 06: Audio MFCC Speech Processing
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
public class SpeechProcessing extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Canvas canvas = new Canvas(800, 500);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        AudioInput audioInput = new AudioInput();
        PatternMatching patternMatching = new PatternMatching();

        AudioDisplay audioDisplay = new AudioDisplay(audioInput, patternMatching, gc);

        scene.widthProperty().addListener(evt -> audioDisplay.draw());
        scene.heightProperty().addListener(evt -> audioDisplay.draw());

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        scene.addEventHandler(MouseEvent.MOUSE_MOVED, audioDisplay::mouseMoved);
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, audioDisplay::mouseClicked);

        stage.setTitle("Interactive Systems - Exercise 6 - Audio MFCC Speech Processing");
        stage.setScene(scene);
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(audioInput.getFrameDuration()), evt -> audioDisplay.update()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
