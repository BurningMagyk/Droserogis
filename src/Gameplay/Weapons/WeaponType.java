package Gameplay.Weapons;

import Gameplay.Weapons.Inflictions.ConditionApp;
import Gameplay.Weapons.Inflictions.ConditionAppCycle;
import Util.GradeEnum;
import Util.Vec2;

public class WeaponType
{
    private final float[] DEF_ORIENT;

    public WeaponType(Vec2 orientPos, float orientTheta)
    {
        DEF_ORIENT = new float[]{ orientPos.x, orientPos.y, orientTheta };
    }

    float[] getDefaultOrient() { return DEF_ORIENT; }

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
}
