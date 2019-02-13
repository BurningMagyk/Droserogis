package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Natural extends Weapon
{
    private Operation PUNCH, PUNCH_UP, PUNCH_DIAG, PUSH, HAYMAKER, UPPERCUT,
            SHOVE, STOMP, STOMP_FALL, KICK, KICK_ARC, KICK_AERIAL,
            KICK_AERIAL_DIAG, GRAB, TACKLE, THROW;

    Operation getOperation(Command command, Operation currentOp)
    {
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_1)
        {
            if (command.DIR == DirEnum.UP)
                return setOperation(PUNCH_UP, command);
            else if (command.DIR.getVert() == DirEnum.UP)
                return setOperation(PUNCH_DIAG, command);
            return setOperation(PUNCH, command);
        }
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_2)
        {
            if (command.TYPE == Command.StateType.LOW)
                return setOperation(UPPERCUT, command);
            return setOperation(HAYMAKER, command);
        }
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3)
        {
            if (command.DIR.getHoriz() == DirEnum.NONE)
                return setOperation(STOMP, command);
            else return setOperation(KICK_ARC, command);
        }
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3 + Actor.ATTACK_KEY_MOD)
        {
            return setOperation(KICK, command);
        }
        return null;
    }

    boolean isApplicable(Command command) { return true; }

    public Natural(float xPos, float yPos, float width, float height, Actor actor)
    {
        super(xPos, yPos, width, height);
        equip(actor);

        defaultOrient = new Orient(
                new Vec2(0.8F, 0), 0);
        setTheta(defaultOrient.getTheta(), DirEnum.RIGHT);
        orient.set(defaultOrient.copy());

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH                                ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> punchTicks = new ArrayList<>();
        punchTicks.add(new Tick(0.05F, 0.7F, -0.2F, 0F));
        punchTicks.add(new Tick(0.08F, 1.2F, -0.2F, 0F));
        punchTicks.add(new Tick(0.13F, 1.7F, -0.2F, 0F));

        ConditionApp punchApp = new ConditionApp(
                0.01F, Actor.Condition.CANT_CROUCH, Actor.Condition.SLOW_RUN);
        ConditionAppCycle punchAppCycle
                = new ConditionAppCycle(punchApp, punchApp, punchApp);

        class Punch extends Melee {
            Punch(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
            }
            public String getName() { return "punch"; }
            public boolean mayInterrupt(Command check) {
                return state == State.COOLDOWN;
            }
        }

        PUNCH = new Punch(0.4F, 0.3F, punchAppCycle, punchTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH (UP)                           ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> punchUpTicks = new ArrayList<>();
        punchUpTicks.add(new Tick(0.05F, 0.4F, -0.1F, (float) -Math.PI/2));
        punchUpTicks.add(new Tick(0.08F, 0.4F, -0.4F, (float) -Math.PI/2));
        punchUpTicks.add(new Tick(0.13F, 0.4F, -0.8F, (float) -Math.PI/2));

        ConditionApp punchUpApp = new ConditionApp(
                0.01F, Actor.Condition.CANT_CROUCH, Actor.Condition.CANT_MOVE);
        ConditionAppCycle punchUpAppCycle
                = new ConditionAppCycle(punchUpApp, punchUpApp, punchUpApp);

        PUNCH_UP = new Punch(0.4F, 0.3F, punchUpAppCycle, punchUpTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH (UP-FORWARD)                   ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> punchDiagTicks = new ArrayList<>();
        punchDiagTicks.add(new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI/4));
        punchDiagTicks.add(new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI/4));
        punchDiagTicks.add(new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI/4));

        PUNCH_DIAG = new Punch(0.4F, 0.3F, punchAppCycle, punchDiagTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUSH                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp pushApp = new ConditionApp(
                0.05F, Actor.Condition.CANT_MOVE);
        ConditionAppCycle pushAppCycle
                = new ConditionAppCycle(null, null, pushApp);

        /* Will do these when adding collision */

        ///////////////////////////////////////////////////////////////////////
        ///                            HAYMAKER                             ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> haymakerTicks = new ArrayList<>();
        haymakerTicks.add(new Tick(0.05F, 1.4F, -0.4F, (float)Math.PI/4F));

        class Haymaker extends Melee {
            Haymaker(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
            }
            public String getName() { return "haymaker"; }
            public boolean mayInterrupt(Command check) {
                return false;
            }
        }

        HAYMAKER = new Haymaker(0.3F, 0.3F, punchAppCycle, haymakerTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            UPPERCUT                             ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> uppercutTicks = new ArrayList<>();
        uppercutTicks.add(new Tick(0.04F, 1.4F, 0.2F, 0.2F));
        uppercutTicks.add(new Tick(0.08F, 1.5F, -0.1F, -0.1F));
        uppercutTicks.add(new Tick(0.12F, 1.4F, -0.4F, -0.4F));
        uppercutTicks.add(new Tick(0.16F, 1.05F, -0.7F, -0.8F));

        UPPERCUT = new Punch(0.3F, 0.4F, punchUpAppCycle, uppercutTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SHOVE                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle shoveAppCycle
                = new ConditionAppCycle(null, null, null);

        /* Will do these when adding collision */

        ///////////////////////////////////////////////////////////////////////
        ///                            STOMP                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp stompApp = new ConditionApp(
                0.3F, Actor.Condition.CANT_MOVE, Actor.Condition.CANT_CROUCH);
        ConditionAppCycle stompAppCycle
                = new ConditionAppCycle(stompApp, stompApp, stompApp);

        Tick footPosition = new Tick(0, 0.7F, 0.4F, 0);

        class Kick extends Punch {
            Kick(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
                warmJourney = new Journey(footPosition.getOrient(),
                        execJourney.get(0).getOrient(), warmupTime);
            }
            public String getName() { return "kick"; }
            public void start()
            {
                super.start();
                footPosition.check(-1, command.FACE);
                warmJourney.setStart(footPosition.getOrient());
            }
        }

        ArrayList<Tick> stompTicks = new ArrayList<>();
        stompTicks.add(new Tick(0.04F, 0.7F, 0F, 0));
        stompTicks.add(new Tick(0.08F, 0.7F, 0.2F, 0));
        stompTicks.add(new Tick(0.12F, 0.7F, 0.4F, 0));

        STOMP = new Kick(0.4F, 0.1F, stompAppCycle, stompTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            STOMP (FALLING)                      ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp stompFallApp = new ConditionApp(
                0.4F, Actor.Condition.CANT_MOVE, Actor.Condition.FORCE_CROUCH);
        ConditionAppCycle stompFallAppCycle
                = new ConditionAppCycle(null, stompFallApp, stompFallApp);

        /* Will do these when adding collision */

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp kickApp = new ConditionApp(
                0.2F, Actor.Condition.CANT_MOVE, Actor.Condition.CANT_CROUCH);
        ConditionAppCycle kickAppCycle
                = new ConditionAppCycle(kickApp, kickApp, kickApp);

        ArrayList<Tick> kickTicks = new ArrayList<>();
        kickTicks.add(new Tick(0.07F, 0.8F, 0F, 0F));
        kickTicks.add(new Tick(0.11F, 1.3F, 0F, 0F));
        kickTicks.add(new Tick(0.17F, 1.9F, 0F, 0F));

        KICK = new Kick(0.3F, 0.4F, kickAppCycle, kickTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (ARC)                           ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> kickArcTicks = new ArrayList<>();
        kickArcTicks.add(new Tick(0.05F, 0.5F, 0.4F, 0F));
        kickArcTicks.add(new Tick(0.09F, 1.1F, 0.2F, (float)Math.PI/4));
        kickArcTicks.add(new Tick(0.14F, 1.7F, 0F, (float)Math.PI/2));

        KICK_ARC = new Kick(0.3F, 0.4F, kickAppCycle, kickArcTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (AERIAL-FORWARD)                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp[] kickAerialApp = {
                new ConditionApp(0.1F, Actor.Condition.FORCE_CROUCH),
                new ConditionApp(0.2F, Actor.Condition.FORCE_PRONE)};
        ConditionAppCycle kickAerialCycle
                = new ConditionAppCycle(kickAerialApp[0], kickAerialApp[0], kickAerialApp[1]);

        class KickAerial extends HoldableMelee {
            KickAerial(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
                warmJourney = new Journey(footPosition.getOrient(),
                        execJourney.get(0).getOrient(), warmupTime);
            }
            public String getName() { return "kick"; }
            public void start()
            {
                super.start();
                footPosition.check(-1, command.FACE);
                warmJourney.setStart(footPosition.getOrient());
            }
            public boolean mayInterrupt(Command check) {
                return state == State.COOLDOWN;
            }
        }

        ArrayList<Tick> kickAerialForwardTicks = new ArrayList<>();
        kickAerialForwardTicks.add(new Tick(0.05F, 0.8F, 0F, 0F));
        kickAerialForwardTicks.add(new Tick(0.10F, 1.3F, 0F, 0F));
        kickAerialForwardTicks.add(new Tick(0.15F, 1.9F, 0F, 0F));

        KICK_AERIAL = new KickAerial(0.2F, 0.2F, kickAerialCycle, kickAerialForwardTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (AERIAL-DOWN-FORWARD)           ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> kickAerialDiagTicks = new ArrayList<>();
        kickAerialDiagTicks.add(new Tick(0.05F, 0.5F, 0F, (float)Math.PI/4F));
        kickAerialDiagTicks.add(new Tick(0.10F, 0.8F, 0.1F, (float)Math.PI/4F));
        kickAerialDiagTicks.add(new Tick(0.15F, 1.2F, 0.2F, (float)Math.PI/4F));

        KICK_AERIAL_DIAG = new KickAerial(0.2F, 0.2F, kickAerialCycle, kickAerialDiagTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (AERIAL-DOWN-FORWARD)           ///
        ///////////////////////////////////////////////////////////////////////

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

    /*private class Slam implements Operation
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
    }*/

    /*private class Shove extends Slam
    {
        Shove(StatusAppCycle statusAppCycle){}
    }

    private class Tackle extends Slam
    {
        Tackle(StatusAppCycle statusAppCycle){}
    }*/

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
