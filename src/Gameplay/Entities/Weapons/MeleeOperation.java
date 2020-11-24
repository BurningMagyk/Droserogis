/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.Entities.Actor;
import Gameplay.Entities.Characters.Character;
import Gameplay.Entities.Characters.CharacterStat;
import Gameplay.DirEnum;
import Gameplay.Entities.Item;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

class MeleeOperation implements Weapon.Operation
{
    @Override
    public String getName() { return name; }
    @Override
    public DirEnum getDir(boolean face) { return face ? this.face : funcDir; }

    private Infliction selfInfliction;
    @Override
    public Infliction getInfliction(Actor actor, GradeEnum mass)
    {
        DirEnum infDir = (face.getHoriz() == DirEnum.LEFT)
                ? DirEnum.get(funcDir.getHoriz().getOpp(), funcDir.getVert()) : funcDir;
        return new Infliction(damage, precision, knockback, conditionApps,
                actor.getTravelDir(),
                extraMomentum ? actor.getMass() : GradeEnum.F,
                infDir, infTypes); }
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

    private RushOperation.RushFinish[] finishes;
    public void interrupt(RushOperation.RushFinish rushFinish)
    {
        if (state.ordinal() >= State.COOLDOWN.ordinal() || finishes == null) return;

        for (RushOperation.RushFinish finish : finishes)
        {
            if (finish == rushFinish)
            {
                state = State.COOLDOWN;
                coolJourney = warmJourney.makeCoolJourney(
                        execJourney[execJourney.length - 1].getOrient(), waits.y);
                break;
            }
        }
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
    private boolean extraMomentum = false;
    @Override
    public void start(Orient startOrient, float warmBoost, CharacterStat characterStat,
                      WeaponStat weaponStat, Command command, boolean extraMomentum)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        face = command.FACE;
        this.extraMomentum = extraMomentum;

        attackKey = command.ATTACK_KEY;

        warmJourney = new Journey(startOrient, execJourney[0].getOrient(), waits.x);
        for (Tick tick : execJourney) { tick.reset(); }

        GradeEnum strGrade = characterStat.getGrade(CharacterStat.Ability.STRENGTH);
        GradeEnum agiGrade = characterStat.getGrade(CharacterStat.Ability.AGILITY);
        GradeEnum dexGrade = characterStat.getGrade(CharacterStat.Ability.DEXTERITY);

        waitSpeed = weaponStat.waitSpeed(strGrade, agiGrade);
        execSpeed = weaponStat.attackSpeed(strGrade, agiGrade);

        ConditionApp[] conditionAppsExtra = weaponStat.inflictionApp();
        ConditionApp conditionApp = conditionApps[0];
        conditionApps = new ConditionApp[conditionAppsExtra.length + 1];
        System.arraycopy(conditionAppsExtra, 0,
                conditionApps, 1, conditionAppsExtra.length);
        conditionApps[0] = conditionApp;
        selfApps = weaponStat.selfInflictionApp();

        damage = weaponStat.damage(strGrade, damageMod);
        knockback = weaponStat.knockback(strGrade, knockbackMod);
        precision = weaponStat.precision(dexGrade, precisionMod);

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
        int inflictionState = State.EXECUTION.ordinal() * (totalSec == 0 ? -1: 1);
        selfInfliction = new Infliction(
                cycle, selfApps, inflictionState, Infliction.InflictionType.METAL);

        for (Tick tick : execJourney)
        {
            if (tick.check(totalSec))
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

    @Override
    public void setStats(GradeEnum damage, GradeEnum knockback, GradeEnum precision)
    {
        damageMod = damage;
        knockbackMod = knockback;
        precisionMod = precision;
    }

    @Override
    public Character.SpriteType getSpriteType() { return spriteType; }

    @Override
    public Weapon.Operation copy()
    {
        MeleeOperation op = new MeleeOperation(
                name, spriteType, next, proceeds, finishes, cycle,
                waits, funcDir,
                parrying, permeating,
                conditionApps[0], execJourney, infTypes);
        op.setStats(damageMod, knockbackMod, precisionMod);
        return op;
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
    private Character.SpriteType spriteType;
    private MeleeEnum[][] next;
    private MeleeEnum[] proceeds;
    private ConditionAppCycle cycle;
    private Vec2 waits;
    private DirEnum funcDir;
    private GradeEnum damage, knockback, precision,
            knockbackMod, precisionMod;
    public GradeEnum damageMod;
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

    MeleeOperation(
            String name,
            Character.SpriteType spriteType,
            MeleeEnum[][] next,
            MeleeEnum[] proceeds,
            ConditionAppCycle cycle,
            Vec2 waits,
            DirEnum funcDir,
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
        this.parrying = parrying;
        this.permeating = permeating;
        this.conditionApps = new ConditionApp[] {conditionApp};
        this.execJourney = cabooseExecJourney(execJourney);
        this.infTypes = infTypes;
    }

    /**
     * This copies the array of ticks into a new array but with a copy of the
     * last tick appended at the end. This is needed to bandage a glitch with
     * weapon-on-actor collision.
     * @param ej - The original array of ticks
     * @return The copy of the array
     */
    private Tick[] cabooseExecJourney(Tick[] ej)
    {
        Tick[] withCaboose = new Tick[ej.length + 1];
        for (int i = 0; i < ej.length; i++)
        {
            withCaboose[i] = ej[i].getCopy();
        }
        withCaboose[ej.length] = ej[ej.length - 1].getCopy();
        return withCaboose;
    }

    MeleeOperation(String name, MeleeOperation op)
    {
        this(name, op.spriteType, op.next, op.proceeds, op.cycle,
                op.waits.copy(), op.funcDir, op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
        setStats(op.damageMod, op.knockbackMod, op.precisionMod);
    }

    MeleeOperation(String name, MeleeOperation op, MeleeEnum[][] next)
    {
        this(name, op.spriteType, next, op.proceeds, op.cycle,
                op.waits.copy(), op.funcDir, op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
        setStats(op.damageMod, op.knockbackMod, op.precisionMod);
    }

    MeleeOperation(String name, MeleeOperation op, ConditionAppCycle cycle)
    {
        this(name, op.spriteType, op.next, op.proceeds, cycle,
                op.waits.copy(), op.funcDir, op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
        setStats(op.damageMod, op.knockbackMod, op.precisionMod);
    }

    MeleeOperation(String name, Character.SpriteType spriteType,
                   MeleeEnum[][] next, MeleeEnum[] proceeds,
                   RushOperation.RushFinish[] finishes,
                   ConditionAppCycle cycle, Vec2 waits, DirEnum funcDir,
                   boolean parrying, boolean permeating,
                   ConditionApp conditionApp, Tick[] execJourney,
                   Infliction.InflictionType... infTypes
    )
    {
        this(name, spriteType, next, proceeds, cycle, waits, funcDir,
                parrying, permeating, conditionApp, execJourney, infTypes);
        if (finishes != null)
        {
            this.finishes = new RushOperation.RushFinish[finishes.length];
            System.arraycopy(finishes, 0, this.finishes, 0, finishes.length);
        }
    }

    MeleeOperation(String name, Character.SpriteType spriteType,
                   MeleeOperation op, Vec2 waits)
    {
        this(name, spriteType, op.next, op.proceeds, op.cycle, waits,
                op.funcDir, op.parrying, op.permeating,
                op.conditionApps[0], op.execJourney, op.infTypes);
        setStats(op.damageMod, op.knockbackMod, op.precisionMod);
    }
}
