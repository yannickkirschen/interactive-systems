package sh.yannick.dhbw.interactive;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Random;

/*
 * Exercise 1 - Colors
 *
 * @author Eckhard Kruse
 * @author Yannick Kirschen
 */
public class Colors extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Group root = new Group();
        Canvas canvas = new Canvas();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, 600, 500);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        scene.widthProperty().addListener(evt -> draw(gc));
        scene.heightProperty().addListener(evt -> draw(gc));

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        draw(gc);

        stage.setTitle("Interactive Systems - Exercise 1 - Colors");
        stage.setScene(scene);
        stage.show();
    }

    private void draw(GraphicsContext gc) {
        Random myRnd = new Random(0);

        // Background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (int i = 0; i < 20; i++) {
            renderElement(gc, i, myRnd);
        }
    }

    private void renderElement(GraphicsContext gc, int cnt, Random myRnd) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        double x = canvasWidth * myRnd.nextFloat();
        double y = canvasHeight * myRnd.nextFloat();
        double w = canvasWidth / 4 * (myRnd.nextFloat() + 0.5f);
        double h = canvasHeight / 5 * (myRnd.nextFloat() + 0.8f);
        double fontsize = canvasWidth / 30;

        // Boxes
        gc.setFill(Color.BLACK);
        gc.fillRect(x - w / 2, y - h / 2, w, h);

        gc.setFill(Color.valueOf("#FCFCFC"));
        gc.fillRect(x - w / 2 + 2, y - h / 2 + 2, w - 4, canvasWidth / 30 + 4);
        gc.fillRect(x - w / 2 + 2, y - h / 2 + 8 + canvasWidth / 30, w - 4, h - 10 - canvasWidth / 30);

        // Title
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BLACK, FontPosture.REGULAR, fontsize));
        gc.fillText("Title" + cnt, x - w / 2 + 4, y - h / 2 + fontsize);

        // Text
        gc.setFill(Color.BLACK);
        fontsize /= 2;
        gc.setFont(new Font("Arial", fontsize));
        for (int i = 1; i < h / fontsize - 4; i++) {
            gc.fillText(i + ". text content", x - w / 2 + 20, y - h / 2 + (i + 3) * fontsize);
        }
    }
}
