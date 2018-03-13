package Menus;

import Importer.FontResource;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

class TopMenu implements Menu
{
    private final GraphicsContext context;

    private Image backgroundImage;

    /* 0 - top, 1 - middle, 2 - bottom */
    private int titleBoundaries[];

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
    private String[] widgetImageNames =
            {"gunome", "midare", "notare", "sanbonsugi", "suguha"};
    private String[] widgetSounds = {"sword.mp3", "sword1.mp3"};
    private Widget selectedWidget;

    /* Frames until action when a widget becomes armed */
    private int armCountdown = 0;

    private FontResource title[];

    private MenuEnum nextMenu;

    TopMenu(final GraphicsContext context)
    {
        this.context = context;

        final int WIDTH = (int) context.getCanvas().getWidth();
        final int HEIGHT = (int) context.getCanvas().getHeight();

        importImages();

        titleBoundaries = new int[3];
        importFonts(WIDTH, HEIGHT);

        widgets = new JutWidget[5];
        setWidgets(WIDTH, HEIGHT);

        nextMenu = null;
    }

    @Override
    public MenuEnum animateFrame(int framesToGo)
    {
        clearContext();

        /* Title */
        context.setFill(Color.DARKBLUE);
        title[2].draw(STUFFING, titleBoundaries[0], "Droserogis");
        context.setFill(Color.BLACK);
        title[1].draw(fontSize * 1.8 + STUFFING,
                titleBoundaries[1], "VS");
        context.setFill(Color.PURPLE);
        title[0].draw(fontSize * 1.25 + STUFFING,
                titleBoundaries[2], "Sothli");

        /* Widgets */
        for (JutWidget widget : widgets)
        {
            widget.animateFrame(framesToGo);
        }

        /* Countdown to act after widget has been armed */
        if (armCountdown > 0)
        {
            armCountdown--;
            return null;
        }

        return nextMenu;
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        switch (code)
        {
            case ESCAPE:
                Platform.exit();
                System.exit(0);
                break;
            case SPACE: // temporary
                nextMenu = MenuEnum.GAME;
                break;
            case ENTER:
                armCountdown = ARM_COUNTDOWN;
                nextMenu = selectedWidget.getNextMenu();
            default:
                /* If no widget is currently selected, select
                 * the top one */
                if (selectedWidget == null) selectedWidget
                        = widgets[0].select(null);
                else selectedWidget
                        = selectedWidget.key(pressed, code);
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y)
    {
        for (JutWidget widget : widgets)
        {
            if (widget.mouse(pressed, button, x, y))
            {
                nextMenu = widget.getNextMenu();
                armCountdown = ARM_COUNTDOWN;
                return;
            }
        }
    }

    @Override
    public void mouse(int x, int y)
    {
        /* Any widget previously selected should be unselected unless
         * the moved mouse is still in bounds of that widget */
        selectedWidget = null;
        for (Widget widget : widgets)
        {
            /* Keeping track of which widget is currently selected lets
             * us know what widget should arm if the user presses ENTER */
            if (widget.hover(x, y)) selectedWidget = widget;
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
    public void reset(Group group)
    {
        nextMenu = null;
        selectedWidget = null;
        for (Widget widget : widgets)
        {
            widget.focused = false;
            widget.disarm();
        }

        clearContext();
    }

    @Override
    public void decorate(Group group)
    {
        armCountdown = 0;
    }

    private void importImages()
    {
        /* Try importing background image file */
        backgroundImage = Main.IMPORTER.getImage(
                "top_background.png").getImage();
    }

    private void clearContext()
    {
        /* Clear canvas */
        context.clearRect(0, 0, context.getCanvas().getWidth(),
                context.getCanvas().getHeight());
    }

    private void importFonts(int WIDTH, int HEIGHT)
    {
        /* The spacing between the 3 lines */
        STUFFING = Math.min(WIDTH, HEIGHT) / 20;
        fontSize = Math.min(WIDTH, HEIGHT) / 7;
        titleBoundaries[2] = STUFFING + (int) (fontSize / 1.5);
        titleBoundaries[1] = titleBoundaries[2]
                + (int) (fontSize / 2.5) + STUFFING / 2;
        titleBoundaries[0] = titleBoundaries[1]
                + (int) (fontSize / 1.5) + STUFFING / 2;

        title = new FontResource[3];
        title[2] = Main.IMPORTER.getFont("scurlock.ttf", fontSize);
        title[1] = Main.IMPORTER.getFont("supernatural_knight.ttf",
                fontSize / 2.5);
        title[0] = Main.IMPORTER.getFont("cardinal.ttf",
                fontSize / 1.2);
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
            /* Variable is called "images",
             * but it's just one image in the array */
            String[] images = {"katana_" + widgetImageNames[i] + ".png"};
            String imageArm = "katana_" + widgetImageNames[i] + "_tint.png";

            /* The only aspect that differs is the yPos */
            aspects[1] += HEIGHT / 7;
            widgets[i] = new JutWidget(aspects, images, imageArm,
                    widgetSounds, context);
            /* Add widgets to Translator */
            Main.TRANSLATOR.addWidget(widgets[i], widgetFont, widgetNames[i]);
            /* Set their jut distance */
            widgets[i].setDistance(jutDistance);
        }

        /* Neighbors need to be set after all widgets have been initialized */
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

            widgets[i].setNeighbors(neighbors);
        }

        widgets[0].setNextMenu(MenuEnum.STORYTIME);
        widgets[1].setNextMenu(MenuEnum.VERSUS);
        widgets[2].setNextMenu(MenuEnum.OPTIONS);
        widgets[3].setNextMenu(MenuEnum.GALLERY);
        widgets[4].setNextMenu(MenuEnum.QUIT);
    }

    private class JutWidget extends Widget
    {
        private int startingPos;
        private int endingPos;

        JutWidget(int[] aspects, String[] images,
                  String imageArm, String[] sounds,
                  GraphicsContext context)
        {
            super(aspects, images, imageArm, sounds, context);

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
