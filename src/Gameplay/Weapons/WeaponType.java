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

    MeleeOperation[] getMeleeOps()
    {
        /* For testing */
        MeleeOperation.MeleeEnum[][] meleeEnums = {{}};
        ConditionApp testCond = new ConditionApp(null);
        ConditionAppCycle testCycle = new ConditionAppCycle(testCond, testCond, testCond);
        Vec2 testWaits = new Vec2(3, 3);
        Tick[] testExecJourney = new Tick[]{new Tick(1, 1, 0, 0)};
        MeleeOperation testOp = new MeleeOperation(
                "Test Op", meleeEnums, testCycle, testWaits, null, GradeEnum.F, testExecJourney);

        return new MeleeOperation[] { testOp };
    }

    private final static MeleeOperation.MeleeEnum[][] EMPTY__NEXT = {{}};
    private final static ConditionApp FORCE_WALK__FORCE_STAND = new ConditionApp(
            NEGATE_RUN_LEFT, NEGATE_RUN_RIGHT, FORCE_STAND);
    private final static ConditionAppCycle STANDARD_CYCLE = new ConditionAppCycle(FORCE_WALK__FORCE_STAND);
    private final static Vec2 SWORD_THRUST_WIATS = new Vec2(2, 2);
    private final static Tick[] SWORD_THRUST_EXEC = new Tick[] {
            new Tick(0.25F, 0.75F, 0, 0),
            new Tick(0.5F, 1, 0, 0),
            new Tick(0.75F, 1.25F, 0, 0),
            new Tick(1, 1.5F, 0, 0)
    };
    private final static MeleeOperation SWORD_THRUST = new MeleeOperation(
            "Thrust", EMPTY__NEXT, STANDARD_CYCLE, SWORD_THRUST_WIATS,
            DirEnum.RIGHT, GradeEnum.F, SWORD_THRUST_EXEC);

    //final static WeaponType SWORD = new WeaponType()
}
