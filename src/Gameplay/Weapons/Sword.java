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
        ArrayList<Tick> swingList = new ArrayList<>();
        swingList.add(new Tick(0.05F, 1.05F, -0.7F, -0.8F));
        swingList.add(new Tick(0.1F, 1.4F, -0.4F, -0.4F));
        swingList.add(new Tick(0.15F, 1.5F, -0.1F, -0.1F));
        swingList.add(new Tick(0.2F, 1.4F, 0.2F, 0.2F));
        setTicks(swing, swingList);
        addOperation(swing, 1);

        setTheta(-3.141F/4F, DirEnum.RIGHT);
    }

    private class Thrust implements Operation
    {
        private DirEnum dir;

        @Override
        public String getName() { return "thrust"; }

        @Override
        public DirEnum getDir() { return dir; }

        @Override
        public void nextFrame(Orient _orient) {

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
        private State state = State.WARMUP;

        @Override
        public String getName() { return "swing"; }

        @Override
        public DirEnum getDir() { return dir; }

        @Override
        public void nextFrame(Orient _orient)
        {
            orient.set(_orient);
            setTheta(_orient.getTheta(), dir);
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


            for (Tick tick : ticks.get(this))
            {
                if (tick.check(totalSec, this)) return false;
            }

            totalSec = 0;
            return true;
        }
    }
}
