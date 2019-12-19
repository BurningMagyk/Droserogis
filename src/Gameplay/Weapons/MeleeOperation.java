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
    public DirEnum getDir() { return face; }

    private Infliction infliction, selfInfliction;
    @Override
    public Infliction getInfliction() { return infliction; }
    @Override
    public Infliction getSelfInfliction() { return selfInfliction; }
    @Override
    public State getState() { return state; }
    @Override
    public Orient getOrient() { return orient; }
    @Override
    public float interrupt(Command command)
    {
        float boost;

        if (state == State.WARMUP || (state == State.COOLDOWN && proceedsTo(command))) boost = totalSec;
        else if (state == State.EXECUTION) boost = 0; // don't expect to interrupt during exec
        else boost = coolJourney.getTotalTime(); // during or after cooldown

        totalSec = 0;
        state = State.VOID;
        attackKey = -1;

        return boost;
    }
    @Override
    public MeleeEnum getNext(Command command) { return null; }

    @Override
    public void start(Orient startOrient, float warmBoost, Command command)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        this.face = command.FACE;
        this.attackKey = command.ATTACK_KEY;

        warmJourney = new Journey(startOrient, execJourney[0].getOrient(), waits.x);

        Print.blue("Operating \"" + getName() + "\"");
    }

    private boolean warmup()
    {
        selfInfliction = new ConditionInfliction(
                cycle, Infliction.InflictionType.METAL, State.WARMUP.ordinal());

        if (warmJourney.check(totalSec, face) && attackKey == -1)
        {
            totalSec = 0;
            state = State.EXECUTION;
            return execute();
        }
        orient = warmJourney.getOrient();
        return false;
    }

    private boolean execute()
    {
        selfInfliction = new ConditionInfliction(
                cycle, Infliction.InflictionType.METAL, State.EXECUTION.ordinal());

        for (Tick tick : execJourney)
        {
            if (tick.check(totalSec, face))
            {
                orient = tick.getOrient();
                return false;
            }
        }
        totalSec = 0;
        state = State.COOLDOWN;
        coolJourney = warmJourney.makeCoolJourney(
                execJourney[execJourney.length - 1].getOrient(), waits.y);
        return cooldown();
    }

    private boolean cooldown()
    {
        selfInfliction = new ConditionInfliction(
                cycle, Infliction.InflictionType.METAL, State.COOLDOWN.ordinal());

        if (!coolJourney.check(totalSec, face))
        {
            orient = coolJourney.getOrient();
            return false;
        }
        orient = coolJourney.getOrient();
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
        if (attackKey == this.attackKey) this.attackKey = -1;
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
    private Tick[] execJourney;
    private Journey warmJourney, coolJourney;

    private DirEnum face;
    private int attackKey = -1;

    private State state = State.VOID;
    private Orient orient;
    private float totalSec = 0;

    // TODO: easyToBlock set in WeaponTypeEnum (sword thrusts harder to block than hammer thrusts)
    // TODO: disruptive set universally in WeaponTypeEnum (swings true, others false)

    MeleeOperation(
            String name,
            ConditionAppCycle cycle,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            Tick[] execJourney
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
