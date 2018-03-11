package Menus;

import Importer.FontResource;
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

class TopMenu implements Menu
{
    private final GraphicsContext context;

    private Image backgroundImage;

    /* 0 - top, 1 - middle, 2 - bottom */
    private int titleBoundaries[];
    private Font titleFonts[];

    private int fontSize;
    private int STUFFING;

    private JutWidget[] widgets;
    private String[][] widgetNames =
            {
                    {"Storytime", "Cuentos", "Favola", "Conte", "Märchenstunde", "寓話の時間"},
                    {"Versus", "Contra", "Contro", "Contre", "Gegen", "対"},
                    {"Options", "Opciones", "Opzioni", "Options", "Optionen", "オプション"},
                    {"Gallery", "Galería", "Galleria", "Galerie", "Galerie", "画廊"},
                    {"Quit", "Dimitir", "Smettere", "Quitter", "Verlassen", "やめる"}
            };
    private Image[] widgetImages;
    private String[] widgetImageNames =
            {"gunome", "midare", "notare", "sanbonsugi", "suguha"};

    private MenuEnum nextMenu;

    TopMenu(final GraphicsContext context)
    {
        this.context = context;

        final int WIDTH = (int) context.getCanvas().getWidth();
        final int HEIGHT = (int) context.getCanvas().getHeight();

        widgetImages = new Image[5];
        importImages();

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
        /* Clear canvas */
        context.clearRect(0, 0, context.getCanvas().getWidth(),
                context.getCanvas().getHeight());

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

        /* Temporary */
        if (code == KeyCode.SPACE)
        {
            nextMenu = MenuEnum.GAME;
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
    public Image getBackground()
    {
        return backgroundImage;
    }

    @Override
    public void startMedia()
    {}

    @Override
    public void stopMedia()
    {
        /* Music for this menu comes from StartMenu */
    }

    @Override
    public void reset(Group group) // TODO: if stuffs go on the group in this menu, remove them here
    {
        nextMenu = null;
        for (Widget widget : widgets)
        {
            widget.focused = false;
        }
    }

    @Override
    public void decorate(Group group)
    {

    }

    private void importImages()
    {
        /* Try importing background image file */
        InputStream input = getClass()
                .getResourceAsStream("/Images/top_background.png");
        if (input != null)
        {
            backgroundImage = new Image(input);
        }
        else
        {
            backgroundImage = null;
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
        fontSize = Math.min(WIDTH, HEIGHT) / 7;
        if (input == null)
        {
            titleFonts[2] = Font.font(fontSize);
            Print.red("\"scurlock.ttf\" was not imported");
        } else {
            titleFonts[2] = Font.loadFont(input, fontSize);
        }
        titleBoundaries[2] = STUFFING + (int) (fontSize / 1.5);


        /* Try importing the Supernatural Knight font file */
        input = getClass()
                .getResourceAsStream("/Fonts/supernatural_knight.ttf");
        if (input == null)
        {
            titleFonts[1] = Font.font(fontSize / 2.5);
            Print.red("\"supernatural.ttf\" was not imported");
        } else {
            titleFonts[1] = Font.loadFont(input, fontSize / 2.5);
        }
        titleBoundaries[1] = titleBoundaries[2] + (fontSize / 3) + STUFFING / 2;


        /* Try importing the Cardinal font file */
        input = getClass()
                .getResourceAsStream("/Fonts/cardinal.ttf");
        if (input == null)
        {
            titleFonts[0] = Font.font(fontSize / 1.2);
            Print.red("\"cardinal.ttf\" was not imported");
        } else {
            titleFonts[0] = Font.loadFont(input, fontSize / 1.2);
        }
        titleBoundaries[0] = titleBoundaries[1] + (int) (fontSize / 1.5) + STUFFING / 2;
    }

    private void setWidgets(int WIDTH, int HEIGHT)
    {
        /* Get the fonts */
        FontResource widgetFont = Main.IMPORTER.getFont(
                "planewalker.otf", "kaisho.ttf", fontSize / 3);

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

            /* TODO: will add more images for arming */
            Image[] images = {widgetImages[i]};

            /* The only aspect that differs is the yPos */
            aspects[1] += HEIGHT / 7;
            widgets[i] = new JutWidget(aspects, images, neighbors, context);
            /* Add widgets to Translator */
            Main.TRANSLATOR.addWidget(widgets[i], widgetFont, widgetNames[i]);
            /* Set their jut distance */
            widgets[i].setDistance(jutDistance);
        }
    }

    private class JutWidget extends Widget
    {
        private int startingPos;
        private int endingPos;

        JutWidget(int[] aspects, Image[] images, Widget[] neighbors,
                  GraphicsContext context)
        {
            super(aspects, images, neighbors, context);

            startingPos = (int) posX;
        }

        void animateFrame(int framesToGo)
        {
            super.animateFrame(framesToGo);

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

        void setDistance(int distance)
        {
            endingPos = startingPos - distance;
        }
    }
}
