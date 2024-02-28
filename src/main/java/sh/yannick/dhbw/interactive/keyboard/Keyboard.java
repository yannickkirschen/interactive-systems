package sh.yannick.dhbw.interactive.keyboard;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

/*
 * Exercise 4: New methods of using the keyboard.
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
public class Keyboard extends Application {
    private final List<TextCharacter> typedText = new LinkedList<>();

    private TextCharacter currentCharacter;
    private String currentCharAsString;
    private boolean shift = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Group root = new Group();
        Canvas canvas = new Canvas();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, 800, 500);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        scene.widthProperty().addListener(evt -> draw(gc));
        scene.heightProperty().addListener(evt -> draw(gc));

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        draw(gc);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), evt -> {
            if (currentCharacter != null) {
                currentCharacter.grow();
                draw(gc);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

//        scene.addEventHandler(KeyEvent.KEY_TYPED, evt -> {
//            if (!evt.getCharacter().equals(currentCharAsString)) {
//                currentCharacter = new TextCharacter(evt.getCharacter());
//                currentCharAsString = evt.getCharacter();
//                System.out.println(currentCharAsString == null);
//                System.out.println(currentCharAsString);
//                typedText.add(currentCharacter);
//                draw(gc);
//            }
//        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode().equals(KeyCode.SHIFT)) {
                shift = false;
            }

            currentCharacter = null;
            currentCharAsString = null;
            draw(gc);
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode().equals(KeyCode.SHIFT)) {
                shift = true;
            }
            if (evt.getCode().equals(KeyCode.ENTER)) {
                currentCharacter = new TextCharacter("");
                currentCharAsString = "\n";
            }
        });

        stage.setTitle("Interactive Systems - Exercise 4 - Keyboard");
        stage.setScene(scene);
        stage.show();
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        double x = 0;
        double y = 50;
        for (TextCharacter typedChar : typedText) {
            if (currentCharAsString != null && currentCharAsString.equals("\n")) {
                x = 0;
                y += 60;
            } else {
                x = typedChar.render(gc, x, y);
            }

            if (x > gc.getCanvas().getWidth() - 30) {
                x = 0;
                y += 30;
            }
        }
    }
}
