package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.Print;

public class Sword extends Weapon
{

    public Sword(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);


    }

    private class Thrust implements Operation
    {
        @Override
        public void run(DirEnum direction) {
            Print.blue("Operating " + name + " using " + getStyle() + " in the "
                    + direction + " direction");
        }
    }
}
