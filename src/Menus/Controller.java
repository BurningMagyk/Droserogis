package Menus;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Controller
{
    private final int WIDTH, HEIGHT;
    private final Group root;

    Controller(final Stage stage)
    {
        WIDTH = (int) stage.getWidth();
        HEIGHT = (int) stage.getHeight();

        root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        stage.setX(0);
        stage.setY(0);
        stage.setScene(scene);
        stage.show();
    }

    void start()
    {
        
    }
}
