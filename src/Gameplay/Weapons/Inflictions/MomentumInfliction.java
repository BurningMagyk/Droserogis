package Gameplay.Weapons.Inflictions;

import Util.GradeEnum;
import Util.Vec2;

public class MomentumInfliction implements Infliction
{
    MomentumInfliction(GradeEnum grade,  InflictionType ...types)
    {
    }

    @Override
    public InflictionType[] getTypes() { return null; }

    @Override
    public GradeEnum getDamage() { return null; }

    @Override
    public ConditionApp getConditionApp() { return null; }

    @Override
    public Vec2 getMomentum()
    {
        return null;
    }
}
