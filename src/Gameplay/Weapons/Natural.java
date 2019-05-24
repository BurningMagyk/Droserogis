package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Item;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;

import static Util.PolygonIntersection.isIntersect;

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

    public Natural(float xPos, float yPos, float width, float height, Actor actor)
    {
        super(xPos, yPos, width, height);
        equip(actor);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                CLASSES                                                  ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        class Punch extends Melee {
            Punch(Vec2 waits, DirEnum functionalDir, boolean useDirHorizFunctionally,
                  ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("punch", waits, functionalDir, useDirHorizFunctionally, new int[]{DURING_COOLDOWN},
                        statusAppCycle, null, execJourney); } }

        /*class Haymaker extends Melee {
            Haymaker(Vec2 waits, ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("haymaker", waits, DirEnum.NONE, true, new int[]{}, statusAppCycle, null, execJourney);
            }

            public String getName() {
                return "haymaker";
            }

            public boolean mayInterrupt(Command check) {
                return false;
            }
        }*/

        class Kick extends Melee {
            Kick(Vec2 waits, DirEnum functionalDir, ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("kick", waits, functionalDir, true, new int[]{DURING_COOLDOWN},
                        statusAppCycle, null, execJourney, execJourney[0]); }
            /*public void start() {
                super.start();
                footPosition.check(-1, command.FACE);
                warmJourney.setStart(footPosition.getOrient());
            }*/
        }

        class StompFall extends HoldableRush {
            StompFall(Vec2 waits, float minExecTime, float maxExecTime, ConditionAppCycle conditionAppCycle) {
                super(waits, minExecTime, maxExecTime, DirEnum.DOWN, conditionAppCycle, null); }

            @Override
            public void apply(Weapon _this, Item other)
            {
                if (other == null)
                {
                    if (actor.getState() == Actor.State.SWIM
                            || actor.getState().isGrounded()
                            || actor.getState().isOnWall())
                        state = State.COOLDOWN;
                }
                super.apply(_this, other);
            }
        }

        class KickAerial extends HoldableMelee {
            KickAerial(Vec2 waits, ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("aerial kick", waits, DirEnum.NONE, true, new int[]{DURING_COOLDOWN},
                        statusAppCycle, null, execJourney, execJourney[0]);
            }

            /*public void start() {
                super.start();
                footPosition.check(-1, command.FACE);
                warmJourney.setStart(footPosition.getOrient());
            }*/
        }

        class Tackle extends Rush {
            Tackle(Vec2 waits, float execTime, ConditionAppCycle conditionAppCycle) {
                super(waits, execTime, DirEnum.NONE, conditionAppCycle, null); }

            @Override
            public void apply(Weapon _this, Item other)
            {
                if (other == null)
                {
                    if (totalSec < execTime) return;
                    if (actor.getState() == Actor.State.SWIM
                            || actor.getState().isGrounded())
                        state = State.COOLDOWN;
                }
                super.apply(_this, other);
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                CONDITIONS                                               ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ConditionApp FORCE_STAND__NEGATE_RUN   =      FORCE_STAND .add(NEGATE_RUN );
        ConditionApp FORCE_STAND__NEGATE_WALK  =      FORCE_STAND .add(NEGATE_WALK);

        ConditionApp FORCE_CROUCH__NEGATE_RUN  =      FORCE_CROUCH.add(NEGATE_RUN );
        ConditionApp FORCE_CROUCH__NEGATE_WALK =      FORCE_CROUCH.add(NEGATE_WALK);

        ConditionApp NEGATE_WALK__LONG =              NEGATE_WALK .lengthen(0.4F  );

        ConditionApp FORCE_STAND__NEGATE_WALK__LONG = FORCE_STAND__NEGATE_WALK.lengthen(0.4F);

        ConditionAppCycle basicCycle = new ConditionAppCycle(
                FORCE_STAND, FORCE_STAND__NEGATE_RUN, FORCE_STAND__NEGATE_RUN);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                JOURNEYS                                                 ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Tick[][] punchJourneys = new Tick[][] {
                new Tick[] { /* PUNCH */
                        new Tick(0.05F, 0.7F, -0.2F, 0F),
                        new Tick(0.08F, 1.2F, -0.2F, 0F),
                        new Tick(0.13F, 1.7F, -0.2F, 0F) },
                new Tick[] { /* PUNCH_UP */
                        new Tick(0.05F, 0.4F, -0.1F, (float) -Math.PI / 2),
                        new Tick(0.08F, 0.4F, -0.4F, (float) -Math.PI / 2),
                        new Tick(0.13F, 0.4F, -0.8F, (float) -Math.PI / 2) },
                new Tick[] { /* PUNCH_DIAG */
                        new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI / 4),
                        new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI / 4),
                        new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI / 4) },
                new Tick[] { /* HAYMAKER */
                        new Tick(0.05F, 1.4F, -0.4F, (float) Math.PI / 4F) },
                new Tick[] { /* UPPERCUT */
                        new Tick(0.04F, 1.4F, 0.2F, 0.2F),
                        new Tick(0.08F, 1.5F, -0.1F, -0.1F),
                        new Tick(0.12F, 1.4F, -0.4F, -0.4F),
                        new Tick(0.16F, 1.05F, -0.7F, -0.8F) }
        };

        Tick[][] kickJourneys = new Tick[][] {
                new Tick[] { /* STOMP */
                        new Tick(0.04F, 0.7F, 0F, 0),
                        new Tick(0.08F, 0.7F, 0.2F, 0),
                        new Tick(0.12F, 0.7F, 0.4F, 0) },
                new Tick[] { /* KICK */
                        new Tick(0.07F, 0.8F, 0F, 0F),
                        new Tick(0.11F, 1.3F, 0F, 0F),
                        new Tick(0.17F, 1.9F, 0F, 0F) },
                new Tick[] { /* KICK_ARC */
                        new Tick(0.05F, 0.5F, 0.4F, 0F),
                        new Tick(0.09F, 1.1F, 0.2F, (float) Math.PI / 4),
                        new Tick(0.14F, 1.7F, 0F, (float) Math.PI / 2) },
                new Tick[] { /* KICK_AERIAL */
                        new Tick(0.05F, 0.8F, 0F, 0F),
                        new Tick(0.10F, 1.3F, 0F, 0F),
                        new Tick(0.15F, 1.9F, 0F, 0F) },
                new Tick[] { /* KICK_AERIAL_DIAG */
                        new Tick(0.05F, 0.5F, 0F, (float) Math.PI / 4F),
                        new Tick(0.10F, 0.8F, 0.1F, (float) Math.PI / 4F),
                        new Tick(0.15F, 1.2F, 0.2F, (float) Math.PI / 4F) },
        };

        Tick[][] grabJourneys = new Tick[][] {
                new Tick[] {
                        new Tick(0.05F, 0.7F, -0.2F, (float) Math.PI / 2F),
                        new Tick(0.08F, 1.2F, -0.2F, (float) Math.PI / 2F),
                        new Tick(0.13F, 1.7F, -0.2F, (float) Math.PI / 2F) }
        };

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                ATTACKS                                                  ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        PUNCH = new Punch(new Vec2(0.4F, 0.3F), DirEnum.NONE, true, basicCycle, punchJourneys[0]);
        PUNCH_UP = new Punch(new Vec2(0.4F, 0.3F), DirEnum.UP, false, basicCycle, punchJourneys[1]);
        PUNCH_DIAG = new Punch(new Vec2(0.4F, 0.3F), DirEnum.UP, true, basicCycle, punchJourneys[2]);

        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp forceStandApp = new ConditionApp(-0.13F, Actor.Condition.FORCE_CROUCH);
        ConditionApp punchApp = new ConditionApp(
                0.01F, Actor.Condition.NEGATE_RUN_LEFT, Actor.Condition.NEGATE_RUN_RIGHT);
        ConditionAppCycle punchAppCycle
                = new ConditionAppCycle(forceStandApp, punchApp, punchApp);



        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH (UP)                           ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp punchUpApp = new ConditionApp(
                0.01F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_RUN_RIGHT);
        ConditionAppCycle punchUpAppCycle
                = new ConditionAppCycle(forceStandApp, punchUpApp, punchUpApp);



        ///////////////////////////////////////////////////////////////////////
        ///                            PUNCH (UP-FORWARD)                   ///
        ///////////////////////////////////////////////////////////////////////



        ///////////////////////////////////////////////////////////////////////
        ///                            PUSH                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp pushApp = new ConditionApp(
                0.05F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_RUN_RIGHT);
        ConditionAppCycle pushAppCycle
                = new ConditionAppCycle(null, null, pushApp);

        PUSH = new HoldableRush(0.1F, 0.1F, 0.2F, 0,
                DirEnum.NONE, pushAppCycle, null);

        ///////////////////////////////////////////////////////////////////////
        ///                            HAYMAKER                             ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> haymakerTicks = new ArrayList<>();
        haymakerTicks.add(new Tick(0.05F, 1.4F, -0.4F, (float) Math.PI / 4F));

        HAYMAKER = new Haymaker(new Vec2(0.3F, 0.3F), punchAppCycle, punchJourneys[3]);

        ///////////////////////////////////////////////////////////////////////
        ///                            UPPERCUT                             ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp cantStandOrMove = new ConditionApp(
                0.2F, Actor.Condition.FORCE_CROUCH, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);
        ConditionAppCycle uppercutAppCycle
                = new ConditionAppCycle(cantStandOrMove, punchUpApp, punchUpApp);

        UPPERCUT = new Punch(new Vec2(0.3F, 0.4F), DirEnum.UP, true, uppercutAppCycle, punchJourneys[4]);

        ///////////////////////////////////////////////////////////////////////
        ///                            SHOVE                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle shoveAppCycle
                = new ConditionAppCycle(null, null, null);

        SHOVE = new HoldableNonMelee(0.1F, 0.1F, 0.2F, 0,
                DirEnum.NONE, shoveAppCycle, null);

        /* Will do these when adding collision */

        ///////////////////////////////////////////////////////////////////////
        ///                            STOMP                                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp stompApp = new ConditionApp(
                0.3F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);
        ConditionAppCycle stompAppCycle
                = new ConditionAppCycle(forceStandApp, stompApp, stompApp);

        Tick footPosition = new Tick(0, 0.7F, 0.4F, 0);

        STOMP = new Kick(new Vec2(0.4F, 0.1F), DirEnum.DOWN, stompAppCycle, kickJourneys[0]);

        ///////////////////////////////////////////////////////////////////////
        ///                            STOMP (FALLING)                      ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp stompFallApp = new ConditionApp(
                0.4F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT, Actor.Condition.FORCE_CROUCH);
        ConditionAppCycle stompFallAppCycle
                = new ConditionAppCycle(null, stompFallApp, stompFallApp);

        STOMP_FALL = new FallingStomp(0.1F, 0.1F, 0.5F, 0, stompFallAppCycle);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp kickApp = new ConditionApp(
                0.2F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);
        ConditionAppCycle kickAppCycle
                = new ConditionAppCycle(forceStandApp, kickApp, kickApp);

        KICK = new Kick(new Vec2(0.3F, 0.4F), DirEnum.NONE, kickAppCycle, kickJourneys[1]);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (ARC)                           ///
        ///////////////////////////////////////////////////////////////////////

        KICK_ARC = new Kick(new Vec2(0.3F, 0.4F), DirEnum.UP, kickAppCycle, kickJourneys[2]);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (AERIAL-FORWARD)                ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp[] kickAerialApp = {
                new ConditionApp(0.1F, Actor.Condition.FORCE_CROUCH),
                new ConditionApp(0.2F, Actor.Condition.FORCE_PRONE)};
        ConditionAppCycle kickAerialCycle
                = new ConditionAppCycle(kickAerialApp[0], kickAerialApp[0], kickAerialApp[1]);

        KICK_AERIAL = new KickAerial(new Vec2(0.2F, 0.2F), kickAerialCycle, kickJourneys[3]);

        ///////////////////////////////////////////////////////////////////////
        ///                            KICK (AERIAL-DOWN-FORWARD)           ///
        ///////////////////////////////////////////////////////////////////////

        KICK_AERIAL_DIAG = new KickAerial(new Vec2(0.2F, 0.2F), kickAerialCycle, kickJourneys[4]);

        ///////////////////////////////////////////////////////////////////////
        ///                            GRAB                                 ///
        ///////////////////////////////////////////////////////////////////////

        GRAB = new Punch(new Vec2(0.3F, 0.4F), DirEnum.NONE, true, punchAppCycle, grabJourneys[0]);

        ///////////////////////////////////////////////////////////////////////
        ///                            GRAB (CROUCHING)                     ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp grabCrouchApp = new ConditionApp(
                0.01F, Actor.Condition.FORCE_CROUCH, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);
        ConditionAppCycle grabCrouchCycle = new ConditionAppCycle(
                grabCrouchApp, grabCrouchApp, grabCrouchApp);

        GRAB_CROUCH = new Punch(new Vec2(0.4F, 0.4F), DirEnum.NONE, true, grabCrouchCycle, grabJourneys[0]);

        ///////////////////////////////////////////////////////////////////////
        ///                            TACKLE                               ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle tackleCycle = new ConditionAppCycle(
                new ConditionApp(0.01F, Actor.Condition.DASH),
                new ConditionApp(0.01F, Actor.Condition.NEGATE_RUN_LEFT, Actor.Condition.NEGATE_RUN_RIGHT),
                new ConditionApp(0.4F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT, Actor.Condition.FORCE_CROUCH));

        TACKLE = new Tackle(new Vec2(0.1F, 0.1F), 0.1F, tackleCycle);

        ///////////////////////////////////////////////////////////////////////
        ///                            TACKLE (LOW)                         ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle tackleLowCycle = new ConditionAppCycle(
                new ConditionApp(0.01F, Actor.Condition.DASH, Actor.Condition.FORCE_CROUCH),
                new ConditionApp(0.01F, Actor.Condition.NEGATE_RUN_LEFT, Actor.Condition.NEGATE_RUN_RIGHT, Actor.Condition.FORCE_CROUCH),
                new ConditionApp(0.4F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT, Actor.Condition.FORCE_CROUCH));

        TACKLE_LOW = new Tackle(new Vec2(0.1F, 0.1F), 0.1F, tackleLowCycle);
    }
}