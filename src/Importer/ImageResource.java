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
        //TODO: don't hard code this probably? and set it up to only do this with sprites
        if (input != null) image = new Image(input,35,70,false,false);
        else
        {
            image = null;
            printFailure();
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
