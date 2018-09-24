package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Entity;
import Gameplay.Item;

import java.util.ArrayList;
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

    @Override
    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        if (ballistic) super.update(entities, deltaSec);
        // to check line intersections:
        // https://stackoverflow.com/questions/4977491/determining-if-two-line-segments-intersect/4977569#4977569
    }

    /**
     * Depending on keyCombo and currentSytle, will cause the weapon to do
     * something.
     */
    public void operate(int keyCombo, DirEnum direction)
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

    public Weapon equip(Actor actor)
    {
        ballistic = false;
        return this;
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
