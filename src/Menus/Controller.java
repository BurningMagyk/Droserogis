package Menus;

import Game.Game;
import Util.Print;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;

class Controller extends AnimationTimer
{
    private final Game GAME;

    private final Mouse MOUSE;
    private final Keyboard KEYBOARD;

    private Menu currentMenu;

    private Menu startMenu, topMenu, storyMenu,
            versusMenu, optionsMenu, creditsMenu;

    private long lastUpdate = 0;

    Controller(final Stage stage)
    {
        final int WIDTH = (int) stage.getWidth();
        final int HEIGHT = (int) stage.getHeight();

        Group ROOT = new Group();
        stage.setX(0);
        stage.setY(0);
        stage.initStyle(StageStyle.UNDECORATED);

        MOUSE = new Mouse();
        KEYBOARD = new Keyboard();

        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, Color.BLACK);
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        GraphicsContext CONTEXT = CANVAS.getGraphicsContext2D();
        ROOT.getChildren().add(CANVAS);

        stage.setScene(scene);
        stage.show();

        /* Set up menus */
        currentMenu = null;
        topMenu = new TopMenu(ROOT, CONTEXT);
        startMenu = new StartMenu(ROOT, CONTEXT);
        versusMenu = new VersusMenu(ROOT, CONTEXT);
        GAME = new Game(ROOT, CONTEXT);

        /* Temporary */
        storyMenu = startMenu;
        optionsMenu = startMenu;
        creditsMenu = startMenu;

        /* Set up mouse and keyboard input */
        scene.addEventHandler(MouseEvent.ANY, MOUSE);
        scene.addEventHandler(KeyEvent.ANY, KEYBOARD);

        goToMenu(Menu.MenuEnum.START);

        /* Try importing image file */
        Image cursorImage;
        ImageCursor cursor;
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

        Menu.MenuEnum nextMenu = currentMenu.animateFrame(framesMissed + 1);
        if (nextMenu != null) goToMenu(nextMenu);

        lastUpdate = now;
    }

    public void stop()
    {
        super.stop();
        startMenu.stopMusic();
    }

    private void goToMenu(Menu.MenuEnum menuEnum)
    {
        Menu menu;
        switch (menuEnum)
        {
            case START: {
                menu = startMenu;
                break;
            }
            case TOP: {
                menu = topMenu;
                break;
            }
            case STORYTIME: {
                menu = storyMenu;
                break;
            }
            case VERSUS: {
                menu = versusMenu;
                break;
            }
            case OPTIONS: {
                menu = optionsMenu;
                break;
            }
            case GALLERY: {
                menu = creditsMenu;
                break;
            }
            case QUIT: {
                Platform.exit();
                System.exit(0);
            }
            case GAME: {
                goToGameplay();
                return;
            }
            default:
                menu = null;
        }

        if (currentMenu != null) currentMenu.reset();
        currentMenu = menu;
        MOUSE.setReactor(menu);
        KEYBOARD.setReactor(menu);
        currentMenu.reset();
    }

    private void goToGameplay()
    {
        stop();
        MOUSE.setReactor(GAME);
        KEYBOARD.setReactor(GAME);
        GAME.start();
    }
}
