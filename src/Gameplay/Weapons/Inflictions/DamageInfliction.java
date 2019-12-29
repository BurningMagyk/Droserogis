package Gameplay.Weapons.Inflictions;

import Util.GradeEnum;
import Util.Vec2;

public class DamageInfliction implements Infliction
{
    final GradeEnum grade;
    final InflictionType[] types;

    public DamageInfliction(GradeEnum grade, InflictionType ...types)
    {
        this.grade = grade;
        this.types = types;
    }

    @Override
    public InflictionType[] getTypes() { return types; }

    @Override
    public GradeEnum getDamage() { return grade; }

    @Override
    public ConditionApp getConditionApp() { return null; }

    @Override
    public Vec2 getMomentum() { return null; }
}
