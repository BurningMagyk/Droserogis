package Menus;

import Importer.AudioResource;
import Importer.FontResource;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class StartMenu implements Menu
{
    private final GraphicsContext context;

    private final int WIDTH, HEIGHT;

    private final Image image;
    private FontResource font;
    private Text message;

    private double opacity;
    private boolean fading;

    private MenuEnum nextMenu;
    private boolean pressed = false;

    private AudioResource audio;

    StartMenu(final GraphicsContext context)
    {
        this.context = context;
        opacity = 1.0;
        fading = true;
        nextMenu = null;

        WIDTH = (int) context.getCanvas().getWidth();
        HEIGHT = (int) context.getCanvas().getHeight();

        /* Try importing image file */
        image = Main.IMPORTER.getImage(
                "start_background.png", Color.GREY).getImage();

        /* Try importing the Planewalker and Kaisho font */
        float fontSize = Math.min(WIDTH, HEIGHT) / 12;
        font = Main.IMPORTER.getFont("planewalker.otf",
                "kaisho.ttf", fontSize);
        String[] messages = {"Press any key to start",
                "Presione una tecla para empiezar",
                "Premere un tasto per iniziare",
                "Appuyez sur une touche pour démarrer",
                "Drücke zum Starten eine Taste",
                "任意のキーを押して開始"};
        message = Main.TRANSLATOR.getText(font, messages);

        /* Try importing music */
        audio = Main.IMPORTER.getAudio("start_background.mp3");
    }

    @Override
    public MenuEnum animateFrame(int framesToGo)
    {
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

        /* Draw update message's opacity */
        message.setOpacity(opacity);

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
        audio.play();
    }

    @Override
    public void stopMedia()
    {
        audio.stop();
    }

    @Override
    public void reset(Group group)
    {
        pressed = false;
        nextMenu = null;
        if (font != null) context.setFont(font.getFont());

        group.getChildren().remove(message);
    }

    @Override
    public void setup(Group group)
    {
        group.getChildren().add(message);

        /* Get text dimensions */
        font.setSample(message.getText());
        double textWidth = font.getWidth();
        double textHeight = font.getHeight();

        /* Calculate position of where the text will be animated */
        double textPosX = (WIDTH - textWidth) / 2;
        double textPosY = (HEIGHT - textHeight) / 4 * 3;

        message.setX(textPosX);
        message.setY(textPosY);
    }
}
