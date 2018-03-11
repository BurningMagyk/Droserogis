package Importer;

import Menus.Main;
import Util.LanguageEnum;
import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.InputStream;

public class FontResource extends Resource
{
    private Font font;
    private Font fontAlt;
    private Text text = null;
    private Text textAlt = null;
    private GraphicsContext context;
    private boolean alt = Main.language == LanguageEnum.WAPANESE;

    FontResource(String path, double size, GraphicsContext context)
    {
        super(path);
        this.context = context;
        InputStream input = getClass().getResourceAsStream(path);
        if (input != null)
        {
            font = Font.loadFont(input, size);
        }
        else
        {
            font = Font.font(size);
            printFailure();
        }
        fontAlt = null;
    }

    FontResource(String path, double size, String pathAlt,
                 double sizeAlt, GraphicsContext context)
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

    public void setSample(String sample)
    {
        if (alt)
        {
            textAlt = new Text(sample);
            textAlt.setFont(getFont());
        }
        else
        {
            text = new Text(sample);
            text.setFont(getFont());
        }
    }

    public double getWidth()
    {
        return alt ? textAlt.getLayoutBounds().getWidth()
                : text.getLayoutBounds().getWidth();
    }

    public double getHeight()
    {
        return alt ? textAlt.getLayoutBounds().getHeight()
                : text.getLayoutBounds().getHeight();
    }

    public Font getFont()
    {
        return alt ? fontAlt : font;
    }

    public Font[] getFonts()
    {
        Font[] fonts = {font, fontAlt};
        return fonts;
    }

    public void switchFont(boolean alt)
    {
        this.alt = alt;

        Text sample = alt ? textAlt : text;
        if (sample != null)
        {
            sample.setFont(getFont());
        }
    }
}
