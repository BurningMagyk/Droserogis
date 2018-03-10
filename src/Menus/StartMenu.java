package Menus;

import Util.LanguageEnum;
import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class StartMenu implements Menu
{
    private final GraphicsContext context;

    private final double textPosX;
    private final double textPosY;

    private final Image image;
    private final String message;
    private Font font;

    private double opacity;
    private boolean fading;

    private MenuEnum nextMenu;
    private boolean pressed = false;

    /* Having mediaPlayer be global makes
     * it work properly for some reason */
    private MediaPlayer mediaPlayer;

    StartMenu(final GraphicsContext context)
    {
        this.context = context;
        message = getMessage();
        opacity = 1.0;
        fading = true;
        nextMenu = null;

        final int WIDTH = (int) context.getCanvas().getWidth();
        final int HEIGHT = (int) context.getCanvas().getHeight();

        /* Try importing image file */
        InputStream input = getClass()
                .getResourceAsStream("/Images/start_background.png");
        if (input != null)
        {
            image = new Image(input);
        }
        else
        {
            image = null;
            Print.red("\"start_background.png\" was not imported");
        }

        /* Prepare text dimensions, set to default values */
        double textWidth = WIDTH / 2;
        double textHeight = HEIGHT / 6;

        /* Try importing the Planewalker or Kaisho font */
        String fileName = Main.language == LanguageEnum.WAPANESE
                ? "kaisho.ttf" : "planewalker.otf";
        input = getClass()
                .getResourceAsStream("/Fonts/" + fileName);
        float fontSize = Math.min(WIDTH, HEIGHT) / 12;
        if (input == null)
        {
            font = Font.font(fontSize);
            Print.red("\"" + fileName + "\" was not imported");
        }
        else
        {
            font = Font.loadFont(input, fontSize);
            context.setFont(font);

            /* Make dummy text to get dimensions */
            Text text = new Text(message);
            text.setFont(font);
            textWidth = text.getLayoutBounds().getWidth();
            textHeight = text.getLayoutBounds().getHeight();
        }

        /* Calculate position of where the text will be animated
         * Using the dummy's text dimensions to center it on the screen */
        textPosX = (WIDTH - textWidth) / 2;
        textPosY = (HEIGHT - textHeight) / 4 * 3;

        /* Try importing music */
        URL url = getClass().getResource("/Music/start_background.mp3");
        if (url != null)
        {
            Media music;
            try {
                music = new Media(url.toURI().toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                music = null;
                mediaPlayer = null;
            }
            if (music != null) mediaPlayer = new MediaPlayer(music);
            else Print.red("\"start_background.mp3\" was not imported");
        }
        else Print.red("\"start_background.mp3\" was not imported");
    }

    @Override
    public MenuEnum animateFrame(int framesToGo)
    {
        /* Clear canvas */
        context.clearRect(0, 0, context.getCanvas().getWidth(),
                context.getCanvas().getHeight());

        /* Set message to change opacity */
        if (fading) opacity -= 0.005 * framesToGo;
        else opacity += 0.005 * framesToGo;

        if (opacity >= 1.0)
        {
            opacity = 1.0;
            fading = true;
        }
        else if (opacity <= 0.3)
        {
            opacity = 0.3;
            fading = false;
        }

        /* Draw message */
        context.setFill(Color.rgb(0,0,0, opacity));
        context.fillText(message,
                textPosX,
                textPosY);

        /* Goes to Top menu when a key is typed or the mouse is clicked */
        return nextMenu;
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        if (this.pressed) nextMenu = MenuEnum.TOP;
        this.pressed = pressed;
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y)
    {
        if (this.pressed) nextMenu = MenuEnum.TOP;
        this.pressed = pressed;
    }

    @Override
    public void mouse(int x, int y)
    {
        /* Does nothing */
    }

    @Override
    public Image getBackground()
    {
        return image;
    }

    @Override
    public void startMedia()
    {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    @Override
    public void stopMedia()
    {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    @Override
    public void reset()
    {
        pressed = false;
        nextMenu = null;
        if (font != null) context.setFont(font);
    }

    /* Get the message in the correct language
     * Just one string, so a Translator isn't needed */
    private String getMessage()
    {
        String[] messages = {"Press any key to start",
                "Presione una tecla para empiezar",
                "Premere un tasto per iniziare",
                "Appuyez sur une touche pour démarrer",
                "Drücke zum Starten eine Taste",
                "任意のキーを押して開始"};

        return messages[Main.language.getID()];
    }
}
