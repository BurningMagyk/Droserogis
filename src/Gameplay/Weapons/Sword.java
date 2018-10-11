package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Sword extends Weapon
{
    public Sword(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        defaultOrient = new Orient(
                new Vec2(1F, -0.2F), (float) (-Math.PI / 4F));
        setTheta(defaultOrient.getTheta(), DirEnum.RIGHT);
        orient.set(defaultOrient.copy());

        new Thrust();
        new Swing();
    }

    private class Thrust implements Operation
    {
        Thrust()
        {
            setOperation(this, 0);
        }

        private DirEnum dir;

        @Override
        public String getName() { return "thrust"; }

        @Override
        public DirEnum getDir() { return dir; }

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
        private Journey warmJourney, coolJourney;

        Swing()
        {
            ArrayList<Tick> swingList = new ArrayList<>();
            swingList.add(new Tick(0.04F, 1.05F, -0.7F, -0.8F));
            swingList.add(new Tick(0.08F, 1.4F, -0.4F, -0.4F));
            swingList.add(new Tick(0.12F, 1.5F, -0.1F, -0.1F));
            swingList.add(new Tick(0.16F, 1.4F, 0.2F, 0.2F));
            ticks.put(this, swingList);
            setOperation(this, 1);

            warmJourney = new Journey(
                    defaultOrient, swingList.get(0).getOrient(), 0.2F);
            coolJourney = new Journey(
                    swingList.get(swingList.size() - 1).getOrient(),
                    defaultOrient, 0.25F);
        }

        private float totalSec = 0;
        private DirEnum dir;
        private State state = State.WARMUP;

        @Override
        public String getName() { return "swing"; }

        @Override
        public DirEnum getDir() { return dir; }

        @Override
        public void start(DirEnum direction) {
            Print.blue("Operating " + getName() + " using " + getStyle() + " in the "
                    + direction + " direction");
            dir = direction;

            warmJourney.setStart(orient);
        }

        @Override
        public boolean run(float deltaSec)
        {
            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                if (warmJourney.check(totalSec, dir))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                for (Tick tick : ticks.get(this))
                {
                    if (tick.check(totalSec, dir)) return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;
            return true;
        }
    }
}
