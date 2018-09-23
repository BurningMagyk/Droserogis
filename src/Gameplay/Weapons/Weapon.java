package Gameplay.Weapons;

import Gameplay.Item;
import Util.KeyCombo;
import Util.Print;

import java.util.ArrayList;

public class Weapon extends Item
{
    private boolean ballistic = true;
    private KeyCombo[] keyCombos;
    private Style currentStyle = null;

    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);
    }

    /**
     * Depending on keyCombo and currentSytle, will cause the weapon to do
     * something.
     */
    void operate(KeyCombo keyCombo)
    {
        boolean matches = false;
        int i = 0;
        for (; i < keyCombos.length; i++)
        {
            if (keyCombo.matches(keyCombos[i]))
            {
                matches = true;
                break;
            }
        }
        if (matches) currentStyle.operate(i);
    }

    void setStyle(Style style) { currentStyle = style; }

    private class Style
    {
        private Operation[] operations;
        String name;

        Style(String name, Operation... operations)
        {
            this.operations = operations;
            this.name = name;
        }

        void operate(int i)
        {
            operations[i].run(name);
        }
    }

    private class Operation
    {
        private String name;

        Operation(String name)
        {
            this.name = name;
        }

        void run(String styleName)
        {
            Print.blue("Operating " + name + " using " + styleName);
        }
    }
}
