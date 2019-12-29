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
    public Orient getOrient() { return orient.copy(); }
    @Override
    public float interrupt(Command command)
    {
        float boost = 0;

        if (state == State.WARMUP || (state.ordinal() >= State.COOLDOWN.ordinal()
                && proceedsTo(command))) boost = totalSec;

        totalSec = 0;
        state = State.VOID;
        attackKey = -1;

        return boost;
    }
    @Override
    public MeleeEnum getNext(MeleeEnum meleeEnum)
    {
        for (int i = 0; i < next[0].length; i++)
        {
            if (next[0][i] == meleeEnum) return next[1][i];
        }
        return null;
    }

    @Override
    public void start(Orient startOrient, float warmBoost, Command command)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        face = command.FACE;
        attackKey = command.ATTACK_KEY;

        warmJourney = new Journey(startOrient, execJourney[0].getOrient(), waits.x);

        Print.blue("Operating \"" + getName() + "\"");
    }

    private boolean warmup()
    {
        selfInfliction = new ConditionInfliction(
                cycle, State.WARMUP.ordinal(), Infliction.InflictionType.METAL);

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
                cycle,  State.EXECUTION.ordinal(), Infliction.InflictionType.METAL);

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
                cycle, State.COOLDOWN.ordinal(), Infliction.InflictionType.METAL);

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
        if (proceeds == null) return false;
        for (MeleeEnum proceed : proceeds)
        {
            if (command.ENUM == proceed) return true;
        }
        return false;
    }

    Orient[] getTickOrients()
    {
        Orient[] tickOrients = new Orient[execJourney.length];
        for (int i = 0; i < execJourney.length; i++)
        {
            tickOrients[i] = execJourney[i].getOrient();
        }
        return tickOrients;
    }

    public Weapon.Operation copy()
    {
        return new MeleeOperation(
                name, next, proceeds, cycle,
                waits, funcDir, damage, execJourney);
    }

    enum MeleeEnum
    {
        THRUST, THRUST_UNTERHAU, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN,
        LUNGE,
        STAB, STAB_UNTERHAU,
        SWING, SWING_UNTERHAU, SWING_UNTERHAU_C, SWING_AERIAL, SWING_PRONE,
        SWING_UP_FORWARD, SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD,
        SHOVE,
        GRAB, GRAB_UP, GRAB_DIAG_UP, GRAB_ALT, POUNCE, TACKLE,
        TOSS, THROW, THROW_UP, THROW_UP_DIAG, THROW_DOWN, THROW_DOWN_DIAG, DROP,
        INTERACT,
        DRAW, LOAD, SHOOT;
    }

    private String name;
    private MeleeEnum[][] next;
    private MeleeEnum[] proceeds;
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
            MeleeEnum[][] next,
            MeleeEnum[] proceeds,
            ConditionAppCycle cycle,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            Tick[] execJourney
    )
    {
        this.name = name;
        this.next = next;
        this.proceeds = proceeds;
        this.cycle = cycle;
        this.waits = waits.copy();
        this.funcDir = funcDir;
        this.damage = damage;
        this.execJourney = execJourney;
    }

    MeleeOperation(String name, MeleeOperation op)
    {
        this(name, op.next, op.proceeds, op.cycle, op.waits.copy(),
                op.funcDir, op.damage, op.execJourney);
    }

    MeleeOperation(
            String name,
            MeleeOperation op,
            MeleeEnum[][] next
    )
    {
        this(name, next, op.proceeds, op.cycle, op.waits.copy(),
                op.funcDir, op.damage, op.execJourney);
    }

    MeleeOperation(
            String name,
            MeleeOperation op,
            ConditionAppCycle cycle
    )
    {
        this(name, op.next, op.proceeds, cycle, op.waits.copy(),
                op.funcDir, op.damage, op.execJourney);
    }
}
