/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Importer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

public class ImageResource extends Resource
{
    private final Image image;
    private GraphicsContext context;
    private final Color backup;

    ImageResource(String path, GraphicsContext context, Color backup)
    {
        super(path);
        this.context = context;

        InputStream input = getClass().getResourceAsStream(path);
        if (input != null) image = new Image(input);
        else
        {
            input = getClass().getResourceAsStream(setUncontrolled());
            if (input != null) image = new Image(input);
            else
            {
                image = null;
                printFailure();
            }
        }

        this.backup = backup;
    }

    public void draw(double xPos, double yPos, double width, double height)
    {
        if (image == null)
        {
            context.setFill(backup);
            context.fillRect(xPos, yPos, width, height);
        }
        else context.drawImage(image, xPos, yPos, width, height);
    }

    public Image getImage()
    {
        return image;
    }

    public double getWidth()
    {
        if (image == null) return -1;
        return image.getWidth();
    }
    public double getHeight()
    {
        if (image == null) return -1;
        return image.getHeight();
    }
}
