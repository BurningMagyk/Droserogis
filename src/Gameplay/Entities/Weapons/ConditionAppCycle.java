package Gameplay.Entities.Weapons;

public class ConditionAppCycle
{
    private final ConditionApp warm, exec, exec_single, cool;

    public ConditionAppCycle(ConditionApp warm, ConditionApp exec, ConditionApp cool)
    {
        this.warm = warm;
        this.exec = exec;
        this.exec_single = null;
        this.cool = cool;
    }
    public ConditionAppCycle(ConditionApp warm, ConditionApp exec_single,
                             ConditionApp exec, ConditionApp cool)
    {
        this.warm = warm;
        this.exec = exec;
        this.exec_single = exec_single;
        this.cool = cool;
    }
    public ConditionAppCycle(ConditionApp app) { this(app, app, app); }

    ConditionApp getWarm() { return warm; }
    ConditionApp getExec() { return exec; }
    ConditionApp getExecSingle() { return exec_single; }
    ConditionApp getCool() { return cool; }
}
