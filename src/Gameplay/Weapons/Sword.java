package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.Print;

public class Sword extends Weapon
{

    public Sword(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        addOperation(new Thrust(), 0);
        addOperation(new Swing(), 1);
    }

    private class Thrust implements Operation
    {
        @Override
        public String getName() { return "thrust"; }

        @Override
        public void run(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
        }
    }

    private class Swing implements Operation
    {
        @Override
        public String getName() { return "swing"; }

        @Override
        public void run(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
        }
    }
}
