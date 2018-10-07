package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.Print;

import java.util.ArrayList;

public class Sword extends Weapon
{

    public Sword(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        Thrust thrust = new Thrust();
        addOperation(thrust, 0);

        Swing swing = new Swing();
        ArrayList<MovementFrame> swingList = new ArrayList<>();
        swingList.add(new MovementFrame(2, 2, 0, 3.141F/4F));
        addMovementFrames(swing, swingList);
        addOperation(swing, 1);

        setTheta(3.141F/4F, DirEnum.RIGHT);
    }

    private class Thrust implements Operation
    {
        DirEnum dirFace;

        @Override
        public String getName() { return "thrust"; }

        @Override
        public int getResilience() { return 1; }

        @Override
        public void nextFrame(float relX, float relY, float theta) {

        }

        @Override
        public void start(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
            dirFace = direction;
        }

        @Override
        public boolean run(float deltaSec)
        {
            setTheta(80, dirFace);
            return true;
        }
    }

    private class Swing implements Operation
    {
        private float totalSec = 0;
        private DirEnum dir;

        @Override
        public String getName() { return "swing"; }

        @Override
        public int getResilience() { return 1; }

        @Override
        public void nextFrame(float relX, float relY, float theta)
        {
            relativePos.x = relX;
            relativePos.y = relY;
            setTheta(theta, dir);
        }

        @Override
        public void start(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
            dir = direction;
        }

        @Override
        public boolean run(float deltaSec)
        {
            totalSec += deltaSec;
            Print.blue(totalSec);

            for (MovementFrame frame : movementFrames.get(this)[0])
            {
                if (frame.check(totalSec, this)) return false;
            }

            totalSec = 0;
            return true;
        }
    }
}
