/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Gameplay.DirEnum;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

public class Infliction
{
    public enum InflictionType
    {
        PIERCE, SLASH, BLUNT, SCRAPE, SURFACE,
        AIR, WATER, EARTH, FIRE, COLD, LIGHTNING, METAL
    }

    private final GradeEnum damage, precision;
    private final ConditionApp[] conditionApps;
    private final GradeEnum momentumMag;
    private final DirEnum momentumDir;
    private final InflictionType[] types;

    /* For colliding with blocks */
    public Infliction(GradeEnum damage, InflictionType...types)
    {
        this.damage = damage;
        this.precision = null;

        conditionApps = null;
        momentumMag = null;
        momentumDir = DirEnum.NONE;

        this.types = types;
    }

    public boolean isSelfInf = false;
    /* For self inflictions */
    public Infliction(ConditionAppCycle cycle, ConditionApp[] extraApps, int operationState, InflictionType...types)
    {
        isSelfInf = true;

        conditionApps = new ConditionApp[extraApps.length + 1];

        if (operationState == 0) conditionApps[0] = cycle.getWarm();
        else if (operationState == 1) conditionApps[0] = cycle.getExec();
        else conditionApps[0] = cycle.getCool();

        System.arraycopy(extraApps, 0, conditionApps, 1, extraApps.length);

        damage = null;
        precision = null;
        momentumMag = null;
        momentumDir = null;

        this.types = types;
    }

//    public Infliction(Vec2 actorSpeed, float actorMass, float actorGrip,
//                      DirEnum weaponDir, float weaponSpeed, float weaponMass, InflictionType...types)
//    {
//        Vec2 actorMomentum = actorSpeed.mul(actorMass + actorGrip);
//        Vec2 weaponMomentum = weaponDir.unit().mul(weaponSpeed * weaponMass);
//        momentum = actorMomentum.add(weaponMomentum);
//
//        damage = null;
//        conditionApps = null;
//
//        this.types = types;
//    }

    /* For inflictions dealt by attacks */
    public Infliction(GradeEnum damage, GradeEnum precision, ConditionApp[] conditionApps,
                      DirEnum actorDir, GradeEnum actorSpeed, GradeEnum actorMass, GradeEnum actorGrip,
                      DirEnum weaponDir, GradeEnum weaponSpeed, GradeEnum weaponMass, InflictionType...types)
    {
        this.damage = damage;
        this.precision = precision;
        this.conditionApps = conditionApps;
//        Vec2 actorMomentum = actorSpeed.mul(actorMass + actorGrip);

        momentumMag = GradeEnum.getGrade((actorSpeed.ordinal() + actorMass.ordinal() + actorGrip.ordinal()
                + weaponSpeed.ordinal() + weaponMass.ordinal()) / 4);
        Print.green(weaponDir);
        momentumDir = DirEnum.add(actorDir, weaponDir);
        Print.green(momentumDir);

//        Vec2 weaponMomentum = weaponDir.unit().mul(weaponSpeed * (weaponMass + actorGrip));
//        Vec2 ridiculous = actorMomentum.add(weaponMomentum);

//        // Differences in momentum are ridiculous so here I'm curbing them
//        int xSign = (ridiculous.x < 0) ? -1 : 1, ySign = (ridiculous.y < 0) ? -1 : 1;
//        momentum = new Vec2(Math.sqrt(Math.abs(ridiculous.x) * 100) / 50 * xSign,
//                Math.sqrt(Math.abs(ridiculous.y) * 100) / 50 * ySign);

        this.types = types;
    }

    public InflictionType[] getTypes() { return types; }

    public GradeEnum getDamage() { return damage; }

    public ConditionApp[] getConditionApps() { return conditionApps; }

    public GradeEnum getMomentum() { return momentumMag; }

    public DirEnum getDir()
    {
        return momentumDir;
//        if (momentum == null) return DirEnum.NONE;
//        if (momentum.x == 0)
//        {
//            if (momentum.y == 0) return DirEnum.NONE;
//            else if (momentum.y > 0) return DirEnum.DOWNRIGHT;
//            else return DirEnum.UP;
//        }
//        else if (momentum.x > 0)
//        {
//            if (momentum.y == 0) return DirEnum.RIGHT;
//            else if (momentum.y > 0) return DirEnum.DOWNRIGHT;
//            else return DirEnum.UPRIGHT;
//        }
//        else // if (momentum.x < 0)
//        {
//            if (momentum.y == 0) return DirEnum.LEFT;
//            else if (momentum.y > 0) return DirEnum.DOWNLEFT;
//            else return DirEnum.UPLEFT;
//        }
    }

    public boolean isDisruptive()
    {
        for (InflictionType inflictionType : types)
        {
            if (inflictionType == InflictionType.SLASH
                    || inflictionType == InflictionType.BLUNT)
                return true;
        }
        return false;
    }
}
