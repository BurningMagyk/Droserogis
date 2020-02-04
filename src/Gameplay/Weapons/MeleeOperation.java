package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.Characters.CharacterStat;
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
    public DirEnum getDir() { return face; }

    private Infliction selfInfliction;
    @Override
    public Infliction getInfliction(Actor actor, float mass)
    {
        DirEnum infDir = (face.getHoriz() == DirEnum.LEFT)
                ? DirEnum.get(funcDir.getHoriz().getOpp(), funcDir.getVert()) : funcDir;
        return new Infliction(damage, precision, conditionApps,
                actor.getVelocity(), actor.getMass() - mass, actor.getGrip(),
                infDir, execSpeed, mass, infTypes); }
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
                && command != null && proceedsTo(command))) boost = totalSec;

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

    private float waitSpeed, execSpeed;
    @Override
    public void start(Orient startOrient, float warmBoost, CharacterStat characterStat,
                      WeaponStat weaponStat, Command command)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        face = command.FACE;
        attackKey = command.ATTACK_KEY;

        warmJourney = new Journey(startOrient, execJourney[0].getOrient(), waits.x);

        GradeEnum strGrade = characterStat.getGrade(CharacterStat.Ability.STRENGTH);
        waitSpeed = weaponStat.waitSpeed(strGrade);
        execSpeed = weaponStat.attackSpeed(strGrade);

        ConditionApp[] conditionAppsExtra = weaponStat.inflictionApp();
        ConditionApp conditionApp = conditionApps[0];
        conditionApps = new ConditionApp[conditionAppsExtra.length + 1];
        System.arraycopy(conditionAppsExtra, 0,
                conditionApps, 1, conditionAppsExtra.length);
        conditionApps[0] = conditionApp;
        selfApps = weaponStat.selfInflictionApp();
        damage = GradeEnum.getGrade(damageMod / 2 *
                (weaponStat.damage().ordinal()
                + strGrade.ordinal()));
        precision = GradeEnum.getGrade(precisionMod / 2 *
                (weaponStat.precision().ordinal()
                + characterStat.getGrade(CharacterStat.Ability.DEXTERITY).ordinal()));

        Print.blue("Operating \"" + getName() + "\"");
    }

    private boolean warmup()
    {
        selfInfliction = new Infliction(
                cycle, selfApps, State.WARMUP.ordinal(), Infliction.InflictionType.METAL);

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
        selfInfliction = new Infliction(
                cycle, selfApps, State.EXECUTION.ordinal(), Infliction.InflictionType.METAL);

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
        selfInfliction = new Infliction(
                cycle, selfApps, State.COOLDOWN.ordinal(), Infliction.InflictionType.METAL);

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
        if (state == State.WARMUP)
        {
            totalSec += deltaSec * waitSpeed;
            return warmup();
        }
        else if (state == State.EXECUTION)
        {
            totalSec += deltaSec * execSpeed;
            return execute();
        }
        else if (state == State.COOLDOWN)
        {
            totalSec += deltaSec * waitSpeed;
            return cooldown();
        }

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
    public boolean isParrying() {
        return parrying;
    }
    @Override
    public boolean isPermeating() { return permeating; }

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
                waits, funcDir, damageMod, precisionMod,
                parrying, permeating,
                conditionApps[0], execJourney, infTypes);
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
    private GradeEnum damage, precision;
    private float damageMod, precisionMod;
    private boolean parrying, permeating;
    private ConditionApp[] conditionApps, selfApps;
    private Tick[] execJourney;
    private Journey warmJourney, coolJourney;
    private Infliction.InflictionType[] infTypes;

    private DirEnum face;
    private int attackKey = -1;

    private State state = State.VOID;
    private Orient orient;
    private float totalSec = 0;

    // TODO: easyToBlock set in WeaponTypeEnum (sword thrusts harder to block than hammer thrusts)

    MeleeOperation(
            String name,
            MeleeEnum[][] next,
            MeleeEnum[] proceeds,
            ConditionAppCycle cycle,
            Vec2 waits,
            DirEnum funcDir,
            float damageMod,
            float precisionMod,
            boolean parrying,
            boolean permeating,
            ConditionApp conditionApp,
            Tick[] execJourney,
            Infliction.InflictionType... infTypes
    )
    {
        this.name = name;
        this.next = next;
        this.proceeds = proceeds;
        this.cycle = cycle;
        this.waits = waits.copy();
        this.funcDir = funcDir;
        this.damageMod = damageMod;
        this.precisionMod = precisionMod;
        this.parrying = parrying;
        this.permeating = permeating;
        this.conditionApps = new ConditionApp[] {conditionApp};
        this.execJourney = execJourney;
        this.infTypes = infTypes;
    }

    MeleeOperation(String name, MeleeOperation op)
    {
        this(name, op.next, op.proceeds, op.cycle, op.waits.copy(),
                op.funcDir, op.damageMod, op.precisionMod,
                op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
    }

    MeleeOperation(
            String name,
            MeleeOperation op,
            MeleeEnum[][] next
    )
    {
        this(name, next, op.proceeds, op.cycle, op.waits.copy(),
                op.funcDir, op.damageMod, op.precisionMod,
                op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
    }

    MeleeOperation(
            String name,
            MeleeOperation op,
            ConditionAppCycle cycle
    )
    {
        this(name, op.next, op.proceeds, cycle, op.waits.copy(),
                op.funcDir, op.damageMod, op.precisionMod,
                op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
    }
}
