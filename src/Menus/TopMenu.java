package Menus;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.InputStream;

public class TopMenu implements Menu
{
    private final GraphicsContext context;

    private final double[] imageAspects;
    private final Image image;

    private final int fontBoundaryTop;
    private final int fontBoundaryMiddle;
    private final int fontBoundaryBottom;
    private final Font fontTop;
    private final Font fontMiddle;
    private final Font fontBottom;
    private final int FONTSIZE;
    private final int STUFFING;

    TopMenu(GraphicsContext context, int WIDTH, int HEIGHT)
    {
        this.context = context;
        imageAspects = new double[4];

        /* Try importing image file */
        InputStream input = getClass()
                .getResourceAsStream("/Images/top_background.png");
        if (input != null)
        {
            /* Having it as an ImageView allows it to to be modified */
            image = new Image(input);

            /* Calculate the position of where the image goes
             * This centers the window onto the image */
            double sizeScale = WIDTH / image.getWidth();
            imageAspects[0] = 0;
            imageAspects[1] = (HEIGHT - image.getHeight() * sizeScale) / 5 * 4;
            imageAspects[2] = image.getWidth() * sizeScale;
            imageAspects[3] = image.getHeight() * sizeScale;
        }
        else
        {
            image = null;
            imageAspects[0] = 0;
            imageAspects[1] = 0;
            imageAspects[2] = 0;
            imageAspects[3] = 0;
            Print.red("\"top_background.png\" was not imported");
        }

        /* The spacing between the 3 lines */
        STUFFING = Math.min(WIDTH, HEIGHT) / 20;

        /* Try importing the Scurlock font file */
        input = getClass()
                .getResourceAsStream("/Fonts/scurlock.ttf");
        if (input == null) Print.red("\"scurlock.ttf\" was not imported");
        FONTSIZE = Math.min(WIDTH, HEIGHT) / 7;
        fontBottom = Font.loadFont(input, FONTSIZE);
        fontBoundaryBottom = STUFFING + (int) (FONTSIZE / 1.5);


        /* Try importing the Supernatural Knight font file */
        input = getClass()
                .getResourceAsStream("/Fonts/supernatural_knight.ttf");
        if (input == null) Print.red("\"supernatural.ttf\" was not imported");
        fontMiddle = Font.loadFont(input, FONTSIZE / 2.5);
        fontBoundaryMiddle = fontBoundaryBottom + (FONTSIZE / 3) + STUFFING / 2;


        /* Try importing the Cardinal font file */
        input = getClass()
                .getResourceAsStream("/Fonts/cardinal.ttf");
        if (input == null) Print.red("\"cardinal.ttf\" was not imported");
        fontTop = Font.loadFont(input, FONTSIZE / 1.2);
        fontBoundaryTop = fontBoundaryMiddle + (int) (FONTSIZE / 1.5) + STUFFING / 2;
    }

    @Override
    public Menu animateFrame()
    {
        if (image != null)
        {
            context.drawImage(image,
                    imageAspects[0],
                    imageAspects[1],
                    imageAspects[2],
                    imageAspects[3]);
        }

        if (fontBottom != null) context.setFont(fontBottom);
        context.setFill(Color.DARKBLUE);
        context.fillText("Droserogis",
                STUFFING, fontBoundaryBottom);

        if (fontMiddle != null) context.setFont(fontMiddle);
        context.setFill(Color.BLACK);
        context.fillText("VS",
                FONTSIZE * 1.8 + STUFFING, fontBoundaryMiddle);

        if (fontTop != null) context.setFont(fontTop);
        context.setFill(Color.PURPLE);
        context.fillText("Sothli",
                FONTSIZE * 1.25 + STUFFING, fontBoundaryTop);

        return null;
    }

    @Override
    public MenuEnum getMenuType()
    {
        return MenuEnum.TOP;
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        if (code == KeyCode.ESCAPE) System.exit(0);
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }
}
