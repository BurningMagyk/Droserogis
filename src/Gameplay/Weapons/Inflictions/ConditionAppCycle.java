package Gameplay.Weapons.Inflictions;

public class ConditionAppCycle
{
    private final ConditionApp warm, exec, cool;

    ConditionAppCycle(ConditionApp warm, ConditionApp exec, ConditionApp cool)
    {
        this.warm = warm;
        this.exec = exec;
        this.cool = cool;
    }

    ConditionApp getWarm() { return warm; }
    ConditionApp getExec() { return exec; }
    ConditionApp getCool() { return cool; }
}