package Menus;

import Importer.AudioResource;
import Importer.FontResource;
import Importer.ImageResource;
import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class Widget
{
    double posX, posY, width, height;
    private int textX;
    private int textY;
    private String text;
    private FontResource font;

    private ImageResource images[];
    private ImageResource imageArm;
    private int imageIndex;
    private boolean armed;

    private AudioResource hoverSound, clickSound;

    /* 0 - north, 1 - east, 2 - south, 3 - west */
    private Widget[] neighbors;
    private Menu.MenuEnum nextMenu = null;

    private final GraphicsContext context;
    boolean focused;

    Widget(int[] aspects, String[] images,
           String imageArm, String[] sounds,
           final GraphicsContext context)
    {
        posX = aspects[0]; posY = aspects[1];
        width = aspects[2]; height = aspects[3];

        textX = -1;
        textY = -1;
        text = "ERROR";

        this.images = Main.IMPORTER.getImages(images);
        this.imageArm = Main.IMPORTER.getImage(imageArm);
        imageIndex = 0;
        armed = false;

        this.hoverSound = Main.IMPORTER.getAudio(sounds[0]);
        this.clickSound = Main.IMPORTER.getAudio(sounds[1]);

        this.context = context;
        focused = false;
    }

    /**
     * Call this from animateFrame() in a Menu
     */
    void animateFrame(int framesToGo)
    {
        /* Draw image */
        if (armed) imageArm.draw(posX, posY, width, height);
        else images[imageIndex].draw(posX, posY, width, height);

        /* Update Sprite */
        if (focused && !armed)
        {
            if (imageIndex < images.length - 1) imageIndex =+ framesToGo;
            if (imageIndex >= images.length) imageIndex = images.length - 1;
        }
        else
        {
            if (imageIndex > 0) imageIndex -= framesToGo;
            if (imageIndex < 0) imageIndex = 0;
        }

        /* Draw text */
        textX = (int) ((width - font.getFontSize()) / 3 + posX);
        textY = (int) ((height - font.getFontSize()) * 1.75 + posY);

        context.setFill(Color.RED);
        font.draw(textX, textY, text);
    }

    void setNeighbors(Widget[] neighbors)
    {
        this.neighbors = neighbors;
    }

    public void setText(String string) { this.text = string; }
    public void setFont(FontResource font) { this.font = font; }
    void setNextMenu(Menu.MenuEnum menuEnum) { nextMenu = menuEnum; }
    Menu.MenuEnum getNextMenu() { return nextMenu; }
    void disarm() { armed = false; }

    /**
     * Do something from the menu if it returns true
     * @param pressed - true if ENTER was pressed, false if released
     * @param code - which key was typed
     * @return - Returns itself if nothing happens,
     *           returns a neighbor if focus has changed,
     *           returns null if enter was pressed
     */
    Widget key(boolean pressed, KeyCode code)
    {
        if (!pressed || !focused) return this;

        /* For the focus to shift to a neighboring widget, the current
         * widget needs to lose its focus */
        focused = false;

        if (code == KeyCode.LEFT) return neighbors[3].select(this);
        if (code == KeyCode.RIGHT) return neighbors[1].select(this);
        if (code == KeyCode.UP) return neighbors[0].select(this);
        if (code == KeyCode.DOWN) return neighbors[2].select(this);

        if (code == KeyCode.ENTER)
        {
            clickSound.play();

            armed = true;
            return this.select(this);
        }

        return this.select(this);
    }

    /**
     * So that when the keys select a widget while the mouse is idle,
     * the selected widget will remain selected until the mouse moves.
     *
     * Parameter should always be "this" if from a widget, or "null"
     * if from a menu.
     */
    Widget select(Widget widget)
    {
        /* Don't play sound if this widget selected itself */
        if (widget != this)
        {
            hoverSound.play();
        }

        focused = true;
        return this;
    }

    /**
     * Do something from the menu if it returns true
     * @param pressed - true if button was pressed, false if released
     * @param button - primary, middle, or secondary
     * @return - true means the widget was clicked on, false means it wasn't
     */
    boolean mouse(boolean pressed, MouseButton button, int x, int y)
    {
        if (pressed && button == MouseButton.PRIMARY && inBounds(x, y))
        {
            clickSound.play();

            armed = true;
            return true;
        }
        return false;
    }
    
    /**
     * Call this from mouse(x, y) in a Menu
     * @return - true if within the rectangle, false otherwise
     */
    boolean hover(int x, int y)
    {
        if (inBounds(x, y))
        {
            if (!focused)
            {
                hoverSound.play();
            }

            focused = true;
            return true;
        }

        focused = false;
        return false;

        /* Make the menu set this widget as selected
           and the others set as unselected  */
    }

    private boolean inBounds(int x, int y) {
        return !(x < posX) && !(x > posX + width)
                && !(y < posY) && !(y > posY + height);
    }
}
