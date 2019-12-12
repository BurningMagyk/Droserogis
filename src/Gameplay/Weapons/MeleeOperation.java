package Gameplay.Weapons;

import Gameplay.DirEnum;
import Gameplay.Item;
import Gameplay.Weapons.Inflictions.ConditionAppCycle;
import Gameplay.Weapons.Inflictions.ConditionInfliction;
import Gameplay.Weapons.Inflictions.Infliction;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

class MeleeOperation implements Weapon.Operation
{
    @Override
    public String getName() { return name; }
    @Override
    public DirEnum getDir() { return command.FACE; }

    private Infliction infliction, selfInfliction;
    @Override
    public Infliction getInfliction() { return infliction; }
    @Override
    public Infliction getSelfInfliction() { return selfInfliction; }
    @Override
    public State getState() { return this.state; }
    @Override
    public float interrupt(Command command)
    {
        // TODO: also cancel the operation
        if (state == State.WARMUP || (state == State.COOLDOWN && proceedsTo(command))) return totalSec;
        else if (state == State.EXECUTION) return 0; // don't expect to interrupt during exec
        return coolJourney.getTotalTime(); // after cooldown
    }

    @Override
    public void start(Weapon.Orient orient, float warmBoost)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        warmJourney.setStart(orient);
        Print.blue("Operating " + getName());
    }

    private boolean warmup()
    {
        selfInfliction = new ConditionInfliction(
                cycle, Infliction.InflictionType.METAL, State.WARMUP.ordinal());

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
        selfInfliction = new ConditionInfliction(
                cycle, Infliction.InflictionType.METAL, State.EXECUTION.ordinal());

        for (Weapon.Tick tick : execJourney)
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
        selfInfliction = new ConditionInfliction(
                cycle, Infliction.InflictionType.METAL, State.COOLDOWN.ordinal());

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

        if (state == State.WARMUP) return warmup();
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

    private boolean proceedsTo(Command command)
    {
        return true;
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
    private ConditionAppCycle cycle;
    private Vec2 waits;
    private DirEnum funcDir;
    private GradeEnum damage;
    private Weapon.Tick[] execJourney;
    private Weapon.Journey warmJourney, coolJourney;

    private State state = State.VOID;
    private Command command;
    private float totalSec = 0;

    // TODO: easyToBlock set in WeaponTypeEnum (sword thrusts harder to block than hammer thrusts)
    // TODO: disruptive set universally in WeaponTypeEnum (swings true, others false)

    MeleeOperation(
            String name,
            ConditionAppCycle cycle,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            Weapon.Tick[] execJourney
    )
    {
        this.name = name;
        this.cycle = cycle;
        this.waits = waits.clone();
        this.funcDir = funcDir;
        this.damage = damage;
        this.execJourney = execJourney;
    }
}