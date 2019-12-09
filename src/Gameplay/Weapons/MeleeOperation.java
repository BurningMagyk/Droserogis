package Gameplay.Weapons;

import Gameplay.DirEnum;
import Gameplay.Item;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

class MeleeOperation implements Weapon.Operation
{
    @Override
    public String getName() { return name; }
    @Override
    public DirEnum getDir() { return command.FACE; }

    @Override
    public void start(Weapon.Orient orient)
    {
        state = warmSkip ? State.EXECUTION : State.WARMUP;
        totalSec = warmBoost;
        warmBoost = 0;
        warmJourney.setStart(orient);
        Print.blue("Operating " + getName());
    }

    private boolean warmup(float deltaSec)
    {
        cycle.applyWarmup(1); // TODO: how to determine timeMod value
        if (warmBoost > 0) totalSec += deltaSec;
        if (warmJourney.check(totalSec, command.FACE))
        {
            totalSec = 0;
            state = State.EXECUTION;
            return execute();
        }
        return false;
    }

    private boolean execute()
    {
        cycle.applyExecution(1); // TODO: how to determine timeMod value

        for (Weapon_old.Tick tick : execJourney)
        {
            if (tick.check(totalSec, command.FACE))
            {
                return cooldown();
            }
        }
        totalSec = 0;
        state = State.COOLDOWN;
        return false;
    }

    private boolean cooldown()
    {
        cycle.applyCooldown(1); // TODO: how to determine timeMod

        if (!coolJourney.check(totalSec, command.FACE))
        {
            return false;
        }

        totalSec = 0;
        state = State.VOID;

        // TODO: needs access to collidedItems and inflictionsDealt
        //collidedItems.clear();
        //clearInflictionsDealt();
        return true;
    }

    @Override
    public boolean run(float deltaSec)
    {
        totalSec += deltaSec;

        if (state == State.WARMUP) return warmup(deltaSec);
        else if (state == State.EXECUTION) return execute();
        else if (state == State.COOLDOWN) return cooldown();

        return true;
    }

    @Override
    public void release(int attackKey)
    {

    }

    @Override
    public void apply(Item other)
    {

    }

    @Override
    public boolean isEasyToBlock() {
        return false;
    }

    @Override
    public boolean isDisruptive() {
        return false;
    }

    enum MeleeEnum
    {
        THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN,
        THRUST_LUNGE,
        STAB, STAB_UNTERHAU,
        SWING, SWING_UNTERHAU, SWING_UNTERHAU_CROUCH, SWING_UP_FORWARD,
        SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD,
        SWING_LUNGE, SWING_LUNGE_UNTERHAU,
        GRAB,
        DRAW, LOAD, SHOOT;
    }

    private String name;
    private WeaponTypeEnum_old.ConditionAppCycle cycle;
    private Vec2 waits;
    private DirEnum funcDir;
    private GradeEnum damage;
    private Weapon_old.Tick[] execJourney;
    private Weapon.Journey warmJourney, coolJourney;

    private State state = State.VOID;
    private Command command;
    private boolean warmSkip = false;
    private float totalSec = 0, warmBoost = 0; // TODO: warmBoost needs to be set somewhere

    // TODO: conditionAppInfliction should be integrated into damage
    // TODO: easyToBlock set in WeaponTypeEnum (sword thrusts harder to block than hammer thrusts)
    // TODO: disruptive set universally in WeaponTypeEnum (swings true, others false)

    MeleeOperation(
            String name,
            WeaponTypeEnum_old.Stat stat,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            Weapon_old.Tick[] execJourney
    )
    {
        this.name = name;
        this.cycle = stat.cycle;
        this.waits = waits.clone();
        this.funcDir = funcDir;
        this.damage = damage;
        this.execJourney = execJourney;
    }
}
