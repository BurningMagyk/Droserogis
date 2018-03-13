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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class Controller extends AnimationTimer
{
    private final Game GAME;

    private final Mouse MOUSE;
    private final Keyboard KEYBOARD;
    private final ImageView BACKGROUND;
    private final Group ROOT;

    private final int WIDTH, HEIGHT;

    private Menu currentMenu;
    private Menu startMenu, topMenu, storyMenu,
            versusMenu, optionsMenu, creditsMenu;
    private List<Menu> menuList;

    private final Stage stage;
    private long lastUpdate = 0;

    Controller(final Stage stage)
    {
        WIDTH = (int) stage.getWidth();
        HEIGHT = (int) stage.getHeight();

        ROOT = new Group();

        MOUSE = new Mouse();
        KEYBOARD = new Keyboard();

        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, Color.GREY);
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        GraphicsContext CONTEXT = CANVAS.getGraphicsContext2D();
        Main.IMPORTER.setContext(CONTEXT);
        BACKGROUND = new ImageView();

        /* Background image needs to be added before the canvas */
        ROOT.getChildren().add(BACKGROUND);
        ROOT.getChildren().add(CANVAS);

        this.stage = stage;
        stage.setX(0);
        stage.setY(0);
        stage.setScene(scene);

        /* Set up menus */
        currentMenu = null;
        menuList = new ArrayList<>();
        topMenu = new TopMenu(CONTEXT); menuList.add(topMenu);
        startMenu = new StartMenu(CONTEXT); menuList.add(startMenu);
        versusMenu = new VersusMenu(CONTEXT); menuList.add(versusMenu);

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
        else Print.red("\"/Images/cursor.png\" was not imported");
    }

    /**
     * Call to start the game after the prompt.
     */
    @Override
    public void start()
    {
        /* Start calling handle */
        super.start();
        stage.show();
        currentMenu.startMedia();

        currentMenu.reset(ROOT);
        currentMenu.decorate(ROOT);
    }

    @Override
    public void handle(long now)
    {
        int framesMissed = (int) ((now - lastUpdate) / 16_666_666);

        Menu.MenuEnum nextMenu = currentMenu.animateFrame(framesMissed + 1);
        if (nextMenu != null) goToMenu(nextMenu);

        lastUpdate = now;
    }

    @Override
    public void stop()
    {
        super.stop();
        startMenu.stopMedia();
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

        if (currentMenu != null)
        {
            currentMenu.reset(ROOT);
            /* The media does not stop in certain transitions */
            if (!isSpecialCase(currentMenu, menu)) stopMedia();
        }

        menu.reset(ROOT);
        setBackground(menu);
        menu.decorate(ROOT);

        MOUSE.setReactor(menu);
        KEYBOARD.setReactor(menu);
        currentMenu = menu;

        /* So that the Start Menu song doesn't play at the prompt */
        if (menuEnum != Menu.MenuEnum.START) currentMenu.startMedia();
    }

    private boolean isSpecialCase(Menu prev, Menu next)
    {
        boolean cases[] = {
                prev == startMenu && next == topMenu,
                prev == topMenu && next == startMenu};
        for (boolean _case : cases)
        {
            if (_case) return true;
        }
        return false;
    }

    private void setBackground(Menu menu)
    {
        Image image = menu.getBackground();
        if (image == null) return;

        BACKGROUND.setImage(image);
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double sizeScale;

        if (imageWidth / WIDTH < imageHeight / HEIGHT)
        {
            /* The image is offset if it's the Top Menu */
            double yMod = menu == topMenu ? 8F / 5F : 1;

            sizeScale = WIDTH / imageWidth;
            BACKGROUND.setX(0);
            BACKGROUND.setY((HEIGHT - imageHeight * sizeScale) / 2 * yMod);

        }
        else
        {
            sizeScale = HEIGHT / imageHeight;
            BACKGROUND.setX((WIDTH - imageWidth * sizeScale) / 2);
            BACKGROUND.setY(0);
        }

        BACKGROUND.setFitWidth(imageWidth * sizeScale);
        BACKGROUND.setFitHeight(imageHeight * sizeScale);
    }

    private void stopMedia()
    {
        menuList.forEach(Menu::stopMedia);
    }

    private void goToGameplay()
    {
        stop();
        MOUSE.setReactor(GAME);
        KEYBOARD.setReactor(GAME);
        GAME.start();
    }
}
