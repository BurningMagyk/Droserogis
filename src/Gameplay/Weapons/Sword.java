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
            SWING_UNTERHAU, SWING_UP_FORWARD, SWING_UP_BACKWARD,
            SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD, SWING_LUNGE,
            SWING_LUNGE_UNTERHAU;

    @Override
    Operation getOperation(Command command, Operation currentOp)
    {


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

        ///////////////////////////////////////////////////////////////////////
        ///                            THRUST                               ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp slowRunApp = new ConditionApp(
                0.01F, Actor.Condition.SLOW_RUN);
        ConditionAppCycle slowRunCycle = new ConditionAppCycle(
                slowRunApp, slowRunApp, slowRunApp);

        ArrayList<Tick> thrustTicks = new ArrayList<>();
        thrustTicks.add(new Tick(0.06F, 0.8F, -0.2F, 0F));
        thrustTicks.add(new Tick(0.10F, 1.4F, -0.2F, 0F));
        thrustTicks.add(new Tick(0.16F, 2F, -0.2F, 0F));

        THRUST = new HoldableMelee(0.6F, 0.3F, slowRunCycle, thrustTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            THRUST_UP                            ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> thrustUpTicks = new ArrayList<>();
        thrustUpTicks.add(new Tick(0.06F, 0.4F, -0.4F, (float) -Math.PI/2));
        thrustUpTicks.add(new Tick(0.10F, 0.4F, -0.7F, (float) -Math.PI/2));
        thrustUpTicks.add(new Tick(0.16F, 0.4F, -1F, (float) -Math.PI/2));

        THRUST_UP = new HoldableMelee(0.6F, 0.3F, slowRunCycle, thrustUpTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            THRUST_DOWN                          ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> thrustDownTicks = new ArrayList<>();
        for (Tick tick : thrustUpTicks)
        {
            Tick tickCopy = tick.getMirrorCopy(false, true);
            tickCopy.getOrient().addTheta((float) Math.PI / 2);
            thrustDownTicks.add(tickCopy);
        }

        THRUST_DOWN = new HoldableMelee(0.6F, 0.3F, slowRunCycle, thrustDownTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            THRUST_DIAG_UP                       ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> thrustDiagUpTicks = new ArrayList<>();
        thrustDiagUpTicks.add(new Tick(0.06F, 0.8F, -0.35F, (float) -Math.PI/4));
        thrustDiagUpTicks.add(new Tick(0.10F, 1.2F, -0.6F, (float) -Math.PI/4));
        thrustDiagUpTicks.add(new Tick(0.16F, 1.6F, -0.85F, (float) -Math.PI/4));

        THRUST_DIAG_UP = new HoldableMelee(0.6F, 0.3F, slowRunCycle, thrustDiagUpTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            THRUST_DIAG_DOWN                     ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> thrustDiagDownTicks = new ArrayList<>();
        for (Tick tick : thrustDiagUpTicks)
        {
            thrustDiagDownTicks.add(tick.getMirrorCopy(false, true));
        }

        THRUST_DIAG_DOWN = new HoldableMelee(0.6F, 0.3F, slowRunCycle, thrustDiagDownTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            THRUST_LUNGE                         ///
        ///////////////////////////////////////////////////////////////////////

        ConditionAppCycle lungeCycle = new ConditionAppCycle(
                new ConditionApp(0.01F, Actor.Condition.FORCE_DASH),
                new ConditionApp(0.01F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_STAND),
                new ConditionApp(0.4F, Actor.Condition.IGNORE_MOVE, Actor.Condition.FORCE_STAND));

        THRUST_LUNGE = new Melee(0.6F, 0.3F, lungeCycle, thrustTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            STAB                                 ///
        ///////////////////////////////////////////////////////////////////////

        ConditionApp ignoreMoveApp = new ConditionApp(
                0.01F, Actor.Condition.IGNORE_MOVE);
        ConditionAppCycle ignoreMoveCycle = new ConditionAppCycle(
                ignoreMoveApp, ignoreMoveApp, ignoreMoveApp);

        ArrayList<Tick> stabTicks = new ArrayList<>();
        stabTicks.add(new Tick(0.04F, 1.1F, -0.6F, (float) Math.PI/2));
        stabTicks.add(new Tick(0.08F, 1.1F, -0.1F, (float) Math.PI/2));
        stabTicks.add(new Tick(0.12F, 1.1F, 0.4F, (float) Math.PI/2));

        STAB = new Melee(0.6F, 0.3F, ignoreMoveCycle, stabTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            STAB_UNTERHAU                        ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> stabUnterhauTicks = new ArrayList<>();
        stabUnterhauTicks.add(new Tick(0.04F, 1.3F, 0F, (float) -Math.PI/2));
        stabUnterhauTicks.add(new Tick(0.08F, 1.3F, -0.5F, (float) -Math.PI/2));
        stabUnterhauTicks.add(new Tick(0.12F, 1.3F, -1F, (float) -Math.PI/2));

        STAB_UNTERHAU = new Melee(0.6F, 0.3F, ignoreMoveCycle, stabUnterhauTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING                                ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingTicks = new ArrayList<>();
        swingTicks.add(new Tick(0.04F, 1.05F, -0.7F, -0.8F));
        swingTicks.add(new Tick(0.08F, 1.4F, -0.4F, -0.4F));
        swingTicks.add(new Tick(0.12F, 1.5F, -0.1F, -0.1F));
        swingTicks.add(new Tick(0.16F, 1.4F, 0.2F, 0.2F));

        SWING = new Melee(0.6F, 0.3F, slowRunCycle, swingTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_UNTERHAU                       ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingUnterhauTicks = new ArrayList<>();
        swingUnterhauTicks.add(new Tick(0.04F, 1.4F, 0.2F, 0.2F));
        swingUnterhauTicks.add(new Tick(0.08F, 1.5F, -0.1F, -0.1F));
        swingUnterhauTicks.add(new Tick(0.12F, 1.4F, -0.4F, -0.4F));
        swingUnterhauTicks.add(new Tick(0.16F, 1.05F, -0.7F, -0.8F));

        SWING_UNTERHAU = new Melee(0.6F, 0.3F, slowRunCycle, swingUnterhauTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_UP_FORWARD                     ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingUpForwardTicks = new ArrayList<>();
        swingUpForwardTicks.add(new Tick(0.04F,  -0.8F,-0.6F, -2F));
        swingUpForwardTicks.add(new Tick(0.08F,  -0.2F,-0.85F, -1.5F));
        swingUpForwardTicks.add(new Tick(0.12F,  0.4F,-0.85F, -1F));
        swingUpForwardTicks.add(new Tick(0.16F,  1.05F,-0.7F, -0.5F));

        SWING_UP_FORWARD = new Melee(0.6F, 0.3F, slowRunCycle, swingUpForwardTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_UP_BACKWARD                    ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingUpBackwardTicks = new ArrayList<>();
        swingUpBackwardTicks.add(new Tick(0.04F,  1.05F,-0.7F, -0.5F));
        swingUpBackwardTicks.add(new Tick(0.08F,  0.4F,-0.85F, -1F));
        swingUpBackwardTicks.add(new Tick(0.12F,  -0.2F,-0.85F, -1.5F));
        swingUpBackwardTicks.add(new Tick(0.16F,  -0.8F,-0.6F, -2F));

        SWING_UP_BACKWARD = new Melee(0.6F, 0.3F, slowRunCycle, swingUpBackwardTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_DOWN_FORWARD                   ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingDownForward = new ArrayList<>();
        for (Tick tick : swingUpForwardTicks)
        {
            swingDownForward.add(tick.getMirrorCopy(false, true));
        }

        SWING_DOWN_FORWARD = new Melee(0.6F, 0.3F, slowRunCycle, swingDownForward);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_DOWN_BACKWARD                  ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingDownBackward = new ArrayList<>();
        for (Tick tick : swingUpBackwardTicks)
        {
            swingDownBackward.add(tick.getMirrorCopy(false, true));
        }

        SWING_DOWN_BACKWARD = new Melee(0.6F, 0.3F, slowRunCycle, swingDownBackward);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_LUNGE                          ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingLungeTicks = new ArrayList<>();

        swingLungeTicks.add(new Tick(0.03F,  -0.8F,-0.6F, -2F));
        swingLungeTicks.add(new Tick(0.06F,  -0.2F,-0.85F, -1.5F));
        swingLungeTicks.add(new Tick(0.09F,  0.4F,-0.85F, -1F));
        swingLungeTicks.add(new Tick(0.12F,  1.05F,-0.7F, -0.5F));

        swingLungeTicks.add(new Tick(0.15F, 1.05F, -0.7F, -0.8F));
        swingLungeTicks.add(new Tick(0.18F, 1.4F, -0.4F, -0.4F));
        swingLungeTicks.add(new Tick(0.21F, 1.5F, -0.1F, -0.1F));
        swingLungeTicks.add(new Tick(0.24F, 1.4F, 0.2F, 0.2F));

        SWING_LUNGE = new Melee(0.6F, 0.3F, lungeCycle, swingLungeTicks);

        ///////////////////////////////////////////////////////////////////////
        ///                            SWING_LUNGE_UNTERHAU                 ///
        ///////////////////////////////////////////////////////////////////////

        ArrayList<Tick> swingLungeUnterhauTicks = new ArrayList<>();

        swingLungeUnterhauTicks.add(new Tick(0.03F,  -0.8F,0.6F, -2F));
        swingLungeUnterhauTicks.add(new Tick(0.06F,  -0.2F,0.85F, -1.5F));
        swingLungeUnterhauTicks.add(new Tick(0.09F,  0.4F,0.85F, -1F));
        swingLungeUnterhauTicks.add(new Tick(0.12F,  1.05F,0.7F, -0.5F));

        swingLungeUnterhauTicks.add(new Tick(0.15F, 1.4F, 0.2F, 0.2F));
        swingLungeUnterhauTicks.add(new Tick(0.18F, 1.5F, -0.1F, -0.1F));
        swingLungeUnterhauTicks.add(new Tick(0.21F, 1.4F, -0.4F, -0.4F));
        swingLungeUnterhauTicks.add(new Tick(0.24F, 1.05F, -0.7F, -0.8F));

        SWING_LUNGE_UNTERHAU = new Melee(0.6F, 0.3F, lungeCycle, swingLungeUnterhauTicks);

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

    }
}
