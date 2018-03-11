package Importer;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

import java.io.InputStream;

public class FontResource extends Resource
{
    private Font font;
    private Font fontAlt;
    private GraphicsContext context;
    private boolean alt = false;

    FontResource(String path, double size, GraphicsContext context)
    {
        super(path);
        this.context = context;
        InputStream input = getClass().getResourceAsStream(path);
        if (input != null) font = Font.loadFont(input, size);
        else
        {
            font = Font.font(size);
            printFailure();
        }
        fontAlt = null;
    }

    FontResource(String path, double size, String pathAlt, double sizeAlt, GraphicsContext context)
    {
        this(path, size, context);
        InputStream input = getClass().getResourceAsStream(pathAlt);
        if (input != null) fontAlt = Font.loadFont(input, size);
        else
        {
            fontAlt = Font.font(sizeAlt);
            Print.red("\"" + pathAlt + "\" was not imported");
        }
    }

    public void draw(double xPos, double yPos, String text)
    {
        context.setFont(alt ? fontAlt : font);
        context.fillText(text, xPos, yPos);
    }

    public Font getFont()
    {
        return alt ? fontAlt : font;
    }

    public void switchFont(boolean alt)
    {
        this.alt = alt;
    }
}
