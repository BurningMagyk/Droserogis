package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;
import java.util.Collections;

public class Sword extends Weapon
{
    @Override
    Operation getOperation(Command command, Operation currentOp) { return null; }
    @Override
    boolean isApplicable(Command command) { return false; }

    public Sword(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        defaultOrient = new Orient(
                new Vec2(1F, -0.2F), (float) (-Math.PI / 4F));
        setTheta(defaultOrient.getTheta(), DirEnum.RIGHT);
        orient.set(defaultOrient.copy());

        /*StatusAppCycle clumpCycle = new StatusAppCycle(
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.CLUMPED));
        StatusAppCycle plodRunCycle = new StatusAppCycle(
                null,
                new StatusApp(0.05F, Actor.Status.PLODDED),
                null);
        StatusAppCycle rushStagnateCycle = new StatusAppCycle(
                new StatusApp(0.05F, Actor.Status.RUSHED),
                new StatusApp(0.01F, Actor.Status.STAGNANT),
                new StatusApp(0.01F, Actor.Status.STAGNANT));

        //================================================================================================================
        // Thrusting straight forward, stabbing downward, stabbing upward, and stabbing behind
        //================================================================================================================

        ArrayList<Tick> thrustReach = new ArrayList<>(),
                thrustDownward = new ArrayList<>(),
                thrustUnterhau = new ArrayList<>(),
                thrustBehind = new ArrayList<>();
        thrustReach.add(new Tick(0.06F, 0.8F, -0.2F, 0F));
        thrustReach.add(new Tick(0.10F, 1.4F, -0.2F, 0F));
        thrustReach.add(new Tick(0.16F, 2F, -0.2F, 0F));
        thrustDownward.add(new Tick(0.04F, 1.1F, -0.6F, (float) Math.PI/2));
        thrustDownward.add(new Tick(0.08F, 1.1F, -0.1F, (float) Math.PI/2));
        thrustDownward.add(new Tick(0.12F, 1.1F, 0.4F, (float) Math.PI/2));
        thrustUnterhau.add(new Tick(0.04F, 1.3F, 0F, (float) -Math.PI/2));
        thrustUnterhau.add(new Tick(0.08F, 1.3F, -0.5F, (float) -Math.PI/2));
        thrustUnterhau.add(new Tick(0.12F, 1.3F, -1F, (float) -Math.PI/2));
        thrustBehind.add(new Tick(0.06F, 1.2F, 0F, 0F));
        thrustBehind.add(new Tick(0.10F, 0.3F, 0F, 0F));
        thrustBehind.add(new Tick(0.16F, -0.6F, 0F, 0F));

        setOperation(new StandingThrust(0.6F, 0.3F, plodRunCycle,
                thrustReach, thrustDownward, thrustUnterhau, thrustBehind),
                new int[] {Actor.ATTACK_KEY_2}, // standing, airborne
                OpContext.STANDARD, OpContext.FREE);

        //================================================================================================================
        // Thrusting straight forward while crouching
        //================================================================================================================

        setOperation(new Thrust(0.6F, 0.3F, clumpCycle,
                thrustReach),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN,
                        Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ}, // crouching
                OpContext.LOW);

        //================================================================================================================
        // Thrusting straight forward while sprinting
        //================================================================================================================

        setOperation(new Thrust(0.6F, 0.3F, rushStagnateCycle,
                thrustReach),
                new int[] {Actor.ATTACK_KEY_2}, // sprinting
                OpContext.LUNGE);

        //================================================================================================================
        // Thrusting straight upward
        //================================================================================================================

        ArrayList<Tick> thrustUpwards = new ArrayList<>();
        thrustUpwards.add(new Tick(0.06F, 0.4F, -0.4F, (float) -Math.PI/2));
        thrustUpwards.add(new Tick(0.10F, 0.4F, -0.7F, (float) -Math.PI/2));
        thrustUpwards.add(new Tick(0.16F, 0.4F, -1F, (float) -Math.PI/2));

        setOperation(new Thrust(0.25F, 0.25F, plodRunCycle,
                thrustUpwards),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_UP},
                OpContext.STANDARD, OpContext.FREE);

        //================================================================================================================
        // Thrusting diagonally forward-up
        //================================================================================================================

        ArrayList<Tick> thrustDiagonal = new ArrayList<>();
        thrustDiagonal.add(new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI/4));
        thrustDiagonal.add(new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI/4));
        thrustDiagonal.add(new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI/4));

        setOperation(new Thrust(0.35F, 0.35F, plodRunCycle,
                thrustDiagonal),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_UP + Actor.COMBO_HORIZ},
                OpContext.STANDARD, OpContext.FREE);

        //================================================================================================================
        // Thrusting straight down and diagonally forward-down and
        //================================================================================================================

        // Do not confuse with "thrustDownward" used in the Thrust object
        ArrayList<Tick> thrustDownwards = new ArrayList<>(),
                thrustDiagonalDown = new ArrayList<>();
        for (Tick tick : thrustUpwards)
        {
            Tick tickCopy = tick.getMirrorCopy(false, true);
            tickCopy.getOrient().addTheta((float) Math.PI / 2);
            thrustDownwards.add(tickCopy);
        }
        for (Tick tick : thrustDiagonal)
        {
            thrustDiagonalDown.add(tick.getMirrorCopy(false, true));
        }
        setOperation(new Thrust(0.2F, 0.2F, plodRunCycle,
                thrustDownwards),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN},
                OpContext.FREE);
        setOperation(new Thrust(0.2F, 0.2F, plodRunCycle,
                thrustDiagonalDown),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ},
                OpContext.FREE);

        //================================================================================================================
        // Swinging in front
        //================================================================================================================

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
        setOperation(new Swing(0.4F, 0.5F, plodRunCycle,
                swingDownward, swingUnterhau),
                new int[] {Actor.ATTACK_KEY_1},
                OpContext.STANDARD, OpContext.FREE);
        setOperation(new SimpleSwing(0.4F, 0.5F, plodRunCycle,
                swingDownward),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_UP + Actor.COMBO_HORIZ},
                OpContext.STANDARD, OpContext.FREE);
        setOperation(new SimpleSwing(0.3F, 0.5F, plodRunCycle,
                swingUnterhau),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ},
                OpContext.LOW);

        //================================================================================================================
        // Swinging in front while sprinting
        //================================================================================================================

        swingDownward = new ArrayList<>();
        swingUnterhau = new ArrayList<>();

        swingDownward.add(new Tick(0.03F,  -0.8F,-0.6F, -2F));
        swingDownward.add(new Tick(0.06F,  -0.2F,-0.85F, -1.5F));
        swingDownward.add(new Tick(0.09F,  0.4F,-0.85F, -1F));
        swingDownward.add(new Tick(0.12F,  1.05F,-0.7F, -0.5F));

        swingDownward.add(new Tick(0.15F, 1.05F, -0.7F, -0.8F));
        swingDownward.add(new Tick(0.18F, 1.4F, -0.4F, -0.4F));
        swingDownward.add(new Tick(0.21F, 1.5F, -0.1F, -0.1F));
        swingDownward.add(new Tick(0.24F, 1.4F, 0.2F, 0.2F));

        swingUnterhau.add(new Tick(0.03F,  -0.8F,0.6F, -2F));
        swingUnterhau.add(new Tick(0.06F,  -0.2F,0.85F, -1.5F));
        swingUnterhau.add(new Tick(0.09F,  0.4F,0.85F, -1F));
        swingUnterhau.add(new Tick(0.12F,  1.05F,0.7F, -0.5F));

        swingUnterhau.add(new Tick(0.15F, 1.4F, 0.2F, 0.2F));
        swingUnterhau.add(new Tick(0.18F, 1.5F, -0.1F, -0.1F));
        swingUnterhau.add(new Tick(0.21F, 1.4F, -0.4F, -0.4F));
        swingUnterhau.add(new Tick(0.24F, 1.05F, -0.7F, -0.8F));

        setOperation(new Swing(0.15F, 0.3F, rushStagnateCycle,
                swingDownward, swingUnterhau),
                new int[] {Actor.ATTACK_KEY_1}, // sprinting
                OpContext.LUNGE);
        setOperation(new SimpleSwing(0.15F, 0.3F, rushStagnateCycle,
                        swingDownward),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_UP + Actor.COMBO_HORIZ}, // sprinting
                OpContext.LUNGE);
        setOperation(new SimpleSwing(0.15F, 0.3F, rushStagnateCycle,
                swingUnterhau),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ}, // crouch + sprinting
                OpContext.LUNGE);

        //================================================================================================================
        // Swinging upwards
        //================================================================================================================

        ArrayList<Tick> swingForehand = new ArrayList<>(),
                swingBackhand = new ArrayList<>();
        swingForehand.add(new Tick(0.04F,  -0.8F,-0.6F, -2F));
        swingForehand.add(new Tick(0.08F,  -0.2F,-0.85F, -1.5F));
        swingForehand.add(new Tick(0.12F,  0.4F,-0.85F, -1F));
        swingForehand.add(new Tick(0.16F,  1.05F,-0.7F, -0.5F));
        swingBackhand.add(new Tick(0.04F,  1.05F,-0.7F, -0.5F));
        swingBackhand.add(new Tick(0.08F,  0.4F,-0.85F, -1F));
        swingBackhand.add(new Tick(0.12F,  -0.2F,-0.85F, -1.5F));
        swingBackhand.add(new Tick(0.16F,  -0.8F,-0.6F, -2F));
        ArrayList<Tick> swingForehandDown = new ArrayList<>(),
                swingBackhandDown = new ArrayList<>();
        for (Tick tick : swingForehand)
        {
            swingForehandDown.add(tick.getMirrorCopy(false, true));
        }
        for (Tick tick : swingBackhand)
        {
            swingBackhandDown.add(tick.getMirrorCopy(false, true));
        }
        setOperation(new TurningSwing(0.3F, 0.5F, plodRunCycle,
                swingForehand, swingBackhand),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_UP,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_UP + Actor.COMBO_DOWN},
                OpContext.STANDARD, OpContext.LOW, OpContext.FREE);
        setOperation(new TurningSwing(0.1F, 0.5F, plodRunCycle,
                swingForehandDown, swingBackhandDown),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN},
                OpContext.FREE);*/

    }

    /*private class StandingThrust extends Thrust
    {
        StandingThrust(float warmupTime, float cooldownTime, StatusAppCycle statusAppCycle,
               ArrayList<Tick> reachJourney,
               ArrayList<Tick> stabJourney, ArrayList<Tick> unterJourney,
               ArrayList<Tick> behindJourney)
        {
            super(warmupTime, cooldownTime, statusAppCycle, stabJourney, unterJourney,
                    behindJourney, reachJourney);
        }

        @Override
        public String getName() { return "thrust"; }

        @Override
        public void start(DirEnum direction)
        {
            erected = false;
            dir = direction;
            totalSec = 0;
            state = State.WARMUP;

            if (prev instanceof TurningSwing) hau = 2;
            else if (prev instanceof Swing || prev instanceof SimpleSwing)
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

            statusAppCycle.applyStart();

            Print.blue("Operating " + getName() + " using " + getStyle()
                    + " as " + hau);
        }
    }

    private class TurningSwing extends BasicMelee
    {

        TurningSwing(float warmupTime, float cooldownTime,
                StatusAppCycle statusAppCycle,
                ArrayList<Tick> forehand, ArrayList<Tick> backhand)
        {
            super(warmupTime, cooldownTime, statusAppCycle, forehand, backhand);

            coolJourney[0] = new Journey(
                    forehand.get(forehand.size() - 1).getOrient(),
                    defaultOrient.copyOppHoriz(), 0.5F);
            coolJourney[1] = new Journey(
                    backhand.get(backhand.size() - 1).getOrient(),
                    defaultOrient.copyOppHoriz(), 0.5F);
        }

        @Override
        public String getName() { return "turning_swing"; }

        @Override
        public void start(DirEnum direction) {
            dir = direction;
            changeActorDirFace();

            super.start(direction);
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

            orient.set(execJourney[hau].get(0).tickOrient);
            statusAppCycle.applyFinish();
            return true;
        }
    }

    private class Thrust extends BasicMelee
    {
        Thrust(float warmupTime, float cooldownTime,
               StatusAppCycle statusAppCycle, ArrayList<Tick>... execJourney)
        {
            super(warmupTime, cooldownTime, statusAppCycle, execJourney);
        }

        @Override
        public String getName() { return "thrust"; }

        boolean erected = false;
        private boolean isLetGo = false;

        @Override
        public void start(DirEnum direction)
        {
            super.start(direction);
            erected = false;
        }

        @Override
        public boolean run(float deltaSec)
        {
            if (!erected)
            {
                statusAppCycle.applyRun();
                totalSec += deltaSec;
            }

            if (state == State.WARMUP)
            {
                erected = false;
                if (warmJourney[hau].check(totalSec, dir))
                {
                    totalSec = 0;
                    state = State.EXECUTION;
                }
                return false;
            }
            else if (state == State.EXECUTION)
            {
                //Print.blue("erected: " + erected + ", isLetGo: " + isLetGo);
                for (Tick tick : execJourney[hau])
                {
                    if (tick.check(totalSec, dir)) return false;
                }
                if (isLetGo)
                {
                    totalSec = 0;
                    state = State.COOLDOWN;
                    isLetGo = false;
                }
                erected = true;
                return false;
            }
            else if (state == State.COOLDOWN)
            {
                erected = false;
                if (!coolJourney[hau].check(totalSec, dir))
                {
                    return false;
                }
            }

            totalSec = 0;
            state = State.WARMUP;

            statusAppCycle.applyFinish();
            return true;
        }

        @Override
        public boolean letGo()
        {
            boolean returnValue = isLetGo;

            isLetGo = true;
            erected = false;

            /* Usually always returns true */
            /*return returnValue;
        }
    }

    private class Swing extends BasicMelee
    {
        Swing(float warmupTime, float cooldownTime,
              StatusAppCycle statusAppCycle,
              ArrayList<Tick> downward, ArrayList<Tick> unterhau)
        {
            super(warmupTime, cooldownTime, statusAppCycle,
                    downward, unterhau);
        }

        @Override
        public String getName() { return "swing"; }
    }

    private class SimpleSwing extends BasicMelee
    {
        SimpleSwing(float warmupTime, float cooldownTime,
              StatusAppCycle statusAppCycle,
              ArrayList<Tick> swing)
        {
            super(warmupTime, cooldownTime, statusAppCycle, swing);
        }
    }*/
}
