package Util;

import Importer.FontResource;
import Menus.Main;
import Menus.Widget;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.ArrayList;

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

    /**
     * In order to make a complete translate, change Main.language,
     * call translate(), and call reset(ROOT)and decorate(ROOT) for
     * menus with Text.
     */
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
            button.setText(assignment.strings[ID]);
            button.setFont(assignment.font.getFont());

            positionButton(button, assignment, ID);
        }

        for (Assignment assignment : widgetList)
        {
            Widget widget = (Widget) assignment.object;
            widget.setText(assignment.strings[ID]);
            widget.setFont(assignment.font.getFont());
        }

        for (Assignment assignment : textList)
        {
            Text text = (Text) assignment.object;
            text.setText(assignment.strings[ID]);
            text.setFont(assignment.font.getFont());
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
        FontResource font;
        String[] strings;
        double[] width;
        double[] height;

        Assignment(Object object, FontResource font, String... strings)
        {
            this.object = object;
            this.font = font;
            this.strings = strings;
            width = new double[strings.length];
            height = new double[strings.length];

            font.switchFont(false);
            for (int i = 0; i < strings.length; i++)
            {
                if (i == LanguageEnum.WAPANESE.getID())
                {
                    font.switchFont(true);
                }
                font.setSample(strings[i]);
                width[i] = font.getWidth();
                height[i] = font.getHeight();
            }
            font.switchFont(Main.language == LanguageEnum.WAPANESE);
        }
    }
}
