package Gameplay.entity.Weapons;

public class ConditionAppCycle
{
    private final ConditionApp warm, exec, cool;

    public ConditionAppCycle(ConditionApp warm, ConditionApp exec, ConditionApp cool)
    {
        this.warm = warm;
        this.exec = exec;
        this.cool = cool;
    }
    public ConditionAppCycle(ConditionApp app) { this(app, app, app); }

    ConditionApp getWarm() { return warm; }
    ConditionApp getExec() { return exec; }
    ConditionApp getCool() { return cool; }
}
