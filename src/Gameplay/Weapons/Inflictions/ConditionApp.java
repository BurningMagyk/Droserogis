package Gameplay.Weapons.Inflictions;

import Gameplay.Actor;

public class ConditionApp
{
    final Actor.Condition[] conds;
    final float time;

    ConditionApp(float time, Actor.Condition... conds)
    {
        this.conds = conds;
        this.time = time;
    }
    ConditionApp(Actor.Condition... conds)
    {
        this.conds = conds;
        this.time = -1;
    }

    public Actor.Condition[] getConditions() { return conds; }
    public float getTime() { return time; }
    public boolean isSinglet() { return this.time == -1; }
}
