package sh.yannick.dhbw.interactive.ui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Exercise 2 - Deep GUI
 *
 * @author Yannick Kirschen
 */
public class DeepUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Window window1 = new Window(new Point(10, 10), new Size(300, 250), Color.valueOf("#F5F5F5"));
        Window window2 = new Window(new Point(50, 50), new Size(450, 300), Color.valueOf("#F5F5F5"));
        Window window3 = new Window(new Point(100, 100), new Size(280, 250), Color.valueOf("#F5F5F5"));

        window1.setName("Black");
        window2.setName("Red");
        window3.setName("Blue");

        window1.setZIndex(0);
        window2.setZIndex(1);
        window3.setZIndex(2);

        Group root = new Group();
        Canvas canvas = new Canvas();
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        WindowManager windowManager = new WindowManager(gc);
        windowManager.addWindow(window1);
        windowManager.addWindow(window2);
        windowManager.addWindow(window3);

        Scene scene = new Scene(root, 1500, 1000);

        scene.widthProperty().addListener(evt -> windowManager.draw(gc));
        scene.heightProperty().addListener(evt -> windowManager.draw(gc));

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, windowManager::mousePressed);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, windowManager::mouseMoved);
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, windowManager::mouseMoved);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, windowManager::mouseReleased);

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        windowManager.draw(gc);

        stage.setTitle("Interactive Systems - Exercise 2 - Deep GUI");
        stage.setScene(scene);
        stage.show();
    }
}

