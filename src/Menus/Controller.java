package Menus;

import Util.Print;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;

class Controller
{
    private final int WIDTH, HEIGHT;
    private final Group root;
    private final GraphicsContext context;

    Controller(final Stage stage)
    {
        WIDTH = (int) stage.getWidth();
        HEIGHT = (int) stage.getHeight();

        root = new Group();
        stage.setX(0);
        stage.setY(0);
        stage.initStyle(StageStyle.UNDECORATED);

        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        final Canvas canvas = new Canvas(WIDTH, HEIGHT);
        context = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        stage.setScene(scene);
        stage.show();
    }

    //@Override
    public void handle(long now)
    {

    }

    void goToStartMenu()
    {
        /* Try importing image file */
        InputStream input = getClass()
                .getResourceAsStream("/Images/start_background.png");
        if (input != null)
        {
            /* Having it as an ImageView allows it to to be modified */
            Image image = new Image(input);

            /* This centers the window onto the image */
            double sizeScale = HEIGHT / image.getHeight();
            context.drawImage(image,
                    (WIDTH - image.getWidth() * sizeScale) / 2,
                    0,
                    image.getWidth() * sizeScale,
                    image.getHeight() * sizeScale);
        }
        else Print.red("\"start_background.png\" was not imported");
    }
}
