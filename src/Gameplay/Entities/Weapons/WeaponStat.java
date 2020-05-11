/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Weapons;

import Util.GradeEnum;

import static Util.GradeEnum.parseGrade;

public class WeaponStat
{
    public enum Ability {
        DURABILITY,

        WAIT_SPEED,
        ATTACK_SPEED,
        SPEED_DEP,

        DAMAGE,
        PRECISION
    }

    private GradeEnum[] grades;
    private int blockRating;
    private ConditionApp[] inflictApps, selfInflictApps;
    private Infliction.InflictionType[] inflictTypes;

    public WeaponStat(
            String dur, String wait_spe,
            String atk_spe, String spe_dep,
            int blo_rat,
            ConditionApp[] infApps, ConditionApp[] selfApps,
            String dam, String pre,
            Infliction.InflictionType... infTypes)
    {
        // Durability
        // Warmup/cooldown speed
        // Attack speed
        // Speed's dependence on strength
        // Block rating
        // ConditionApp inflictions
        // ConditionApp self-inflictions
        // Damage
        // Precision
        // Infliction types

        grades = new GradeEnum[Ability.values().length];
        grades[0] = parseGrade(dur);
        grades[1] = parseGrade(wait_spe);
        grades[2] = parseGrade(atk_spe);
        grades[3] = parseGrade(spe_dep);
        grades[4] = parseGrade(dam);
        grades[5] = parseGrade(pre);

        blockRating = blo_rat;
        inflictApps = infApps == null ? new ConditionApp[]{} : infApps;
        selfInflictApps = selfApps == null ? new ConditionApp[]{} : selfApps;

        inflictTypes = infTypes;
    }


    /*****************************************************************************/
    /************************ Ability Score Charts *******************************/
    /*****************************************************************************/

    private final static int[] DURABILITY = new int[] {
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,

            1, 1, 1,
            1, 1, 1,
            1, 1, 1,

            1, 1, 1,
            1, 1, 1,
            1, 1, 1 };

    private final static float[] WAIT_SPEED = new float[] {
            1.0F, 1.25F, 1.5F,
            1.75F, 2.0F, 2.25F,
            2.5F, 2.75F, 3.0F,

            3.5F, 4.0F, 4.5F,
            5.0F, 5.5F, 6.0F,
            6.5F, 7.0F, 7.5F,

            8.0F, 8.5F, 9.0F,
            9.5F, 10F, 10F,
            10F, 10F, 10F };
    private final static float[] ATTACK_SPEED = new float[] {
            1.7F, 2.1F, 2.5F,
            3.9F, 4.3F, 4.7F,
            5.1F, 5.5F, 5.9F,

            6.3F, 6.7F, 7.1F,
            7.5F, 7.9F, 8.0F,
            8.0F, 8.0F, 8.0F,

            8.0F, 8.0F, 8.0F,
            8.0F, 8.0F, 8.0F,
            8.0F, 8.0F, 8.0F };

    /***************************** Ability Score Chart Access *****************************/

    int durability() { return DURABILITY[grades[Ability.DURABILITY.ordinal()].ordinal()]; }
    float waitSpeed(GradeEnum strGrade, GradeEnum agiGrade)
    {
        int avg = (grades[Ability.WAIT_SPEED.ordinal()].ordinal() + agiGrade.ordinal()) / 2;
        return speedDep(strGrade) ? WAIT_SPEED[avg] : WAIT_SPEED[avg] * 2;
    }
    GradeEnum attackSpeed(GradeEnum strGrade, GradeEnum agiGrade)
    {
        return GradeEnum.getGrade(strGrade.ordinal() + agiGrade.ordinal() / (speedDep(strGrade) ? 2 : 4));
    }
    ConditionApp[] inflictionApp() { return inflictApps; }
    ConditionApp[] selfInflictionApp() { return selfInflictApps; }
    GradeEnum damage(GradeEnum strGrade)
    {
        int damageVal = grades[Ability.DAMAGE.ordinal()].ordinal();
        return GradeEnum.getGrade((damageVal + strGrade.ordinal()) / 2);
    }
    GradeEnum precision(GradeEnum dexGrade)
    {
        int precisionVal = grades[Ability.PRECISION.ordinal()].ordinal();
        return GradeEnum.getGrade((precisionVal + dexGrade.ordinal()) / 2);
    }

    /* 0 - can't block (eg. flail)
     * 1 - can block non-permeating attacks with partial damage soak (eg. natural)
     * 2 - can block non-permeating attacks with complete damage soak (eg. sword)
     * 3 - can block permeating attacks too (eg. kite shield)
     * 4 - can block top and bottom halves at the same time (eg. tower shield)
     */
    int blockRating() { return blockRating; }

    private boolean speedDep(GradeEnum strGrade)
    {
        return strGrade.ordinal() >= grades[Ability.SPEED_DEP.ordinal()].ordinal();
    }

    public String toDataString()
    {
        return "" + grades[0] + "," + grades[1] + "," + grades[2] + "," + grades[3]
                + "," + blockRating + "," + "null" + "," + "null" + "," + grades[4]
                + "," + grades[5] + "," + "null";
    }
}