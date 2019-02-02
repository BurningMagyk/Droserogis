package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Natural extends Weapon
{
    Operation getOperation(Command command, Operation currentOp){return null;}
    boolean isApplicable(Command command){return false;}

    public Natural(float xPos, float yPos, float width, float height, Actor actor)
    {
        super(xPos, yPos, width, height);
        equip(actor);

        defaultOrient = new Orient(
                new Vec2(0.8F, 0), 0);
        setTheta(defaultOrient.getTheta(), DirEnum.RIGHT);
        orient.set(defaultOrient.copy());

        /*StatusAppCycle clumpCycle = new StatusAppCycle(
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.CLUMPED));
        StatusAppCycle poundCycle = new StatusAppCycle(
                new StatusApp(0.01F, Actor.Status.CLUMPED),
                new StatusApp(0.01F, Actor.Status.STAGNANT),
                new StatusApp(0.01F, Actor.Status.CLUMPED));
        StatusAppCycle plodRunCycle = new StatusAppCycle(
                null,
                new StatusApp(0.05F, Actor.Status.PLODDED),
                null);
        StatusAppCycle selfThrowCycle = new StatusAppCycle(
                new StatusApp(0.05F, Actor.Status.RUSHED),
                new StatusApp(0.01F, Actor.Status.STAGNANT),
                new StatusApp(0.01F, Actor.Status.CLUMPED));
        StatusAppCycle inertiaCycle = new StatusAppCycle(
                new StatusApp(0.05F, Actor.Status.INERT),
                new StatusApp(0.01F, Actor.Status.INERT),
                new StatusApp(0.01F, Actor.Status.INERT));
        StatusAppCycle rushStagnateCycle = new StatusAppCycle(
                new StatusApp(0.05F, Actor.Status.RUSHED),
                new StatusApp(0.01F, Actor.Status.STAGNANT),
                new StatusApp(0.01F, Actor.Status.STAGNANT));

        //================================================================================================================
        // Punching forward
        //================================================================================================================

        ArrayList<Tick> punchForward = new ArrayList<>();
        punchForward.add(new Tick(0.05F, 0.7F, -0.2F, 0F));
        punchForward.add(new Tick(0.08F, 1.2F, -0.2F, 0F));
        punchForward.add(new Tick(0.13F, 1.7F, -0.2F, 0F));
        setOperation(
                new Punch(0.4F, 0.3F, plodRunCycle,
                        punchForward),
                new int[] { Actor.ATTACK_KEY_2 },
                OpContext.STANDARD, OpContext.FREE);

        //================================================================================================================
        // Punching straight forward while crouching
        //================================================================================================================

        setOperation(new Punch(0.6F, 0.3F, clumpCycle,
                        punchForward),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN,
                        Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ}, // crouching
                OpContext.LOW);

        //================================================================================================================
        // Punching upwards
        //================================================================================================================

        ArrayList<Tick> punchUp = new ArrayList<>();
        punchUp.add(new Tick(0.05F, 0.4F, -0.3F, (float) -Math.PI/2));
        punchUp.add(new Tick(0.08F, 0.4F, -0.5F, (float) -Math.PI/2));
        punchUp.add(new Tick(0.13F, 0.4F, -0.8F, (float) -Math.PI/2));
        setOperation(
                new Punch(0.4F, 0.3F, plodRunCycle,
                        punchUp),
                new int[] { Actor.ATTACK_KEY_2 + Actor.COMBO_UP },
                OpContext.STANDARD, OpContext.FREE, OpContext.LOW);

        //================================================================================================================
        // Punching diagonally forward-up
        //================================================================================================================

        ArrayList<Tick> punchDiagonal = new ArrayList<>();
        punchDiagonal.add(new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI/4));
        punchDiagonal.add(new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI/4));
        punchDiagonal.add(new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI/4));

        setOperation(new Punch(0.35F, 0.35F, plodRunCycle,
                        punchDiagonal),
                new int[] {Actor.ATTACK_KEY_2 + Actor.COMBO_UP + Actor.COMBO_HORIZ},
                OpContext.STANDARD, OpContext.FREE);

        //================================================================================================================
        // Pushing while sprinting (trying to punch while sprinting)
        //================================================================================================================

        ArrayList<Tick> pushForward = new ArrayList<>();
        pushForward.add(new Tick(0.05F, 0.7F, -0.2F, 0F));
        pushForward.add(new Tick(0.08F, 1.2F, -0.2F, 0F));
        pushForward.add(new Tick(0.13F, 1.7F, -0.2F, 0F));
        setOperation(
                new Push(0.4F, 0.3F, inertiaCycle,
                        pushForward),
                new int[] { Actor.ATTACK_KEY_2,
                        Actor.ATTACK_KEY_2 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ,
                        Actor.ATTACK_KEY_2 + Actor.COMBO_UP + Actor.COMBO_HORIZ },
                OpContext.LUNGE);

        //================================================================================================================
        // Pound and uppercut
        //================================================================================================================

        ArrayList<Tick> pound = new ArrayList<>(),
                uppercut = new ArrayList<>();
        pound.add(new Tick(0.04F, 1.05F, -0.5F, -0.8F));
        pound.add(new Tick(0.08F, 1.4F, -0.4F, -0.4F));
        pound.add(new Tick(0.12F, 1.5F, -0.1F, -0.1F));
        pound.add(new Tick(0.16F, 1.4F, 0.2F, 0.2F));
        uppercut.add(new Tick(0.04F, 1.4F, 0.2F, 0.2F));
        uppercut.add(new Tick(0.08F, 1.5F, -0.1F, -0.1F));
        uppercut.add(new Tick(0.12F, 1.4F, -0.4F, -0.4F));
        uppercut.add(new Tick(0.16F, 1.05F, -0.7F, -0.8F));

        setOperation(new Punch(0.4F, 0.5F, plodRunCycle,
                        pound, uppercut),
                new int[] {Actor.ATTACK_KEY_1,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_UP + Actor.COMBO_HORIZ},
                OpContext.STANDARD, OpContext.FREE);
        setOperation(new Punch(0.4F, 0.5F, plodRunCycle,
                        pound),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_UP + Actor.COMBO_HORIZ,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_UP},
                OpContext.STANDARD, OpContext.FREE);
        setOperation(new Punch(0.3F, 0.5F, plodRunCycle,
                        uppercut),
                new int[] {Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ},
                OpContext.LOW);

        //================================================================================================================
        // Shoving (trying to pound or uppercut while sprinting)
        //================================================================================================================

        /*setOperation(new Shove(inertiaCycle),
                new int[] {Actor.ATTACK_KEY_1,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_DOWN + Actor.COMBO_HORIZ,
                        Actor.ATTACK_KEY_1 + Actor.COMBO_UP + Actor.COMBO_HORIZ},
                OpContext.LUNGE);*/
    }

    /*private class Punch extends BasicMelee
    {
        Punch(float warmupTime, float cooldownTime,
              StatusAppCycle statusAppCycle, ArrayList<Tick> journey)
        {
            super(warmupTime, cooldownTime, statusAppCycle, journey);
        }
        Punch(float warmupTime, float cooldownTime,
              StatusAppCycle statusAppCycle,
              ArrayList<Tick> poundJourney, ArrayList<Tick> uppercutJourney)
        {
            super(warmupTime, cooldownTime, statusAppCycle,
                    poundJourney, uppercutJourney);
        }

        @Override
        public String getName() { return "punch"; }
    }*/

    /*private class OpenHands extends BasicMelee
    {
        OpenHands(float warmupTime, float cooldownTime,
             StatusAppCycle statusAppCycle, ArrayList<Tick> journey)
        {
            super(warmupTime, cooldownTime, statusAppCycle, journey);
        }

        @Override
        public String getName() { return "push"; }
    }*/

    /*private class Push extends OpenHands
    {
        Push(float warmupTime, float cooldownTime,
             StatusAppCycle statusAppCycle, ArrayList<Tick> journey)
        {
            super(warmupTime, cooldownTime, statusAppCycle, journey);
        }

        @Override
        public String getName() { return "push"; }
    }*/

    /*private class Grab extends BasicMelee
    {
        Grab(float warmupTime, float cooldownTime,
             StatusAppCycle statusAppCycle, ArrayList<Tick> journey)
        {
            super(warmupTime, cooldownTime, statusAppCycle, journey);
        }

        @Override
        public String getName() { return "grab"; }
    }*/

    private class Slam implements Operation
    {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public DirEnum getDir() {
            return null;
        }

        @Override
        public void start(){ }

        @Override
        public boolean run(float deltaSec) {
            return false;
        }

        @Override
        public boolean mayInterrupt() {
            return false;
        }

        @Override
        public void letGo(int attackKey) { }
    }

    private class Shove extends Slam
    {
        Shove(StatusAppCycle statusAppCycle){}
    }

    private class Tackle extends Slam
    {
        Tackle(StatusAppCycle statusAppCycle){}
    }

    /*private class Kick extends BasicMelee
    {
        Kick(float warmupTime, float cooldownTime,
              StatusAppCycle statusAppCycle, ArrayList<Tick> journey)
        {
            super(warmupTime, cooldownTime, statusAppCycle, journey);
        }

        @Override
        public String getName() { return "kick"; }
    }*/
}
