package Gameplay.Weapons;

import Gameplay.Characters.CharacterStat;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

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
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };
    private final static float[] ATTACK_SPEED = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    /***************************** Ability Score Chart Access *****************************/

    int durability() { return DURABILITY[grades[Ability.DURABILITY.ordinal()].ordinal()]; }
    float waitSpeed(GradeEnum strGrade, GradeEnum agiGrade)
    {
        int avg = (grades[Ability.WAIT_SPEED.ordinal()].ordinal() + agiGrade.ordinal()) / 2;
        return speedDep(strGrade) ? WAIT_SPEED[avg] : WAIT_SPEED[avg / 2];
    }
    float attackSpeed(GradeEnum strGrade, GradeEnum agiGrade)
    {
        int avg = (grades[Ability.ATTACK_SPEED.ordinal()].ordinal() + agiGrade.ordinal()) / 2;
        return speedDep(strGrade) ? ATTACK_SPEED[avg] : ATTACK_SPEED[avg / 2];
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
}