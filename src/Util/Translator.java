package Util;

import Importer.FontResource;
import Menus.Main;
import Menus.Widget;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class Translator
{
    private ArrayList<Assignment> buttonList;
    private ArrayList<Assignment> widgetList;
    private ArrayList<Assignment> textList;

    private ArrayList<FontResource> fontList;

    public Translator()
    {
        buttonList = new ArrayList<>();
        widgetList = new ArrayList<>();
        textList = new ArrayList<>();

        fontList = new ArrayList<>();
    }

    public void translate()
    {
        int ID = Main.language.getID();

        for (FontResource font : fontList)
        {
            font.switchFont(ID == LanguageEnum.WAPANESE.getID());
        }

        for (Assignment assignment : buttonList)
        {
            Button button = (Button) assignment.object;
            String string = assignment.strings[ID];
            Font font = Main.language == LanguageEnum.WAPANESE
                    ? assignment.wapanese_font : assignment.font;

            button.setText(string);
            button.setFont(font);

            positionButton(button, assignment, ID);
        }

        for (Assignment assignment : widgetList)
        {
            Widget widget = (Widget) assignment.object;
            String string = assignment.strings[ID];
            Font font = Main.language == LanguageEnum.WAPANESE
                    ? assignment.wapanese_font : assignment.font;

            widget.setText(string);
            widget.setFont(font);
        }

        for (Assignment assignment : textList)
        {
            Text text = (Text) assignment.object;
            String string = assignment.strings[ID];
            Font font = Main.language == LanguageEnum.WAPANESE
                    ? assignment.wapanese_font : assignment.font;

            text.setText(string);
            text.setFont(font);
        }
    }

    private void positionButton(Button button, Assignment assignment, int langID)
    {
        button.setPrefWidth(assignment.width[langID] * 2.0);
        button.setPrefHeight(assignment.height[langID] * 1.5);
    }

    public void clear()
    {
        buttonList.clear();
        widgetList.clear();
        textList.clear();
    }

    public Button getButton(FontResource font, String... strings)
    {
        int ID = Main.language.getID();
        Button button = new Button(strings[ID]);

        /* Use the other font if Wapanese */
        if (Main.language == LanguageEnum.WAPANESE)
            button.setFont(font.getFonts()[1]);
        else button.setFont(font.getFonts()[0]);

        Assignment assignment = new Assignment(button, font, strings);

        positionButton(button, assignment, ID);

        buttonList.add(assignment);
        fontList.add(font);
        return button;
    }

    public void addWidget(Widget widget, FontResource font, String... strings)
    {
        int ID = Main.language.getID();
        widget.setText(strings[ID]);

        /* Use the other font if Wapanese */
        if (Main.language == LanguageEnum.WAPANESE)
            widget.setFont(font.getFonts()[1]);
        else widget.setFont(font.getFonts()[0]);

        Assignment assignment = new Assignment(widget, font, strings);

        widgetList.add(assignment);
        fontList.add(font);
    }

    public Text getText(FontResource font, String... strings)
    {
        int ID = Main.language.getID();
        Text text = new Text(strings[ID]);
        text.setFont(font.getFont());
        font.setSample(strings[ID]);

        /* Use the other font if Wapanese */
        if (Main.language == LanguageEnum.WAPANESE)
            font.switchFont(true);
        else font.switchFont(false);

        Assignment assignment = new Assignment(
                text, font, strings);

        textList.add(assignment);
        fontList.add(font);
        return text;
    }

    private class Assignment
    {
        Object object;
        Font font;
        Font wapanese_font;
        String[] strings;
        double[] width;
        double[] height;

        Assignment(Object object, FontResource fonts, String... strings)
        {
            this.object = object;

            font = fonts.getFonts()[0];
            wapanese_font = fonts.getFonts()[1];
            this.strings = strings;
            width = new double[strings.length];
            height = new double[strings.length];

            Text text = new Text();
            text.setFont(font);
            for (int i = 0; i < strings.length; i++)
            {
                if (i == LanguageEnum.WAPANESE.getID())
                {
                    text.setFont(wapanese_font);
                }
                text.setText(strings[i]);
                width[i] = text.getLayoutBounds().getWidth();
                height[i] = text.getLayoutBounds().getHeight();
            }
        }
    }
}
