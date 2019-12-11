package Gameplay.Weapons.Inflictions;

import Util.GradeEnum;
import Util.Vec2;

public class DamageInfliction implements Infliction
{
    final GradeEnum grade;
    final InflictionType type;

    public DamageInfliction(GradeEnum grade, InflictionType type)
    {
        this.grade = grade;
        this.type = type;
    }

    @Override
    public InflictionType getType() { return type; }

    @Override
    public GradeEnum getDamage() { return grade; }

    @Override
    public ConditionApp getConditionApp() { return null; }

    @Override
    public Vec2 getMomentum() { return null; }
}
