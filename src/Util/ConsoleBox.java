package Util;


import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 5/1/2020
 */
public class ConsoleBox
{
    private boolean active = true;
    private final int MAX_LINES = 27;
    private int lineCount = 0;

    private final TextField entry;
    private final TextArea output;

    public ConsoleBox(int width, int height)
    {
        entry = new TextField();

        entry.setPrefWidth(width);
        entry.setTranslateX(0);
        float lineHeight = 30;
        entry.setPrefHeight(lineHeight);
        entry.setTranslateY(height - lineHeight);

        entry.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.BACK_QUOTE) toggleActive();
            else if (keyEvent.getCode() == KeyCode.ENTER) {
                enterCommand(entry.getText());
                print("> " + entry.getText());
                entry.setText("");
            }
        });


        entry.setVisible(false);

        output = new TextArea();

        output.setPrefWidth(width);
        output.setPrefHeight(height / 3F);
        output.setTranslateX(0);
        output.setTranslateY(0);

        output.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.BACK_QUOTE) toggleActive();
        });

        output.setVisible(false);

    }

    public void enterCommand(String command) { System.out.println(command); }

    public void print(String text)
    {
        if (lineCount < MAX_LINES) lineCount++;
        else
        {
            int idx = 0;
            String o = output.getText();
            while (o.charAt(idx) != '\n') { idx++; }
            output.setText(o.substring(idx + 1));
        }
        output.setText(output.getText() + "\n" + text);
    }

    public Node[] getNodes()
    {
        return new Node[]{entry, output};
    }

    public void toggleActive()
    {
        active = !active;
        entry.setVisible(active);
        output.setVisible(active);
        entry.setText("");
    }

    public boolean isActive() { return active; }
}
