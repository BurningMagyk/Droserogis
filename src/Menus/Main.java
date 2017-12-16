package Menus;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application
{
    private final int[] SCREEN_DIMS = {
            Toolkit.getDefaultToolkit().getScreenSize().width,
            Toolkit.getDefaultToolkit().getScreenSize().height };

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        String VERSION = "indev";

        /* Prepare the basics: stage, scene, group, and canvas */
        Group root = new Group();
        final Scene scene = new Scene(root,
                SCREEN_DIMS[0] / 2, SCREEN_DIMS[1] / 2, Color.BLACK);
        stage.setScene(scene);
        final Canvas canvas = new Canvas(300, 300);
        GraphicsContext context = canvas.getGraphicsContext2D();

        /* Sample code to draw on the canvas: */
        context.setFill(Color.BLUE);
        context.fillRect(75,75,100,100);

        /* Add the widgets in order */
        root.getChildren().add(canvas);

        // Set the stage at the center of the screen.
        stage.setX(SCREEN_DIMS[0] / 4);
        stage.setY(SCREEN_DIMS[1] / 4);

        stage.setScene(scene);
        stage.show();
    }

    private void addWidgets(Group group)
    {
        Button startButton = new Button("Start Game");
        Button exitButton = new Button("Exit");

        //group.getChildren().add(startButton, exitButton);
    }
}
