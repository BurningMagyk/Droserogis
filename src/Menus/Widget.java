package Menus;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

class Widget
{
    double posX, posY, width, height;

    Image images[];
    int imageIndex;

    /* 0 - north, 1 - east, 2 - south, 3 - west */
    Widget[] neighbors;

    final GraphicsContext context;
    boolean focused;

    Widget(int[] aspects, Image[] images, Widget[] neighbors,
           final GraphicsContext context)
    {
        posX = aspects[0]; posY = aspects[1];
        width = aspects[2]; height = aspects[3];

        this.images = images;
        imageIndex = 0;

        this.neighbors = neighbors;

        this.context = context;
        focused = false;
    }

    /**
     * Call this from animateFrame() in a Menu
     */
    void animateFrame(int framesToGo)
    {
        /* Draw image */
        if (images[imageIndex] != null)
        {
            context.drawImage(images[imageIndex],
                posX, posY, width, height);
        } else {
            context.setFill(Color.GREEN);
            context.fillRect(posX, posY, width, height);
        }

        /* Update sprite */
        if (focused)
        {
            if (imageIndex < images.length - 1) imageIndex =+ framesToGo;
            if (imageIndex >= images.length) imageIndex = images.length - 1;
        }
        else
        {
            if (imageIndex > 0) imageIndex -= framesToGo;
            if (imageIndex < 0) imageIndex = 0;
        }
    }

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
        /* TODO: Make it animate a different set of images when armed */
        if (!pressed) return this;
        if (!focused) return this;

        if (code == KeyCode.LEFT) return neighbors[3];
        if (code == KeyCode.RIGHT) return neighbors[1];
        if (code == KeyCode.UP) return neighbors[0];
        if (code == KeyCode.DOWN) return neighbors[2];

        if (code != KeyCode.ENTER) return this;

        return null;
    }

    /**
     * Do something from the menu if it returns true
     * @param pressed - true if button was pressed, false if released
     * @param button - primary, middle, or secondary
     * @return - true means the widget was clicked on, false means it wasn't
     */
    boolean mouse(boolean pressed, MouseButton button, int x, int y)
    {
        if (!pressed) return false;
        if (button != MouseButton.PRIMARY) return false;
        if (!inBounds(x, y)) return false;

        return true;
    }
    
    /**
     * Call this from mouse(x, y) in a Menu
     * @return - true if within the rectangle, false otherwise
     */
    boolean hover(int x, int y)
    {
        if (inBounds(x, y))
        {
            focused = true;
            return true;
        }

        focused = false;
        return false;

        /* Make the menu set this widget as selected
           and the others set as unselected  */
    }

    private boolean inBounds(int x, int y)
    {
        if (x < posX) return false;
        if (x > posX + width) return false;
        if (y < posY) return false;
        if (y > posY + height) return false;

        return true;
    }
}
