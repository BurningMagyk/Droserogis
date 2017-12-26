package Menus;

import Util.Print;
import Util.Translator;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;

class Controller extends AnimationTimer
{
    private final int WIDTH, HEIGHT;
    private final Group ROOT;
    private final GraphicsContext CONTEXT;

    private final Mouse MOUSE;
    private final Keyboard KEYBOARD;

    private Menu currentMenu;

    private Menu startMenu;
    private Menu topMenu;

    private long lastUpdate = 0;

    Controller(final Stage stage)
    {
        WIDTH = (int) stage.getWidth();
        HEIGHT = (int) stage.getHeight();

        ROOT = new Group();
        stage.setX(0);
        stage.setY(0);
        stage.initStyle(StageStyle.UNDECORATED);

        MOUSE = new Mouse();
        KEYBOARD = new Keyboard();

        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, Color.BLACK);
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        CONTEXT = CANVAS.getGraphicsContext2D();
        ROOT.getChildren().add(CANVAS);
        ROOT.getStylesheets();

        stage.setScene(scene);
        stage.show();

        /* Set up menus */
        topMenu = new TopMenu(CONTEXT, WIDTH, HEIGHT);
        startMenu = new StartMenu(CONTEXT, WIDTH, HEIGHT, topMenu);

        /* Set up mouse and keyboard input */
        scene.addEventHandler(MouseEvent.ANY, MOUSE);
        scene.addEventHandler(KeyEvent.ANY, KEYBOARD);

        goToMenu(startMenu);

        /* Try importing image file */
        Image cursorImage;
        ImageCursor cursor = null;
        InputStream input = getClass()
                .getResourceAsStream("/Images/cursor.png");
        if (input != null)
        {
            /* This centers the window onto the image */
            cursorImage = new Image(input);
            //double sizeScale = image.getWidth() / width;
            cursor = new ImageCursor(cursorImage, 30, 30);
            scene.setCursor(cursor);
        }
        else Print.red("\"opening_background.png\" was not imported");
    }

    @Override
    public void handle(long now)
    {
        int framesMissed = (int) ((now - lastUpdate) / 16_666_666);

        Menu nextMenu = currentMenu.animateFrame(framesMissed + 1);
        if (nextMenu != null) goToMenu(nextMenu);

        lastUpdate = now;
    }

    private void goToMenu(Menu menu)
    {
        currentMenu = menu;
        MOUSE.setMenu(menu);
        KEYBOARD.setMenu(menu);
    }

    private class ForTheAge extends Timer
    {

    }
}
