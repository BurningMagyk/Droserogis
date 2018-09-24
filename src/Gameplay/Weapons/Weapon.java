package Gameplay.Weapons;

import Gameplay.DirEnum;
import Gameplay.Item;

import java.util.Map;

public class Weapon extends Item
{
    private boolean ballistic = true;
    private Map<Integer, Operation> keyCombos;
    private Style currentStyle = Style.DEFAULT;

    Weapon(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);
    }

    /**
     * Depending on keyCombo and currentSytle, will cause the weapon to do
     * something.
     */
    void operate(int keyCombo, DirEnum direction)
    {
        keyCombos.get(keyCombo).run(direction);
    }

    enum Style
    {
        HALF
                {
                    boolean isValid(Weapon weapon)
                    {
                        if (weapon instanceof Sword) return true;
                        return false;
                    }
                },
        MURDER
                {
                    boolean isValid(Weapon weapon)
                    {
                        if (weapon instanceof Sword) return true;
                        return false;
                    }
                },
        DEFAULT;

        boolean isValid(Weapon weapon) { return true; }
    }

    Style getStyle()
    {
        return currentStyle;
    }

    interface Operation
    {
        String name = "";

        void run(DirEnum direction);
    }
}
