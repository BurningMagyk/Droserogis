package Util;

import Menus.Main;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Translator
{
    private ArrayList<Assignment> buttonList;

    public Translator()
    {
        buttonList = new ArrayList<>();
    }

    public void translate()
    {
        int ID = Main.language.getID();
        for (Assignment assignment : buttonList)
        {
            Button button = (Button) assignment.object;
            String string = assignment.strings[ID];
            Font font = Main.language == LanguageEnum.WAPANESE
                    ? assignment.wapanese_font : assignment.font;

            button.setText(string);
            button.setFont(font);

            positionButton(button, assignment);
        }
    }

    public void clear()
    {
        buttonList.clear();
    }

    public Button getButton(Font[] fonts, String... strings)
    {
        int ID = Main.language.getID();

        String string = strings[ID];
        Button button = new Button(string);

        /* Use the other font if Wapanese */
        if (Main.language == LanguageEnum.WAPANESE) button.setFont(fonts[1]);
        else button.setFont(fonts[0]);

        Assignment assignment = new Assignment(button, fonts, strings);

        positionButton(button, assignment);

        buttonList.add(assignment);
        return button;
    }

    private class Assignment
    {
        Object object;
        Font font;
        Font wapanese_font;
        String[] strings;
        double[] width;
        double[] height;

        Assignment(Object object, Font[] fonts, String... strings)
        {
            this.object = object;
            font = fonts[0];
            wapanese_font = fonts[1];
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

    private void positionButton(Button button, Assignment assignment)
    {
        int ID = Main.language.getID();
        button.setPrefWidth(assignment.width[ID] * 2.0);
        button.setPrefHeight(assignment.height[ID] * 1.5);
    }
}
