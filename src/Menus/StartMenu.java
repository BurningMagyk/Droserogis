package Menus;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.InputStream;

public class StartMenu implements Menu
{
    private final GraphicsContext context;

    private final double imagePosX;
    private final double imagePosY;
    private final double imageWidth;
    private final double imageHeight;

    private final double textPosX;
    private final double textPosY;

    private final Image image;
    private final String message;

    private double opacity;
    private boolean fading;

    private Menu nextMenu;
    private boolean goToNextMenu;

    StartMenu(final GraphicsContext context,
              final int WIDTH, final int HEIGHT, Menu menu)
    {
        this.context = context;
        message = "Press any key to start";
        opacity = 1.0;
        fading = true;
        nextMenu = menu;
        goToNextMenu = false;

        /* Try importing image file */
        InputStream input = getClass()
                .getResourceAsStream("/Images/start_background.png");
        if (input != null)
        {
            /* Having it as an ImageView allows it to to be modified */
            image = new Image(input);

            /* Calculate the position of where the image goes
             * This centers the window onto the image */
            double sizeScale = HEIGHT / image.getHeight();
            imagePosX = (WIDTH - image.getWidth() * sizeScale) / 2;
            imagePosY = 0;
            imageWidth = image.getWidth() * sizeScale;
            imageHeight = image.getHeight() * sizeScale;
        }
        else
        {
            image = null;
            imagePosX = 0;
            imagePosY = 0;
            imageWidth = 0;
            imageHeight = 0;
            Print.red("\"start_background.png\" was not imported");
        }

        /* Prepare text dimensions, set to default values */
        double textWidth = WIDTH / 2;
        double textHeight = HEIGHT / 6;

        /* Try importing the Supernatural_Knight font */
        input = getClass()
                .getResourceAsStream("/Fonts/supernatural_knight.ttf");
        if (input == null) Print.red("\"supernatural_knight.ttf\" was not imported");
        else
        {
            Font font = Font.loadFont(input, Math.min(WIDTH, HEIGHT) / 10);
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
        textPosY = (HEIGHT - textHeight) / 5 * 4;
    }

    @Override
    public Menu animateFrame()
    {
        if (image != null)
        {
            context.drawImage(image,
                imagePosX,
                0,
                imageWidth,
                imageHeight);
        }

        if (fading) opacity -= 0.02;
        else opacity += 0.02;

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

        context.setFill(Color.rgb(0,0,0, opacity));
        context.fillText(message,
                textPosX,
                textPosY);

        if (goToNextMenu) return nextMenu;
        else return null;
    }

    @Override
    public MenuEnum getMenuType()
    {
        return MenuEnum.START;
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        goToNextMenu = true;
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y)
    {
        goToNextMenu = true;
    }

    @Override
    public void mouse(int x, int y)
    {
        /* Does nothing */
    }
}
