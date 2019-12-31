package Gameplay.Weapons;

import Util.GradeEnum;
import Util.Vec2;

public class Infliction
{
    public enum InflictionType
    {
        PIERCE, SLASH, BLUNT, SCRAPE,
        AIR, WATER, EARTH, FIRE, COLD, LIGHTNING, METAL
    }

    private final GradeEnum damage;
    private final ConditionApp conditionApp;
    private final Vec2 momentum;
    private final InflictionType[] types;

    public Infliction(GradeEnum damage, InflictionType...types)
    {
        this.damage = damage;

        conditionApp = null;
        momentum = null;

        this.types = types;
    }

    public Infliction(ConditionAppCycle cycle, int operationState, InflictionType...types)
    {
        if (operationState == 0) conditionApp = cycle.getWarm();
        else if (operationState == 1) conditionApp = cycle.getExec();
        else conditionApp = cycle.getCool();

        damage = null;
        momentum = null;

        this.types = types;
    }

    public Infliction(Vec2 actorSpeed, float actorMass, Vec2 weaponSpeed, float weaponMass, InflictionType...types)
    {
        damage = null;
        conditionApp = null;
        momentum = null; // temporary

        this.types = types;
    }

    public InflictionType[] getTypes() { return types; }

    public GradeEnum getDamage() { return damage; }

    public ConditionApp getConditionApp() { return conditionApp; }

    public Vec2 getMomentum() { return null; }
}
