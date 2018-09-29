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
        public void start(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
        }

        @Override
        public boolean run(float deltaSec)
        {
            Print.blue("testing");
            return true;
        }
    }

    private class Swing implements Operation
    {
        @Override
        public String getName() { return "swing"; }

        @Override
        public void start(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
        }

        @Override
        public boolean run(float deltaSec)
        {
            Print.blue("testing");
            return true;
        }
    }
}
