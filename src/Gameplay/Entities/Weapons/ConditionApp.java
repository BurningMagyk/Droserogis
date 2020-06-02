package Gameplay.Entities.Weapons;

import Gameplay.Entities.Actor;

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

    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < conds.length; i++)
        {
            out.append(conds[i]);
            if (i < conds.length - 1) out.append(", ");
            else out.append(".");
        }
        out.append(" Duration: ");
        if (isSinglet()) out.append("singlet");
        else out.append(time);
        return out.toString();
    }
}
