package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Gameplay.Weapons.Inflictions.ConditionApp;
import Gameplay.Weapons.Inflictions.ConditionAppCycle;
import Util.GradeEnum;
import Util.Vec2;

import static Gameplay.Weapons.MeleeOperation.MeleeEnum.*;
import static Gameplay.Actor.Condition.*;

public class WeaponType
{
    private final Orient DEF_ORIENT;
    private final MeleeOperation[] MELEE_OPS;

    WeaponType(Orient orient, MeleeOperation ...ops)
    {
        DEF_ORIENT = orient.copy();
        MELEE_OPS = ops;
    }

    Orient getDefaultOrient() { return DEF_ORIENT; }
    MeleeOperation[] getMeleeOps() { return MELEE_OPS; }

    private final static float PI2 = (float) Math.PI / 2;
    private final static float PI4 = (float) Math.PI / 4;

    private final static MeleeOperation.MeleeEnum[][] EMPTY__NEXT = {{}};
    private final static MeleeOperation.MeleeEnum[][] UNTERHAU_SWING__NEXT = {
            {SWING}, {SWING_UNTERHAU}};

    private final static ConditionApp FORCE_WALK__FORCE_STAND = new ConditionApp(
            NEGATE_RUN_LEFT, NEGATE_RUN_RIGHT, FORCE_STAND);
    private final static ConditionApp LUNGE_END_CONDITION = new ConditionApp(
            NEGATE_WALK_LEFT, NEGATE_WALK_RIGHT, NEGATE_ATTACK, NEGATE_BLOCK);

    private final static ConditionAppCycle STANDARD_CYCLE = new ConditionAppCycle(FORCE_WALK__FORCE_STAND);
    private final static ConditionAppCycle LUNGE_CYCLE = new ConditionAppCycle(
            new ConditionApp(FORCE_STAND), new ConditionApp(DASH), LUNGE_END_CONDITION);

    private final static Vec2 SWORD__THRUST_WIATS = new Vec2(2, 2);

    private final static Tick[] SWORD__THRUST__EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, -0.1F, 0),
            new Tick(0.5F, 1, -0.1F, 0),
            new Tick(0.75F, 1.25F, -0.1F, 0),
            new Tick(1, 1.5F, -0.1F, 0) };
    private final static MeleeOperation SWORD__THRUST = new MeleeOperation(
            "Thrust", EMPTY__NEXT, STANDARD_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.RIGHT, GradeEnum.F, SWORD__THRUST__EXEC);

    private final static Tick[] SWORD__THRUST_UNTERHAU__EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, 0.1F, 0),
            new Tick(0.5F, 1, 0.1F, 0),
            new Tick(0.75F, 1.25F, 0.1F, 0),
            new Tick(1, 1.5F, 0.1F, 0) };
    private final static MeleeOperation SWORD__THRUST_UNTERHAU = new MeleeOperation(
            "Thrust", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.RIGHT, GradeEnum.F, SWORD__THRUST_UNTERHAU__EXEC);

    private final static Tick[] SWORD__THRUST_UP__EXEC = new Tick[] {
            new Tick(0.25F, 0, -0.25F, PI2),
            new Tick(0.5F, 0, -0.5F, PI2),
            new Tick(0.75F, 0, -0.75F, PI2),
            new Tick(1, 0, -1F, PI2) };
    private final static MeleeOperation SWORD__THRUST_UP = new MeleeOperation(
            "Thrust up", EMPTY__NEXT, STANDARD_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.UP, GradeEnum.F, SWORD__THRUST_UP__EXEC);

    private final static Tick[] SWORD__THRUST_DOWN__EXEC = new Tick[] {
            new Tick(0.25F, 0, 0.25F, PI2),
            new Tick(0.5F, 0, 0.5F, PI2),
            new Tick(0.75F, 0, 0.75F, PI2),
            new Tick(1, 0, 1F, PI2) };
    private final static MeleeOperation SWORD__THRUST_DOWN = new MeleeOperation(
            "Thrust down", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.DOWN, GradeEnum.F, SWORD__THRUST_DOWN__EXEC);

    private final static Tick[] SWORD__THRUST_DIAG_UP__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, -0.25F, -PI4),
            new Tick(0.5F, 0.5F, -0.5F, -PI4),
            new Tick(0.75F, 0.75F, -0.75F, -PI4),
            new Tick(1, 1F, -1F, -PI4) };
    private final static MeleeOperation SWORD__THRUST_DIAG_UP = new MeleeOperation(
            "Thrust diag down", EMPTY__NEXT, STANDARD_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.DOWNRIGHT, GradeEnum.F, SWORD__THRUST_DIAG_UP__EXEC);

    private final static Tick[] SWORD__THRUST_DIAG_DOWN__EXEC = new Tick[] {
            new Tick(0.25F, 0.25F, 0.25F, PI4),
            new Tick(0.5F, 0.5F, 0.5F, PI4),
            new Tick(0.75F, 0.75F, 0.75F, PI4),
            new Tick(1, 1F, 1F, PI4) };
    private final static MeleeOperation SWORD__THRUST_DIAG_DOWN = new MeleeOperation(
            "Thrust diag down", UNTERHAU_SWING__NEXT, STANDARD_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.DOWNRIGHT, GradeEnum.F, SWORD__THRUST_DIAG_DOWN__EXEC);

    private final static Tick[] SWORD__LUNGE__EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, -0.1F, 0),
            new Tick(0.5F, 1, -0.1F, 0),
            new Tick(0.75F, 1.25F, -0.1F, 0),
            new Tick(1, 1.5F, -0.1F, 0) };
    private final static MeleeOperation SWORD__LUNGE = new MeleeOperation(
            "Lunge", EMPTY__NEXT, LUNGE_CYCLE, SWORD__THRUST_WIATS,
            DirEnum.RIGHT, GradeEnum.F, SWORD__LUNGE__EXEC);

    public final static WeaponType SWORD = new WeaponType(
            new Orient(new Vec2(0.8F, 0.1F), -PI4),
            SWORD__THRUST, SWORD__THRUST_UNTERHAU,
            SWORD__THRUST_UP, SWORD__THRUST_DOWN,
            SWORD__THRUST_DIAG_UP, SWORD__THRUST_DIAG_DOWN,
            SWORD__LUNGE);
}
