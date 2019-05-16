package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Print;
import Util.Vec2;

import java.util.ArrayList;
import java.util.Collections;

public class Sword extends Weapon
{
    private Operation THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP,
            THRUST_DIAG_DOWN, THRUST_LUNGE, STAB, STAB_UNTERHAU, SWING,
            SWING_UNTERHAU, SWING_UNTERHAU_CROUCH, SWING_UP_FORWARD, SWING_UP_BACKWARD,
            SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD, SWING_LUNGE,
            SWING_LUNGE_UNTERHAU, THROW;

    @Override
    Operation getOperation(Command command, Operation currentOp)
    {
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_1)
        {
            if (command.TYPE == Command.StateType.MOMENTUM
                    && command.MOMENTUM_DIR.getHoriz().getSign() != 0)
                return setOperation(STAB, command); // with normal warm-up time
            if (command.TYPE == Command.StateType.FREE)
            {
                if (command.DIR.getHoriz().getSign() != 0)
                {
                    if (command.DIR == DirEnum.UP)
                        return setOperation(THRUST_DIAG_UP, command);
                    if (command.DIR == DirEnum.DOWN)
                        return setOperation(THRUST_DIAG_DOWN, command);
                }
                if (command.DIR == DirEnum.DOWN)
                    return setOperation(THRUST_DOWN, command);
            }
            if ((currentOp == SWING && ((Melee) currentOp).state == Operation.State.WARMUP))
                return setOperation((Melee) STAB, command, (((Melee) currentOp).totalSec)); // with reduced warm-up time
            if ((currentOp == SWING_UNTERHAU && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    || (currentOp == SWING_UNTERHAU_CROUCH && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    || (currentOp == SWING_UP_FORWARD && ((Melee) currentOp).state == Operation.State.COOLDOWN))
                return setOperation((Melee) STAB, command, false); // with half warm-up time
            if ((currentOp == SWING_UNTERHAU && ((Melee) currentOp).state == Operation.State.WARMUP)
                    || (currentOp == SWING_UNTERHAU_CROUCH && ((Melee) currentOp).state == Operation.State.WARMUP))
                return setOperation((Melee) STAB_UNTERHAU, command, ((Melee) currentOp).totalSec); // with reduced warm-up time
            if (currentOp == SWING && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                return setOperation((Melee) STAB_UNTERHAU, command, false); // with half warm-up time
            if (command.DIR.getVert() == DirEnum.UP)
            {
                if (command.DIR.getHoriz().getSign() != 0)
                    return setOperation(THRUST_DIAG_UP, command);
                return setOperation(THRUST_UP, command);
            }
            if (currentOp == SWING_UP_BACKWARD
                    && currentOp.getDir().getHoriz() != command.FACE.getHoriz())
            {
                if (((Melee) currentOp).state == Operation.State.WARMUP)
                    return setOperation((Melee) STAB, command, ((Melee) currentOp).totalSec); // with reduced warm-up time
                if (((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) STAB, command, false); // with half warm-up time
            }
            if (command.SPRINT) return setOperation(THRUST_LUNGE, command);
            return setOperation(THRUST, command);
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_2)
        {
            if (command.TYPE == Command.StateType.LOW)
            {
                if (command.SPRINT) return setOperation(SWING_LUNGE_UNTERHAU, command);
                return setOperation(SWING_UNTERHAU_CROUCH, command);
            }
            if (command.TYPE == Command.StateType.MOMENTUM
                    && command.MOMENTUM_DIR.getHoriz().getSign() != 0)
            {
                if (currentOp == SWING_UP_FORWARD
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_UP_BACKWARD, command, false); // with half warm-up time
                if (currentOp == SWING_UP_BACKWARD
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_UP_FORWARD, command, false); // with half warm-up time
                return setOperation(SWING_UP_FORWARD, command); // with normal warm-up time
            }
            if (command.DIR == DirEnum.UP)
            {
                if ((currentOp == SWING_UNTERHAU || currentOp == SWING_UNTERHAU_CROUCH
                        || currentOp == STAB_UNTERHAU)
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_UP_BACKWARD, command, true); // with no warm-up time
                if (currentOp == SWING_UP_FORWARD
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_UP_BACKWARD, command, false); // with half warm-up time
                if (currentOp == SWING_UP_BACKWARD
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_UP_FORWARD, command, false); // with half warm-up time
                return setOperation(SWING_UP_FORWARD, command); // with normal warm-up time
            }
            if (command.DIR == DirEnum.DOWN)
            {
                if ((currentOp == SWING || currentOp == STAB)
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_DOWN_BACKWARD, command, true); // with no warm-up time
                if (currentOp == SWING_DOWN_FORWARD
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_DOWN_FORWARD, command, false); // with half warm-up time
                if (currentOp == SWING_DOWN_BACKWARD
                        && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                    return setOperation((Melee) SWING_DOWN_FORWARD, command, false); // with half warm-up time
                return setOperation(SWING_DOWN_FORWARD, command); // with normal warm-up time
            }
            if (currentOp == SWING
                    && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                return setOperation((Melee) SWING_UNTERHAU, command, false); // with half warm-up time
            if ((currentOp == SWING_UNTERHAU || currentOp == SWING_UNTERHAU_CROUCH)
                    && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                return setOperation((Melee) SWING, command, false); // with half warm-up time
            if (currentOp == SWING_UP_FORWARD
                    && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                return setOperation((Melee) SWING, command, true); // with no warm-up time
            if (currentOp == SWING_DOWN_FORWARD
                    && ((Melee) currentOp).state == Operation.State.COOLDOWN)
                return setOperation((Melee) SWING_UNTERHAU, command, true); // with no warm-up time
            if (currentOp == SWING_UP_BACKWARD
                    && currentOp.getDir().getHoriz() != command.FACE.getHoriz())
                return setOperation((Melee) SWING, command, true); // with no warm-up time
            if (currentOp == SWING_DOWN_BACKWARD
                    && currentOp.getDir().getHoriz() != command.FACE.getHoriz())
                return setOperation((Melee) SWING_UNTERHAU, command, true); // with no warm-up time
            if (command.SPRINT) return setOperation(SWING_LUNGE, command);
            return setOperation(SWING, command); // with normal warm-up time
        }

        if (command.ATTACK_KEY == Actor.ATTACK_KEY_2 + Actor.ATTACK_KEY_MOD)
        {
            return setOperation(THROW, command);
        }

        return null;
    }

    @Override
    boolean isApplicable(Command command)
    {
        if (command.ATTACK_KEY == Actor.ATTACK_KEY_3
                || command.ATTACK_KEY == Actor.ATTACK_KEY_3 + Actor.ATTACK_KEY_MOD
                || command.ATTACK_KEY == Actor.ATTACK_KEY_1 + Actor.ATTACK_KEY_MOD)
            return false;
        return true;
    }

    @Override
    Orient getDefaultOrient()
    {
        return new Orient(new Vec2(1F, -0.2F), (float) (-Math.PI / 4F));
    }
    public Sword(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                CLASSES                                                  ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /* THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN */
        class Thrust extends HoldableMelee {
            Thrust(Vec2 waits, DirEnum functionalDir, boolean useDirHorizFunctionaly,
                   ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("thrust", waits, functionalDir, useDirHorizFunctionaly, new int[]{DURING_COOLDOWN},
                        statusAppCycle, null, execJourney); } }

        /* THRUST_LUNGE, STAB, STAB_UNTERHAU */
        class Stab extends Melee {
            Stab(Vec2 waits, DirEnum functionalDir, ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("stab", waits, functionalDir, true, new int[]{DURING_COOLDOWN},
                        statusAppCycle, null, execJourney); } }

        /* SWING, SWING_UNTERHAU (+ _CROUCH), SWING_UP_FORWARD, SWING_UP_BACKWARD,
           SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD, SWING_LUNGE, SWING_LUNGE_UNTERHAU, */
        class Swing extends Melee {
            Swing(Vec2 waits, DirEnum functionalDir, ConditionAppCycle statusAppCycle, Tick[] execJourney) {
                super("swing", waits, functionalDir, true, new int[]{Actor.ATTACK_KEY_1},
                        statusAppCycle, null, execJourney); }
            /*public boolean mayInterrupt(Command check) {
                if (check.ATTACK_KEY == Actor.ATTACK_KEY_1
                        && state == State.WARMUP) return true;
                return state == State.COOLDOWN;
            }*/}

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                CONDITIONS                                               ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ConditionApp forceStand = new ConditionApp(0.1F, Actor.Condition.FORCE_STAND);
        ConditionApp forceStand_long = new ConditionApp(forceStand, 0.4F);
        ConditionApp forceDash = new ConditionApp(0.01F, Actor.Condition.DASH);
        ConditionApp negateRun = new ConditionApp(0.01F, Actor.Condition.NEGATE_RUN_LEFT, Actor.Condition.NEGATE_RUN_RIGHT);
        ConditionApp negateRun_forceStand = new ConditionApp(negateRun, Actor.Condition.FORCE_STAND);
        ConditionApp negateRun_forceCrouch = new ConditionApp(negateRun, Actor.Condition.FORCE_CROUCH);
        ConditionApp negateWalk = new ConditionApp(0.01F, Actor.Condition.NEGATE_WALK_LEFT, Actor.Condition.NEGATE_WALK_RIGHT);
        ConditionApp negateWalk_long = new ConditionApp(negateWalk, 0.4F);
        ConditionApp negateWalk_forceStand = new ConditionApp(negateWalk, Actor.Condition.FORCE_STAND);
        ConditionApp negateWalk_forceStand_long = new ConditionApp(negateWalk_forceStand, 0.4F);
        ConditionApp negateWalk_forceCrouch = new ConditionApp(negateWalk, Actor.Condition.FORCE_CROUCH);

        /* THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN, SWING, SWING_UNTERHAU,
           SWING_UP_FORWARD, SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD */
        ConditionAppCycle basicCycle = new ConditionAppCycle(
                forceStand, negateRun_forceStand, negateRun_forceStand);

        /* STAB, STAB_UNTERHAU */
        ConditionAppCycle a_Cycle = new ConditionAppCycle(
                negateWalk_forceStand, negateWalk_forceStand, negateWalk_forceStand);

        /* SWING_UNTERHAU_CROUCH */
        ConditionAppCycle b_Cycle = new ConditionAppCycle(
                negateWalk_forceCrouch, negateRun_forceStand, negateWalk_long);

        /* THRUST_LUNGE, SWING_LUNGE */
        ConditionAppCycle lungeCycle = new ConditionAppCycle(
                forceDash, negateRun_forceStand, negateWalk_forceStand_long);

        /* SWING_LUNGE_UNTERHAU */
        ConditionAppCycle lungeCrouchCycle = new ConditionAppCycle(
                forceDash, negateRun_forceCrouch, negateWalk_forceStand_long);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                JOURNEYS                                                 ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Tick[][] thrustJourneys = new Tick[][] {
                new Tick[] { /* THRUST, THRUST_LUNGE */
                        new Tick(0.06F, 0.8F, -0.2F, 0F),
                        new Tick(0.10F, 1.4F, -0.2F, 0F),
                        new Tick(0.16F, 2F, -0.2F, 0F) },
                new Tick[] { /* THRUST_UP */
                        new Tick(0.06F, 0.4F, -0.4F, (float) -Math.PI/2),
                        new Tick(0.10F, 0.4F, -0.7F, (float) -Math.PI/2),
                        new Tick(0.16F, 0.4F, -1F, (float) -Math.PI/2) },
                null /* THRUST_DOWN */,
                new Tick[] { /* THRUST_DIAG_UP */
                        new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI/4),
                        new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI/4),
                        new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI/4) },
                null /* THRUST_DIAG_DOWN */
        };

        thrustJourneys[2] = new Tick[thrustJourneys[1].length];
        for (int i = 0; i < thrustJourneys[1].length; i++)
        {
            Tick tickCopy = thrustJourneys[1][i].getMirrorCopy(false, true);
            tickCopy.getOrient().addTheta((float) Math.PI / 2);
            thrustJourneys[2][i] = tickCopy;
        }
        thrustJourneys[4] = new Tick[thrustJourneys[3].length];
        for (int i = 0; i < thrustJourneys[3].length; i++)
            thrustJourneys[4][i] = thrustJourneys[3][i].getMirrorCopy(false, true);

        Tick[][] stabJourneys = new Tick[][] {
                new Tick[] { /* STAB */
                        new Tick(0.04F, 1.1F, -0.6F, (float) Math.PI/2),
                        new Tick(0.08F, 1.1F, -0.1F, (float) Math.PI/2),
                        new Tick(0.12F, 1.1F, 0.4F, (float) Math.PI/2) },
                new Tick[] { /* STAP_UNTERHAU */
                        new Tick(0.04F, 1.3F, 0F, (float) -Math.PI/2),
                        new Tick(0.08F, 1.3F, -0.5F, (float) -Math.PI/2),
                        new Tick(0.12F, 1.3F, -1F, (float) -Math.PI/2) }
        };

        Tick[][] swingJourneys = new Tick[][] {
                new Tick[] { /* SWING */
                        new Tick(0.04F, 1.05F, -0.7F, -0.8F),
                        new Tick(0.08F, 1.4F, -0.4F, -0.4F),
                        new Tick(0.12F, 1.5F, -0.1F, -0.1F),
                        new Tick(0.16F, 1.4F, 0.2F, 0.2F) },
                new Tick[] { /* SWING_UNTERHAU (+ _CROUCH) */
                        new Tick(0.04F, 1.4F, 0.2F, 0.2F),
                        new Tick(0.08F, 1.5F, -0.1F, -0.1F),
                        new Tick(0.12F, 1.4F, -0.4F, -0.4F),
                        new Tick(0.16F, 1.05F, -0.7F, -0.8F) },
                new Tick[] { /* SWING_UP_FORWARD */
                        new Tick(0.04F,  -0.8F,-0.6F, -2F),
                        new Tick(0.08F,  -0.2F,-0.85F, -1.5F),
                        new Tick(0.12F,  0.4F,-0.85F, -1F),
                        new Tick(0.16F,  1.05F,-0.7F, -0.5F) },
                new Tick[] { /* SWING_UP_BACKWARD */
                        new Tick(0.04F,  1.05F,-0.7F, -0.5F),
                        new Tick(0.08F,  0.4F,-0.85F, -1F),
                        new Tick(0.12F,  -0.2F,-0.85F, -1.5F),
                        new Tick(0.16F,  -0.8F,-0.6F, -2F) },
                null /* SWING_DOWN_FORWARD */,
                null /* SWING_DOWN_FORWARD */,
                null /* SWING_LUNGE */ ,
                null /* SWING_LUNGE_UNTERHAU */
        };

        swingJourneys[4] = new Tick[swingJourneys[2].length];
        for (int i = 0; i < swingJourneys[4].length; i++)
            swingJourneys[4][i] = swingJourneys[2][i].getMirrorCopy(false, true);
        swingJourneys[5] = new Tick[swingJourneys[3].length];
        for (int i = 0; i < swingJourneys[5].length; i++)
            swingJourneys[5][i] = swingJourneys[3][i].getMirrorCopy(false, true);

        swingJourneys[6] = new Tick[swingJourneys[2].length + swingJourneys[0].length];
        for (int i = 0; i < swingJourneys[2].length; i++)
            swingJourneys[6][i] = swingJourneys[2][i].getTimeModdedCopy(0, 0.75F);
        float timeAdd_6 = swingJourneys[2][swingJourneys[2].length - 1].totalSec;
        for (int i = swingJourneys[2].length; i < swingJourneys[6].length; i++)
            swingJourneys[6][i] = swingJourneys[0][i].getTimeModdedCopy(timeAdd_6, 0.75F);
        swingJourneys[7] = new Tick[swingJourneys[4].length + swingJourneys[1].length];
        for (int i = 0; i < swingJourneys[4].length; i++)
            swingJourneys[7][i] = swingJourneys[4][i].getTimeModdedCopy(0, 0.75F);
        float timeAdd_7 = swingJourneys[4][swingJourneys[4].length - 1].totalSec;
        for (int i = swingJourneys[4].length; i < swingJourneys[7].length; i++)
            swingJourneys[7][i] = swingJourneys[1][i].getTimeModdedCopy(timeAdd_7, 0.75F);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///                                                ATTACKS                                                  ///
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        THRUST = new Thrust(new Vec2(0.6F, 0.3F), DirEnum.NONE, true, basicCycle, thrustJourneys[0]);
        THRUST_UP = new Thrust(new Vec2(0.6F, 0.3F), DirEnum.UP, false, basicCycle, thrustJourneys[1]);
        THRUST_DOWN = new Thrust(new Vec2(0.6F, 0.3F), DirEnum.DOWN, false, basicCycle, thrustJourneys[2]);
        THRUST_DIAG_UP = new Thrust(new Vec2(0.6F, 0.3F), DirEnum.UP, true, basicCycle, thrustJourneys[3]);
        THRUST_DIAG_DOWN = new Thrust(new Vec2(0.6F, 0.3F), DirEnum.DOWN, true, basicCycle, thrustJourneys[4]);
        THRUST_LUNGE = new Stab(new Vec2(0.6F, 0.3F), DirEnum.NONE, lungeCycle, thrustJourneys[0]);

        STAB = new Stab(new Vec2(0.6F, 0.3F), DirEnum.DOWN, a_Cycle, stabJourneys[0]);
        STAB_UNTERHAU = new Stab(new Vec2(0.6F, 0.3F), DirEnum.UP, a_Cycle, stabJourneys[1]);

        SWING = new Swing(new Vec2(0.6F, 0.3F), DirEnum.DOWN, basicCycle, swingJourneys[0]);
        SWING_UNTERHAU = new Swing(new Vec2(0.6F, 0.3F), DirEnum.UP, basicCycle, swingJourneys[1]);
        SWING_UNTERHAU_CROUCH = new Swing(new Vec2(0.6F, 0.3F), DirEnum.UP, b_Cycle, swingJourneys[1]);
        SWING_UP_FORWARD = new Swing(new Vec2(0.6F, 0.3F), DirEnum.UP, basicCycle, swingJourneys[2]);
        SWING_UP_BACKWARD = new Swing(new Vec2(0.6F, 0.3F), DirEnum.UP, basicCycle, swingJourneys[3]);
        SWING_DOWN_FORWARD = new Swing(new Vec2(0.6F, 0.3F), DirEnum.DOWN, basicCycle, swingJourneys[4]);
        SWING_DOWN_BACKWARD = new Swing(new Vec2(0.6F, 0.3F), DirEnum.DOWN, basicCycle, swingJourneys[5]);
        SWING_LUNGE = new Swing(new Vec2(0.6F, 0.3F), DirEnum.DOWN, lungeCycle, swingJourneys[6]);
        SWING_LUNGE_UNTERHAU = new Swing(new Vec2(0.6F, 0.3F), DirEnum.UP, lungeCrouchCycle, swingJourneys[7]);
    }
}