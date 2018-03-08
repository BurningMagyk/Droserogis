package Menus;

import Util.LanguageEnum;
import Util.Print;
import javafx.application.Platform;
import javafx.scene.Group;
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
    private Image backgroundImage;

    /* 0 - top, 1 - middle, 2 - bottom */
    private int titleBoundaries[];
    private Font titleFonts[];
    private Font widgetFont;

    private int fontSize;
    private int STUFFING;

    private JutWidget[] widgets;
    /* Translator class not needed here */
    private String[][] widgetNames =
            {
                    {"Storytime", "Versus", "Options", "Gallery", "Quit"},
                    {"Cuentos", "Contra", "Opciones", "Galería", "Dimitir"},
                    {"Favola", "Contro", "Opzioni", "Galleria", "Smettere"},
                    {"Conte", "Contre", "Options", "Galerie", "Quitter"},
                    {"Märchenstunde", "Gegen", "Optionen", "Galerie", "Verlassen"},
                    {"寓話の時間", "対", "オプション", "画廊", "やめる"}
            };
    private Image[] widgetImages;
    private String[] widgetImageNames =
            {"gunome", "midare", "notare", "sanbonsugi", "suguha"};

    private MenuEnum nextMenu;

    TopMenu(final Group group, final GraphicsContext context)
    {
        this.context = context;
        imageAspects = new double[4];

        final int WIDTH = (int) context.getCanvas().getWidth();
        final int HEIGHT = (int) context.getCanvas().getHeight();

        widgetImages = new Image[5];
        importImages(WIDTH, HEIGHT);

        titleBoundaries = new int[3];
        titleFonts = new Font[3];
        importFonts(WIDTH, HEIGHT);

        widgets = new JutWidget[5];
        setWidgets(WIDTH, HEIGHT);

        nextMenu = null;
    }

    @Override
    public MenuEnum animateFrame(int framesToGo)
    {
        /* Background */
        if (backgroundImage != null)
        {
            context.drawImage(backgroundImage,
                    imageAspects[0],
                    imageAspects[1],
                    imageAspects[2],
                    imageAspects[3]);
        }

        /* Title */
        if (titleFonts[2] != null) context.setFont(titleFonts[2]);
        context.setFill(Color.DARKBLUE);
        context.fillText("Droserogis",
                STUFFING, titleBoundaries[2]);

        if (titleFonts[1] != null) context.setFont(titleFonts[1]);
        context.setFill(Color.BLACK);
        context.fillText("VS",
                fontSize * 1.8 + STUFFING, titleBoundaries[1]);

        if (titleFonts[0] != null) context.setFont(titleFonts[0]);
        context.setFill(Color.PURPLE);
        context.fillText("Sothli",
                fontSize * 1.25 + STUFFING, titleBoundaries[0]);

        /* Widgets */
        context.setFont(widgetFont);
        for (JutWidget widget : widgets)
        {
            widget.animateFrame(framesToGo);
        }

        return nextMenu;
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        if (code == KeyCode.ESCAPE)
        {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y)
    {
        if (widgets[0].mouse(pressed, button, x, y))
            nextMenu = MenuEnum.STORYTIME;
        else if (widgets[1].mouse(pressed, button, x, y))
            nextMenu = MenuEnum.VERSUS;
        else if (widgets[2].mouse(pressed, button, x, y))
            nextMenu = MenuEnum.OPTIONS;
        else if (widgets[3].mouse(pressed, button, x, y))
            nextMenu = MenuEnum.GALLERY;
        else if (widgets[4].mouse(pressed, button, x, y))
            nextMenu = MenuEnum.QUIT;
    }

    @Override
    public void mouse(int x, int y)
    {
        for (Widget widget : widgets)
        {
            widget.hover(x, y);
        }
    }

    @Override
    public void stopMusic()
    {
        /* Music for this menu comes from StartMenu */
    }

    @Override
    public void reset()
    {
        nextMenu = null;
        for (Widget widget : widgets)
        {
            widget.focused = false;
        }
    }

    private void importImages(int WIDTH, int HEIGHT)
    {
        /* Try importing background image file */
        InputStream input = getClass()
                .getResourceAsStream("/Images/top_background.png");
        if (input != null)
        {
            backgroundImage = new Image(input);

            /* Calculate the position of where the image goes
             * This centers the window onto the image */
            double sizeScale = WIDTH / backgroundImage.getWidth();
            imageAspects[0] = 0;
            imageAspects[1] = (HEIGHT - backgroundImage.getHeight() * sizeScale) / 5 * 4;
            imageAspects[2] = backgroundImage.getWidth() * sizeScale;
            imageAspects[3] = backgroundImage.getHeight() * sizeScale;
        }
        else
        {
            backgroundImage = null;
            for (int i = 0; i < imageAspects.length; i++)
                imageAspects[i] = 0;
            Print.red("\"top_background.png\" was not imported");
        }

        /* Try importing widget image files */
        for (int i = 0; i < widgetImageNames.length; i++)
        {
            input = getClass().getResourceAsStream("/Images/katana_"
                    + widgetImageNames[i] + ".png");
            if (input != null)
                widgetImages[i] = new Image(input);
            else {
                widgetImages[i] = null;
                Print.red("\"katana_" + widgetImageNames[i]
                        + ".png\" was not imported");
            }
        }
    }

    private void importFonts(int WIDTH, int HEIGHT)
    {
        /* The spacing between the 3 lines */
        STUFFING = Math.min(WIDTH, HEIGHT) / 20;

        /* Try importing the Scurlock font file */
        InputStream input = getClass()
                .getResourceAsStream("/Fonts/scurlock.ttf");
        if (input == null) Print.red("\"scurlock.ttf\" was not imported");
        fontSize = Math.min(WIDTH, HEIGHT) / 7;
        titleFonts[2] = Font.loadFont(input, fontSize);
        titleBoundaries[2] = STUFFING + (int) (fontSize / 1.5);


        /* Try importing the Supernatural Knight font file */
        input = getClass()
                .getResourceAsStream("/Fonts/supernatural_knight.ttf");
        if (input == null) Print.red("\"supernatural.ttf\" was not imported");
        titleFonts[1] = Font.loadFont(input, fontSize / 2.5);
        titleBoundaries[1] = titleBoundaries[2] + (fontSize / 3) + STUFFING / 2;


        /* Try importing the Cardinal font file */
        input = getClass()
                .getResourceAsStream("/Fonts/cardinal.ttf");
        if (input == null) Print.red("\"cardinal.ttf\" was not imported");
        titleFonts[0] = Font.loadFont(input, fontSize / 1.2);
        titleBoundaries[0] = titleBoundaries[1] + (int) (fontSize / 1.5) + STUFFING / 2;
    }

    private void setWidgets(int WIDTH, int HEIGHT)
    {
        /* Get the fonts */
        InputStream input = getClass().getResourceAsStream(
                "/Fonts/planewalker.otf");
        InputStream input_wapanese = getClass().getResourceAsStream(
                "/Fonts/kaisho.ttf");
        widgetFont = Main.language == LanguageEnum.WAPANESE
                ? Font.loadFont(input_wapanese, fontSize / 3)
                : Font.loadFont(input, fontSize / 3);

        /* Set up widgets */
        int aspects[] = new int[4];
        for (int i = 1; i < aspects.length; i++)
            aspects[i] = HEIGHT / 20;
        /* Using aspects[1] to get the initial spacing value */
        int jutDistance = aspects[1] * 4;
        /* width */
        aspects[2] *= 15;
        /* height */
        aspects[3] *= 1.5;
        /* xPos */
        aspects[0] = WIDTH - aspects[2] + jutDistance;
        /* top yPos */
        aspects[1] *= 3;

        for (int i = 0; i < widgets.length; i++)
        {
            /* Set neighbors */
            Widget neighbors[] = new Widget[4];
            neighbors[1] = widgets[i];
            neighbors[3] = widgets[i];
            int northIndex = i == 0 ? widgets.length - 1 : i - 1;
            neighbors[0] = widgets[northIndex];
            int southIndex = i == widgets.length - 1 ? 0 : i + 1;
            neighbors[2] = widgets[southIndex];

            Image[] images = {widgetImages[i]};

            /* The only aspect that differs is the yPos */
            aspects[1] += HEIGHT / 7;
            widgets[i] = new JutWidget(aspects, images, neighbors, context);
            /* Set their jut distance */
            widgets[i].setDistance(jutDistance);
            /* Give them names */
            widgets[i].setText(widgetNames[Main.language.getID()][i]);
        }
    }

    private class JutWidget extends Widget
    {
        private int startingPos;
        private int endingPos;

        private int textX;
        private int textY;
        private String text;

        JutWidget(int[] aspects, Image[] images, Widget[] neighbors,
                  GraphicsContext context)
        {
            super(aspects, images, neighbors, context);

            startingPos = (int) posX;

            textX = -1;
            textY = -1;
            text = null;
        }

        void animateFrame(int framesToGo)
        {
            super.animateFrame(framesToGo);

            /* Draw text */
            textX = (int) ((width - fontSize) / 3 + posX);
            textY = (int) ((height - fontSize / 2.9) * 2 + posY);

            context.setFill(Color.RED);
            context.fillText(text,
                    textX, textY);

            double speed;
            /* Move the widget */
            if (focused)
            {
                if (posX > endingPos)
                {
                    speed = Math.max((posX - endingPos) / 7, 3);
                    posX -= speed * framesToGo;
                }
            }
            else if (posX < startingPos)
            {
                speed = Math.max((startingPos - posX) / 7, 3);
                posX += speed * framesToGo;
            }

            /* Restore if out of bounds */
            if (posX < endingPos) posX = endingPos;
            else if (posX > startingPos) posX = startingPos;
        }

        void setText(String string)
        {
            this.text = string;
        }

        void setDistance(int distance)
        {
            endingPos = startingPos - distance;
        }
    }
}
