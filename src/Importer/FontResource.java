/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

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
    private double fontSize;
    private double fontSizeAlt;

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
        fontSize = size;
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
        fontSizeAlt = sizeAlt;
    }

    public void draw(double xPos, double yPos, String text)
    {
        context.setFont(isAlt() ? fontAlt : font);
        context.fillText(text, xPos, yPos);
    }

    public void setSample(String sample)
    {
        if (fontAlt != null)
        {
            textAlt = new Text(sample);
            textAlt.setFont(fontAlt);
        }
        text = new Text(sample);
        text.setFont(font);
    }

    public double getWidth()
    {
        return isAlt() ? textAlt.getLayoutBounds().getWidth()
                : text.getLayoutBounds().getWidth();
    }

    public double getHeight()
    {
        return isAlt() ? textAlt.getLayoutBounds().getHeight()
                : text.getLayoutBounds().getHeight();
    }

    public Font getFont()
    {
        return isAlt() ? fontAlt : font;
    }

    public Font[] getFonts()
    {
        return new Font[] {font, fontAlt};
    }

    public double getFontSize()
    {
        return isAlt() ? fontSizeAlt : fontSize;
    }

    public void switchFont(boolean alt)
    {
        this.alt = alt;

        Text sample = isAlt() ? textAlt : text;
        if (sample != null)
        {
            sample.setFont(getFont());
        }
    }

    private boolean isAlt() { return alt && fontAlt != null; }
}
