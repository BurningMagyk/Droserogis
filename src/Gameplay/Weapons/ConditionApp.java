package Gameplay.Weapons;

import Gameplay.Actor;

public class ConditionApp
{
    final Actor.Condition[] conds;
    final float time;

    public ConditionApp(float time, Actor.Condition... conds)
    {
        this.conds = conds;
        this.time = time;
    }
    public ConditionApp(Actor.Condition... conds)
    {
        this.conds = conds;
        this.time = -1;
    }

    public Actor.Condition[] getConditions() { return conds; }
    public float getTime() { return time; }
    public boolean isSinglet() { return this.time == -1; }
}
