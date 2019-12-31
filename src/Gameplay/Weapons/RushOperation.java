package Gameplay.Weapons;

import Gameplay.DirEnum;
import Gameplay.Item;
import Util.GradeEnum;
import Util.Vec2;

public class RushOperation implements Weapon.Operation
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
    public Orient getOrient() { return null; }

    @Override
    public float interrupt(Command command)
    {
        float boost;

        if (state == State.WARMUP || (state == State.COOLDOWN && proceedsTo(command))) boost = totalSec;
        else if (state == State.EXECUTION) boost = 0; // don't expect to interrupt during exec
        else boost = totalSec; // during or after cooldown

        totalSec = 0;
        state = State.VOID;
        attackKey = -1;

        return boost;
    }
    public void interrupt(RushFinish rushFinish)
    {
        if (state.ordinal() >= State.COOLDOWN.ordinal()) return;

        for (RushFinish finish : finishes)
        {
            if (finish == rushFinish)
            {
                state = State.COOLDOWN;
                break;
            }
        }
    }

    @Override
    public MeleeOperation.MeleeEnum getNext(MeleeOperation.MeleeEnum meleeEnum)
    {
        for (int i = 0; i < next[0].length; i++)
        {
            if (next[0][i] == meleeEnum) return next[1][i];
        }
        return null;
    }

    @Override
    public void start(Orient orient, float warmBoost, Command command)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        face = command.FACE;
        attackKey = command.ATTACK_KEY;
    }

    @Override
    public boolean run(float deltaSec)
    {
        totalSec += deltaSec;

        if (state == State.WARMUP)
        {
            if (totalSec >= waits.x)
            {
                state = State.EXECUTION;
            }
        }
        if (state == State.EXECUTION)
        {
            totalSec = 0;
            if (attackKey == -1) state = State.COOLDOWN;
        }

        selfInfliction = new Infliction(
                cycle, state.ordinal(), Infliction.InflictionType.METAL);

        if (state == State.COOLDOWN)
        {
            if (totalSec >= waits.y)
            {
                state = State.VOID;
                totalSec = 0;
                attackKey = -1;
                return true;
            }
            return false;
        }
        return false;


    }

    @Override
    public void release(int attackKey) { if (this.attackKey == attackKey) this.attackKey = -1; }

    @Override
    public void apply(Item other)
    {

    }

    @Override
    public boolean isEasyToBlock() { return false; }

    @Override
    public boolean isDisruptive() { return false; }

    private boolean proceedsTo(Command command)
    {
        return true;
    }

    @Override
    public Weapon.Operation copy()
    {
        return new RushOperation(name, next, cycle,
                waits, funcDir, damage, finishes);
    }

    public enum RushFinish
    {
        HIT_FLOOR, HIT_WALL, HIT_WATER, HIT_TARGET,
        MAKE_LOW, LOSE_SPRINT, STAGGER
    }

    private String name;
    private MeleeOperation.MeleeEnum[][] next;
    private ConditionAppCycle cycle;
    private Vec2 waits;
    private DirEnum funcDir;
    private GradeEnum damage;
    private RushFinish[] finishes;

    private DirEnum face;
    private int attackKey = -1;

    private State state = State.VOID;
    private float totalSec = 0;

    RushOperation(
            String name,
            MeleeOperation.MeleeEnum[][] next,
            ConditionAppCycle cycle,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            RushFinish ...finishes
    )
    {
        this.name = name;
        this.next = next;
        this.cycle = cycle;
        this.waits = waits.copy();
        this.funcDir = funcDir;
        this.damage = damage;
        this.finishes = finishes;
    }
}
