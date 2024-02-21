package sh.yannick.dhbw.interactive.audio;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/*
 * Exercise 3: Interactive Audio Output
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
public class Audio extends Application {
    private ImageView img;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root);

        Image image = new Image(Objects.requireNonNull(Audio.class.getResourceAsStream("/sh/yannick/dhbw/interactive/audio/scene.jpg")));
        img = new ImageView(image);
        root.getChildren().add(img);

        InteractiveAudioOutput audioOutput = new InteractiveAudioOutput(stage);
        audioOutput.listen();

        scene.widthProperty().addListener(evt -> draw(scene));
        scene.heightProperty().addListener(evt -> draw(scene));
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, audioOutput::mouseMoved);

        stage.setTitle("Interactive Systems - Interactive Audio Output");
        stage.setScene(scene);
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(50), evt -> audioOutput.audioUpdate()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void draw(Scene scene) {
        // TODO: Verschiedene Oberflächen mit verschiedenen Klangeigenschaften
        img.setFitWidth(scene.widthProperty().doubleValue());
        img.setFitHeight(scene.heightProperty().doubleValue());
    }
}
