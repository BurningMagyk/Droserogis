package Gameplay.Weapons.Inflictions;

import Gameplay.Actor;
import Util.GradeEnum;
import Util.Vec2;

public interface Infliction
{
    public enum InflictionType
    {
        PIERCE, SLASH, BLUNT, SCRAPE,
        AIR, WATER, EARTH, FIRE, COLD, LIGHTNING, METAL
    }

    InflictionType[] getTypes();

    GradeEnum getDamage();

    ConditionApp getConditionApp();

    Vec2 getMomentum();
}