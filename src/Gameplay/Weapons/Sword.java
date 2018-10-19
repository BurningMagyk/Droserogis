package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;
import java.util.Collections;

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
        private Journey[] warmJourney, coolJourney;
        private int hau = 0;
        ArrayList<Tick> downward, unterhau;

        Swing()
        {
            setOperation(this, 1);

            downward = new ArrayList<>(); unterhau = new ArrayList<>();

            downward.add(new Tick(0.04F, 1.05F, -0.7F, -0.8F));
            downward.add(new Tick(0.08F, 1.4F, -0.4F, -0.4F));
            downward.add(new Tick(0.12F, 1.5F, -0.1F, -0.1F));
            downward.add(new Tick(0.16F, 1.4F, 0.2F, 0.2F));

            unterhau.add(new Tick(0.04F, 1.4F, 0.2F, 0.2F));
            unterhau.add(new Tick(0.08F, 1.5F, -0.1F, -0.1F));
            unterhau.add(new Tick(0.12F, 1.4F, -0.4F, -0.4F));
            unterhau.add(new Tick(0.16F, 1.05F, -0.7F, -0.8F));

            warmJourney = new Journey[2];
            warmJourney[0] = new Journey(
                    defaultOrient, downward.get(0).getOrient(), 0.4F);
            warmJourney[1] = new Journey(
                    defaultOrient, unterhau.get(0).getOrient(), 0.4F);
            coolJourney = new Journey[2];
            coolJourney[0] = new Journey(
                    downward.get(downward.size() - 1).getOrient(),
                    defaultOrient, 0.5F);
            coolJourney[1] = new Journey(
                    unterhau.get(unterhau.size() - 1).getOrient(),
                    defaultOrient, 0.5F);
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
            dir = direction;
            totalSec = 0;
            state = State.WARMUP;

            float distDownward = warmJourney[0].setStart(orient);
            float distUnterhau = warmJourney[1].setStart(orient);
            if (distDownward < distUnterhau) hau = 0;
            else hau = 1;

            Print.blue("Operating " + getName() + " using " + getStyle()
                    + " as " + hau);
        }

        @Override
        public boolean run(float deltaSec)
        {
            totalSec += deltaSec;

            if (state == State.WARMUP)
            {
                if (warmJourney[hau].check(totalSec, dir))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                for (Tick tick : hau == 0 ? downward : unterhau)
                {
                    if (tick.check(totalSec, dir)) return false;
                }
                totalSec = 0;
                state = State.COOLDOWN;
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                if (!coolJourney[hau].check(totalSec, dir))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;
            return true;
        }
    }
}
