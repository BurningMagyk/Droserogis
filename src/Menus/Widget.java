package Menus;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

class Widget
{
    int posX, posY, width, height;

    Image images[];
    int imageIndex;

    final GraphicsContext context;
    boolean goingForward;

    Widget(int[] aspects, Image[] images, final GraphicsContext context)
    {
        posX = aspects[0]; posY = aspects[1];
        width = aspects[2]; height = aspects[3];

        this.images = images;
        imageIndex = 0;

        this.context = context;
        goingForward = true;
    }

    /**
     * Does not need to be called if it's just using one image
     */
    public void animateFrame()
    {
        /* Draw image */
        context.drawImage(images[imageIndex],
                posX, posY, width, height);

        /* Update sprite */
        if (goingForward = true)
        {
            if (imageIndex < images.length - 1) imageIndex++;
        }
        else if (imageIndex > 0) imageIndex--;
    }

    void key(boolean pressed, KeyCode code) {

    }

    void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    boolean hover(int x, int y)
    {
        if (inBounds(x, y))
        {
            goingForward = true;
            return true;
        }

        goingForward = false;
        return false;
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
