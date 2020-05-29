/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.Entities.Actor;
import Gameplay.Entities.Characters.CharacterStat;
import Gameplay.DirEnum;
import Gameplay.Entities.Item;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

public class RushOperation implements Weapon.Operation
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
        return new Infliction(damage, null, null, conditionApps,
                actor.getTravelDir(),
                actor.getMass(),
                infDir, actor.getRushInfTypes());
    }
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

    private float waitSpeed;
    @Override
    public void start(Orient orient, float warmBoost, CharacterStat characterStat,
                      WeaponStat weaponStat, Command command, boolean extraMomentum)
    {
        state = State.WARMUP;
        totalSec = warmBoost;
        face = command.FACE;
        attackKey = command.ATTACK_KEY;

        GradeEnum strGrade = characterStat.getGrade(CharacterStat.Ability.STRENGTH);
        GradeEnum agiGrade = characterStat.getGrade(CharacterStat.Ability.AGILITY);
        GradeEnum dexGrade = characterStat.getGrade(CharacterStat.Ability.DEXTERITY);

        waitSpeed = weaponStat.waitSpeed(strGrade, agiGrade);

        ConditionApp[] conditionAppsExtra = weaponStat.inflictionApp();
        ConditionApp conditionApp = conditionApps[0];
        conditionApps = new ConditionApp[conditionAppsExtra.length + 1];
        System.arraycopy(conditionAppsExtra, 0,
                conditionApps, 1, conditionAppsExtra.length);
        conditionApps[0] = conditionApp;
        selfApps = weaponStat.selfInflictionApp();

        damage = weaponStat.damage(strGrade, damageMod);
    }

    @Override
    public boolean run(float deltaSec)
    {
        if (state == State.WARMUP)
        {
            totalSec += deltaSec * waitSpeed;
            if (totalSec >= waits.x)
            {
                state = State.EXECUTION;
                totalSec = 0;
            }
        }
        if (state == State.EXECUTION)
        {
            if (attackKey == -1) state = State.COOLDOWN;
        }

        selfInfliction = new Infliction(
                cycle, selfApps, state.ordinal(), Infliction.InflictionType.METAL);

        if (state == State.COOLDOWN)
        {
            totalSec += deltaSec * waitSpeed;
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
    public boolean isParrying() { return true; }
    @Override
    public boolean isPermeating() { return false; }

    private boolean proceedsTo(Command command)
    {
        return false;
    }

    /** knockback and precision parameters should be null */
    @Override
    public void setStats(GradeEnum damage, GradeEnum knockback, GradeEnum precision)
    {
        damageMod = damage;
    }

    @Override
    public Weapon.Operation copy()
    {
        RushOperation op = new RushOperation(name, next, cycle,
                waits, funcDir, conditionApps[0], finishes);
        op.setStats(damageMod, null, null);
        return op;

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
    private GradeEnum damageMod;
    private ConditionApp[] conditionApps, selfApps;
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
            ConditionApp conditionApp,
            RushFinish ...finishes
    )
    {
        this.name = name;
        this.next = next;
        this.cycle = cycle;
        this.waits = waits.copy();
        this.funcDir = funcDir;
        this.conditionApps = new ConditionApp[] {conditionApp};
        this.finishes = finishes;
    }
}
