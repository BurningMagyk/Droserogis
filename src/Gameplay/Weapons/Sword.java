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

        ArrayList<Tick> thrustReach = new ArrayList<>(),
                thrustDownward = new ArrayList<>(),
                thrustUnterhau = new ArrayList<>(),
                thrustBehind = new ArrayList<>();
        thrustReach.add(new Tick(0.06F, 0.8F, -0.2F, 0F));
        thrustReach.add(new Tick(0.10F, 1.3F, -0.2F, 0F));
        thrustReach.add(new Tick(0.16F, 1.8F, -0.2F, 0F));
        thrustDownward.add(new Tick(0.04F, 1F, -0.3F, 0F));
        thrustDownward.add(new Tick(0.08F, 1F, 0, 0F));
        thrustDownward.add(new Tick(0.12F, 1F, 0.3F, 0F));
        thrustUnterhau.add(new Tick(0.04F, 1F, 0.3F, 0F));
        thrustUnterhau.add(new Tick(0.08F, 1F, 0, 0F));
        thrustUnterhau.add(new Tick(0.12F, 1F, -0.3F, 0F));
        thrustBehind.add(new Tick(0.06F, 1.2F, -0.2F, 0F));
        thrustBehind.add(new Tick(0.10F, 0.7F, -0.2F, 0F));
        thrustBehind.add(new Tick(0.16F, 0.2F, -0.2F, 0F));

        setOperation(new Thrust(0.6F, 0.3F, thrustReach, thrustDownward,
                thrustUnterhau, thrustBehind), 1, 0);

        ArrayList<Tick> swingDownward = new ArrayList<>(),
                swingUnterhau = new ArrayList<>();
        swingDownward.add(new Tick(0.04F, 1.05F, -0.7F, -0.8F));
        swingDownward.add(new Tick(0.08F, 1.4F, -0.4F, -0.4F));
        swingDownward.add(new Tick(0.12F, 1.5F, -0.1F, -0.1F));
        swingDownward.add(new Tick(0.16F, 1.4F, 0.2F, 0.2F));
        swingUnterhau.add(new Tick(0.04F, 1.4F, 0.2F, 0.2F));
        swingUnterhau.add(new Tick(0.08F, 1.5F, -0.1F, -0.1F));
        swingUnterhau.add(new Tick(0.12F, 1.4F, -0.4F, -0.4F));
        swingUnterhau.add(new Tick(0.16F, 1.05F, -0.7F, -0.8F));
        setOperation(new Swing(0.4F, 0.5F, swingDownward, swingUnterhau), 0, 0);

        ArrayList<Tick> swingForehand = new ArrayList<>(),
                swingBackhand = new ArrayList<>();
        swingForehand.add(new Tick(0.04F,  -0.7F,-0.6F, -2F));
        swingForehand.add(new Tick(0.08F,  -0.2F,-0.85F, -1.5F));
        swingForehand.add(new Tick(0.12F,  0.2F,-0.85F, -1F));
        swingForehand.add(new Tick(0.16F,  0.7F,-0.6F, -0.5F));
        swingBackhand.add(new Tick(0.04F,  0.7F,-0.6F, -0.5F));
        swingBackhand.add(new Tick(0.08F,  0.2F,-0.85F, -1F));
        swingBackhand.add(new Tick(0.12F,  -0.2F,-0.85F, -1.5F));
        swingBackhand.add(new Tick(0.16F,  -0.7F,-0.6F, -2F));
        setOperation(new SwingUp(0.4F, 0.5F, swingForehand, swingBackhand), 0, 1);

    }

    private class Thrust extends BasicMelee
    {
        Thrust(float warmupTime, float cooldownTime,
               ArrayList<Tick> reachJourney,
               ArrayList<Tick> stabJourney, ArrayList<Tick> unterJourney,
               ArrayList<Tick> behindJourney)
        {
            super(warmupTime, cooldownTime, stabJourney, unterJourney,
                    behindJourney, reachJourney);
        }

        @Override
        public String getName() { return "thrust"; }

        @Override
        public void start(DirEnum direction, Operation prev) {
            dir = direction;
            totalSec = 0;
            state = State.WARMUP;

            if (prev instanceof SwingUp) hau = 2;
            else if (prev instanceof Swing)
            {
                float hauDist = Float.MAX_VALUE;
                for (int i = 0; i < 2; i++)
                {
                    float currDist = warmJourney[i].setStart(orient);
                    if (currDist < hauDist)
                    {
                        hauDist = currDist;
                        hau = i;
                    }
                }
            }
            else hau = 3;

            Print.blue("Operating " + getName() + " using " + getStyle()
                    + " as " + hau);
        }
    }

    private class SwingUp extends Swing
    {
        SwingUp(float warmupTime, float cooldownTime, ArrayList<Tick> forehand, ArrayList<Tick> backhand)
        {
            super(warmupTime, cooldownTime, forehand, backhand);

            coolJourney[0] = new Journey(
                    forehand.get(forehand.size() - 1).getOrient(),
                    defaultOrient.copyOppHoriz(), 0.5F);
            coolJourney[1] = new Journey(
                    backhand.get(backhand.size() - 1).getOrient(),
                    defaultOrient.copyOppHoriz(), 0.5F);
        }

        @Override
        public String getName() { return "swing_up"; }

        @Override
        public void start(DirEnum direction, Operation prev) {
            dir = direction;
            changeActorDirFace();

            super.start(direction, prev);
        }

        @Override
        public boolean run(float deltaSec)
        {
            boolean finished = super.run(deltaSec);
            if (!finished) return false;

            orient.set(defaultOrient);

            return true;
        }

        @Override
        public boolean mayInterrupt()
        {
            if (state == State.EXECUTION) return false;

            orient.set(defaultOrient);
            return true;
        }
    }

    private class BasicMelee implements Operation
    {
        Journey[] warmJourney, coolJourney;
        int hau = 0;
        ArrayList<Tick>[] execJourney;

        BasicMelee(float warmupTime, float cooldownTime, ArrayList<Tick>... execJourney)
        {
            this.execJourney = execJourney;

            warmJourney = new Journey[execJourney.length];
            coolJourney = new Journey[execJourney.length];
            for (int i = 0; i < execJourney.length; i++)
            {
                warmJourney[i] = new Journey(defaultOrient,
                        execJourney[i].get(0).getOrient(), warmupTime);
                coolJourney[i] = new Journey(
                        execJourney[i].get(execJourney[i].size() - 1).getOrient(),
                        defaultOrient,cooldownTime);
            }
        }

        float totalSec = 0;
        DirEnum dir;
        State state = State.WARMUP;

        @Override
        public String getName() { return "basic_melee"; }

        @Override
        public DirEnum getDir() { return dir; }

        @Override
        public void start(DirEnum direction, Operation prev) {
            dir = direction;
            totalSec = 0;
            state = State.WARMUP;

            float hauDist = Float.MAX_VALUE;
            for (int i = 0; i < warmJourney.length; i++)
            {
                float currDist = warmJourney[i].setStart(orient);
                if (currDist < hauDist)
                {
                    hauDist = currDist;
                    hau = i;
                }
            }

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
                for (Tick tick : execJourney[hau])
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

        @Override
        public boolean mayInterrupt()
        {
            if (state == State.EXECUTION) return false;
            return true;
        }
    }

    private class Swing extends BasicMelee
    {
        Swing(float warmupTime, float cooldownTime, ArrayList<Tick> downward, ArrayList<Tick> unterhau)
        {
            super(warmupTime, cooldownTime, downward, unterhau);
        }

        @Override
        public String getName() { return "swing"; }
    }
}
