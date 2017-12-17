package Menus;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.Toolkit;

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
    public void start(Stage stage)
    {
        final String VERSION = "indev";
        final String NAME = "Droserogis vs Sothli";

        /* Prepare the basics: stage, scene, group, and canvas */
        Group root = new Group();
        final int SCENE_WIDTH = SCREEN_DIMS[0] / 2;
        final int SCENE_HEIGHT = SCREEN_DIMS[1] / 2;
        final Scene SCENE = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT,
                Color.BLACK);
        final Canvas canvas = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        /* Sample code to draw on the canvas: */
        context.setFill(Color.BLUE);
        context.fillRect(300,250,100,100);
        //context.drawImage();
        /* TODO: Make it import a logo image and draw it on the canvas. */

        /* Add the canvas and widgets in order */
        root.getChildren().add(canvas);
        setWidgets(stage, root, SCENE_WIDTH, SCENE_HEIGHT);

        /* Set the stage at the center of the screen */
        stage.setX(SCREEN_DIMS[0] / 4);
        stage.setY(SCREEN_DIMS[1] / 4);

        /* Set scene and make stage visible */
        stage.setTitle(NAME + " - " + VERSION);
        /* TODO: Make this window border-less.
           TODO: Put this title on the main game window. */
        stage.setScene(SCENE);
        stage.show();
    }

    /**
     * Instantiates widgets and modifies them according to the scene's
     * dimensions. Then adds those widgets to the scene.
     * @param width - width of the scene
     * @param height - height of the scene
     */
    private void setWidgets(Stage stage, Group group,
                            int width, int height)
    {
        /* How much space will go between the widgets and borders */
        final int STUFFING = Math.min(width, height) / 20;

        /* Give buttons names */
        Button startButton = new Button("Start Game");
        Button exitButton = new Button("Exit");

        /* Set button sizes */
        startButton.setPrefWidth(width * 3 / 15);
        startButton.setPrefHeight(height / 10);
        exitButton.setPrefWidth(width * 2 / 15);
        exitButton.setPrefHeight(height / 10);

        /* Calculate boundary values */
        int boundsX[] = new int[2];
        boundsX[0] = width - (int) exitButton.getPrefWidth() - STUFFING;
        boundsX[1] = boundsX[0] - (int) startButton.getPrefWidth() - STUFFING;
        int boundsY[] = new int[1];
        boundsY[0] = height - (int) startButton.getPrefHeight() - STUFFING;

        /* Set button locations */
        exitButton.setTranslateX(boundsX[0]);
        startButton.setTranslateX(boundsX[1]);
        exitButton.setTranslateY(boundsY[0]);
        startButton.setTranslateY(boundsY[0]);

        /* Set button actions */
        startButton.setOnAction(event -> startGame(stage, group));
        exitButton.setOnAction(event -> quitGame(stage, group));

        /* Add widgets to the ArrayList */
        group.getChildren().add(startButton);
        group.getChildren().add(exitButton);
    }

    //private void

    /* Starts the game */
    private void startGame(Stage stage, Group root)
    {
        quitGame(stage, root); // temp
    }

    /* Quits the game */
    private void quitGame(Stage stage, Group root)
    {
        root.getChildren().clear();
        stage.close();
        Platform.exit();
        System.exit(0);
    }
}
