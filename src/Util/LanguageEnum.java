package Util;

import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Created by Robin on 2/28/2017.
 */
public enum LanguageEnum
{
    ENGLISH
            {
                public String toString() { return "English"; }
            },
    SPANISH
            {
                public String toString() { return "Español"; }
            },
    ITALIAN
            {
                public String toString() { return "Italiano"; }
            },
    FRENCH
            {
                public String toString() { return "Français"; }
            },
    GERMAN
            {
                public String toString() { return "Deutsche"; }
            },
    WAPANESE
            {
                public String toString() { return "日本語"; }
            };

    public String toString()
    {
        return "Error";
    }
    public Image getFlag()
    {
        String path = "/Images/FLAG_" + super.toString() + ".png";
        InputStream input = getClass()
                .getResourceAsStream(path);
        if (input == null)
        {
            Print.red("\"" + path + "\" was not imported");
            return null;
        }
        return new Image(input);
    }
}