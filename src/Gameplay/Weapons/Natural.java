package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

public class Natural extends Weapon
{
    private Operation PUNCH, PUNCH_UP, PUNCH_DIAG, PUSH, HAYMAKER, UPPERCUT,
            SHOVE, STOMP, STOMP_FALL, KICK, KICK_ARC, KICK_AERIAL,
            KICK_AERIAL_DIAG, GRAB, GRAB_CROUCH, TACKLE, TACKLE_LOW;

    @Override
    Operation getOperation(Command command, Operation currentOp)
    {
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_1)
        {
            if (command.TYPE == Command.StateType.LOW)
                return setOperation(UPPERCUT, command);
            /* if (command.TYPE != Command.StateType.LOW) */
            if (command.DIR == DirEnum.UP)
                return setOperation(PUNCH_UP, command);
            if (command.DIR.getVert() == DirEnum.UP
                    && command.DIR.getHoriz().getSign() != 0)
                return setOperation(PUNCH_DIAG, command);
            if (command.SPRINT) return setOperation(PUSH, command);
            return setOperation(PUNCH, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_2)
        {
            if (command.TYPE == Command.StateType.LOW)
                return setOperation(UPPERCUT, command);
            if (command.SPRINT) return setOperation(SHOVE, command);
            return setOperation(HAYMAKER, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3)
        {
            if (command.TYPE == Command.StateType.STANDARD)
            {
                if (command.DIR.getHoriz() == DirEnum.NONE)
                    return setOperation(STOMP, command);
                return setOperation(KICK_ARC, command);
            }
            if (command.TYPE == Command.StateType.MOMENTUM)
            {
                if (command.MOMENTUM_DIR.getVert() == DirEnum.DOWN)
                {
                    if (command.DIR.getHoriz().getSign() != 0
                            && command.DIR.getVert() != DirEnum.DOWN)
                        return setOperation(KICK_AERIAL, command);
                    return setOperation(STOMP_FALL, command);
                }
                if (command.DIR.getVert() == DirEnum.DOWN)
                    return setOperation(KICK_AERIAL_DIAG, command);
                return setOperation(KICK_AERIAL, command);
            }
            if (command.TYPE == Command.StateType.FREE)
            {
                if (command.DIR.getVert() == DirEnum.DOWN)
                    return setOperation(KICK_AERIAL_DIAG, command);
                return setOperation(KICK_AERIAL, command);
            }
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_1 + Actor.ATTACK_KEY_MOD)
        {
            if (command.TYPE == Command.StateType.LOW)
            {
                if (command.SPRINT)
                    return setOperation(TACKLE_LOW, command);
                return setOperation(GRAB_CROUCH, command);
            }
            if (command.TYPE == Command.StateType.STANDARD)
            {
                if (command.SPRINT)
                    return setOperation(TACKLE, command);
                return setOperation(GRAB, command);
            }
            if (command.TYPE == Command.StateType.MOMENTUM
                    && command.MOMENTUM_DIR.getVert() == DirEnum.DOWN)
                return setOperation(TACKLE, command);
            return setOperation(GRAB, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3 + Actor.ATTACK_KEY_MOD)
        {
            return setOperation(KICK, command);
        }

        return null;
    }

    @Override
    boolean isApplicable(Command command) { return true; }

    @Override
    Orient getDefaultOrient()
    {
        return new Orient(new Vec2(0.8F, 0), 0);
    }
    public Natural(float xPos, float yPos, float width, float height, Actor actor) {
        super(xPos, yPos, width, height);
        equip(actor);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH                                ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> punchTicks = new ArrayList<>();
        punchTicks.add(new Tick(0.05F, 0.7F, -0.2F, 0F));
        punchTicks.add(new Tick(0.08F, 1.2F, -0.2F, 0F));
        punchTicks.add(new Tick(0.13F, 1.7F, -0.2F, 0F));

        ConditionApp punchApp = new ConditionApp(
                0.01F, Actor.Condition.FORCE_STAND, Actor.Condition.SLOW_RUN);
        ConditionAppCycle punchAppCycle
                = new ConditionAppCycle(punchApp, punchApp, punchApp);

        class Punch extends Melee {
            Punch(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
            }

            public String getName() { return "punch"; }

            public boolean mayInterrupt(Command check) { return state == State.COOLDOWN; }

            public void apply(Item other)
            {
                if (other == null || other == actor) return;
                for (Weapon weapon : actor.weapons) { if (other == weapon) return; }
                // TODO: check for collision then apply effect
                DirEnum dir = getDir().getHoriz();
                if (dir == DirEnum.LEFT && other.getX() < actor.getX())
                {
                    appliedItems.add(other);
                    Print.green(other.testingAttacks("left punch"));
                }
                else if (dir == DirEnum.RIGHT && other.getX() > actor.getX())
                {
                    appliedItems.add(other);
                    Print.blue(other.testingAttacks("right punch"));
                }
            }
        }

        PUNCH = new Punch(0.4F, 0.3F, punchAppCycle, punchTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH (UP)                           ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> punchUpTicks = new ArrayList<>();
        punchUpTicks.add(new Tick(0.05F, 0.4F, -0.1F, (float) -Math.PI / 2));
        punchUpTicks.add(new Tick(0.08F, 0.4F, -0.4F, (float) -Math.PI / 2));
        punchUpTicks.add(new Tick(0.13F, 0.4F, -0.8F, (float) -Math.PI / 2));

        ConditionApp punchUpApp = new ConditionApp(
                0.01F, Actor.Condition.FORCE_STAND, Actor.Condition.IGNORE_MOVE);
        ConditionAppCycle punchUpAppCycle
                = new ConditionAppCycle(punchUpApp, punchUpApp, punchUpApp);

        PUNCH_UP = new Punch(0.4F, 0.3F, punchUpAppCycle, punchUpTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH (UP-FORWARD)                   ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> punchDiagTicks = new ArrayList<>();
        punchDiagTicks.add(new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI / 4));
        punchDiagTicks.add(new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI / 4));
        punchDiagTicks.add(new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI / 4));

        PUNCH_DIAG = new Punch(0.4F, 0.3F, punchAppCycle, punchDiagTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUSH                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp pushApp = new ConditionApp(
                0.05F, Actor.Condition.IGNORE_MOVE);
        ConditionAppCycle pushAppCycle
                = new ConditionAppCycle(null, null, pushApp);

        class Push extends HoldableNonMelee {
            Push(float warmupTime, float cooldownTime,
                            float minExecTime, float maxExecTime, ConditionAppCycle conditionAppCycle) {
                super(warmupTime, cooldownTime, minExecTime, maxExecTime, conditionAppCycle);
            }

            @Override
            public void apply(Item other)
            {
                if (other == null || other == actor) return;
                for (Weapon weapon : actor.weapons) { if (other == weapon) return; }
                if (appliedItems.contains(other) || !withinBounds(other)) return;
                DirEnum dir = getDir().getHoriz();
                if (dir == DirEnum.LEFT && other.getX() < actor.getX()
                        && other.getVelocityX() > actor.getVelocityX())
                {
                    appliedItems.add(other);
                    Print.green(other.testingAttacks("left push"));
                }
                else if (dir == DirEnum.RIGHT && other.getX() > actor.getX()
                        && other.getVelocityX() < actor.getVelocityX())
                {
                    appliedItems.add(other);
                    Print.blue(other.testingAttacks("right push"));
                }
            }
        }

        PUSH = new Push(0.1F, 0.1F, 0.2F, 0, pushAppCycle);

        ///////////////////////////////////////////////////////////////////////
        ///                            HAYMAKER                             ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> haymakerTicks = new ArrayList<>();
        haymakerTicks.add(new Tick(0.05F, 1.4F, -0.4F, (float) Math.PI / 4F));

        class Haymaker extends Melee {
            Haymaker(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
            }

            public String getName() {
                return "haymaker";
            }

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

        ConditionApp cantStandOrMove = new ConditionApp(
                0.2F, Actor.Condition.FORCE_CROUCH, Actor.Condition.IGNORE_MOVE);
        ConditionAppCycle uppercutAppCycle
                = new ConditionAppCycle(cantStandOrMove, punchUpApp, punchUpApp);

        UPPERCUT = new Punch(0.3F, 0.4F, uppercutAppCycle, uppercutTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SHOVE                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle shoveAppCycle
                = new ConditionAppCycle(null, null, null);

        class Shove extends HoldableNonMelee {
            Shove(float warmupTime, float cooldownTime,
                 float minExecTime, float maxExecTime, ConditionAppCycle conditionAppCycle) {
                super(warmupTime, cooldownTime, minExecTime, maxExecTime, conditionAppCycle);
            }

            @Override
            public void apply(Item other)
            {
                if (other == null || other == actor) return;
                for (Weapon weapon : actor.weapons) { if (other == weapon) return; }
                if (appliedItems.contains(other) || !withinBounds(other)) return;
                DirEnum dir = getDir().getHoriz();
                if (dir == DirEnum.LEFT && other.getX() < actor.getX()
                        && other.getVelocityX() > actor.getVelocityX())
                {
                    appliedItems.add(other);
                    Print.green(other.testingAttacks("left shove"));
                }
                else if (dir == DirEnum.RIGHT && other.getX() > actor.getX()
                        && other.getVelocityX() < actor.getVelocityX())
                {
                    appliedItems.add(other);
                    Print.blue(other.testingAttacks("right shove"));
                }
            }
        }

        SHOVE = new Shove(0.1F, 0.1F, 0.2F, 0, shoveAppCycle);

        /* Will do these when adding collision */

        ///////////////////////////////////////////////////////////////////////
        ///                            STOMP                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp stompApp = new ConditionApp(
                0.3F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_STAND);
        ConditionAppCycle stompAppCycle
                = new ConditionAppCycle(stompApp, stompApp, stompApp);

        Tick footPosition = new Tick(0, 0.7F, 0.4F, 0);

        class Kick extends Punch {
            Kick(float warmupTime, float cooldownTime, ConditionAppCycle statusAppCycle, ArrayList<Tick> execJourney) {
                super(warmupTime, cooldownTime, statusAppCycle, execJourney);
                warmJourney = new Journey(footPosition.getOrient(),
                        execJourney.get(0).getOrient(), warmupTime);
            }

            public String getName() {
                return "kick";
            }

            public void start() {
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
                0.4F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_CROUCH);
        ConditionAppCycle stompFallAppCycle
                = new ConditionAppCycle(null, stompFallApp, stompFallApp);

        class FallingStomp extends HoldableNonMelee {
            FallingStomp(float warmupTime, float cooldownTime,
                  float minExecTime, float maxExecTime, ConditionAppCycle conditionAppCycle) {
                super(warmupTime, cooldownTime, minExecTime, maxExecTime, conditionAppCycle);
            }

            @Override
            public void apply(Item other)
            {
                if (other == null)
                {
                    if (actor.getState() == Actor.State.SWIM
                            || actor.getState().isGrounded()
                            || actor.getState().isOnWall())
                        state = State.COOLDOWN;
                }
                if (other == actor) return;
                for (Weapon weapon : actor.weapons) { if (other == weapon) return; }
                if (appliedItems.contains(other) || !withinBounds(other)) return;
                if (other.getY() > actor.getY()
                        && other.getVelocityY() < actor.getVelocityY())
                {
                    appliedItems.add(other);
                    Print.green(other.testingAttacks("falling stomp"));
                }
            }
        }

        STOMP_FALL = new FallingStomp(0.1F, 0.1F, 0.5F, 0, stompFallAppCycle);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp kickApp = new ConditionApp(
                0.2F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_STAND);
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
        kickArcTicks.add(new Tick(0.09F, 1.1F, 0.2F, (float) Math.PI / 4));
        kickArcTicks.add(new Tick(0.14F, 1.7F, 0F, (float) Math.PI / 2));

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

            public String getName() {
                return "kick";
            }

            public void start() {
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
        kickAerialDiagTicks.add(new Tick(0.05F, 0.5F, 0F, (float) Math.PI / 4F));
        kickAerialDiagTicks.add(new Tick(0.10F, 0.8F, 0.1F, (float) Math.PI / 4F));
        kickAerialDiagTicks.add(new Tick(0.15F, 1.2F, 0.2F, (float) Math.PI / 4F));

        KICK_AERIAL_DIAG = new KickAerial(0.2F, 0.2F, kickAerialCycle, kickAerialDiagTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            GRAB                                 ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> grabTicks = new ArrayList<>();
        grabTicks.add(new Tick(0.05F, 0.7F, -0.2F, (float) Math.PI / 2F));
        grabTicks.add(new Tick(0.08F, 1.2F, -0.2F, (float) Math.PI / 2F));
        grabTicks.add(new Tick(0.13F, 1.7F, -0.2F, (float) Math.PI / 2F));

        GRAB = new Punch(0.3F, 0.4F, punchAppCycle, grabTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            GRAB (CROUCHING)                     ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp grabCrouchApp = new ConditionApp(
                0.01F, Actor.Condition.FORCE_CROUCH, Actor.Condition.IGNORE_MOVE);
        ConditionAppCycle grabCrouchCycle = new ConditionAppCycle(
                grabCrouchApp, grabCrouchApp, grabCrouchApp);

        GRAB_CROUCH = new Punch(0.4F, 0.4F, grabCrouchCycle, grabTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            TACKLE                               ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle tackleCycle = new ConditionAppCycle(
                new ConditionApp(0.01F, Actor.Condition.FORCE_DASH),
                new ConditionApp(0.01F, Actor.Condition.SLOW_RUN),
                new ConditionApp(0.4F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_CROUCH));

        class Tackle extends NonMelee {
            Tackle(float warmupTime, float cooldownTime, float execTime, ConditionAppCycle conditionAppCycle) {
                super(warmupTime, cooldownTime, execTime, conditionAppCycle);
            }

            @Override
            public void apply(Item other)
            {
                if (other == null)
                {
                    if (totalSec < execTime) return;
                    if (actor.getState() == Actor.State.SWIM
                            || actor.getState().isGrounded())
                        state = State.COOLDOWN;
                }
                if (other == actor) return;
                for (Weapon weapon : actor.weapons) { if (other == weapon) return; }
                if (appliedItems.contains(other) || !withinBounds(other)) return;
                DirEnum dir = getDir().getHoriz();
                if (dir == DirEnum.LEFT && other.getX() < actor.getX()
                        && other.getVelocityX() > actor.getVelocityX())
                {
                    appliedItems.add(other);
                    Print.green(other.testingAttacks("left tackle"));
                }
                else if (dir == DirEnum.RIGHT && other.getX() > actor.getX()
                        && other.getVelocityX() < actor.getVelocityX())
                {
                    appliedItems.add(other);
                    Print.blue(other.testingAttacks("right tackle"));
                }
            }
        }

        TACKLE = new Tackle(0.1F, 0.1F, 0.1F, tackleCycle);

        ///////////////////////////////////////////////////////////////////////
        ///                            TACKLE (LOW)                         ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle tackleLowCycle = new ConditionAppCycle(
                new ConditionApp(0.01F, Actor.Condition.FORCE_DASH, Actor.Condition.FORCE_CROUCH),
                new ConditionApp(0.01F, Actor.Condition.SLOW_RUN, Actor.Condition.FORCE_CROUCH),
                new ConditionApp(0.4F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_CROUCH));

        TACKLE_LOW = new Tackle(0.1F, 0.1F, 0.1F, tackleLowCycle);
    }
}