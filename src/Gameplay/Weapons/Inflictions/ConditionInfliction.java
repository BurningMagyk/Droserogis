package Gameplay.Weapons.Inflictions;

import Util.GradeEnum;
import Util.Vec2;

public class ConditionInfliction implements Infliction
{
    final ConditionApp conditionApp;
    final InflictionType[] types;

    public ConditionInfliction(ConditionAppCycle cycle, int operationState, InflictionType ...types)
    {
        if (operationState == 0) conditionApp = cycle.getWarm();
        else if (operationState == 1) conditionApp = cycle.getExec();
        else conditionApp = cycle.getCool();

        this.types = types;
    }

    @Override
    public InflictionType[] getTypes() { return types; }

    @Override
    public GradeEnum getDamage() { return null; }

    @Override
    public ConditionApp getConditionApp() { return conditionApp; }

    @Override
    public Vec2 getMomentum() { return null; }
}
