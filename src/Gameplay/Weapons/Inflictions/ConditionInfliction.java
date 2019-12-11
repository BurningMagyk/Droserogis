package Gameplay.Weapons.Inflictions;

import Util.GradeEnum;
import Util.Vec2;

public class ConditionInfliction implements Infliction
{
    final ConditionApp conditionApp;
    final InflictionType type;

    public ConditionInfliction(ConditionAppCycle cycle, InflictionType type, int operationState)
    {
        if (operationState == 0) conditionApp = cycle.getWarm();
        else if (operationState == 1) conditionApp = cycle.getExec();
        else conditionApp = cycle.getCool();

        this.type = type;
    }

    @Override
    public InflictionType getType() { return type; }

    @Override
    public GradeEnum getDamage() { return null; }

    @Override
    public ConditionApp getConditionApp() { return conditionApp; }

    @Override
    public Vec2 getMomentum() { return null; }
}
