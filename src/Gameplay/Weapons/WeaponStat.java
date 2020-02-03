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
    private ConditionApp[] inflictApps, selfInflictApps;
    private Infliction.InflictionType[] inflictTypes;

    public WeaponStat(
            String dur, String wait_spe,
            String atk_spe, String spe_dep,
            ConditionApp[] infApps, ConditionApp[] selfApps,
            String dam, String pre, Infliction.InflictionType... infTypes)
    {
        // Durability
        // Warmup/cooldown speed
        // Attack speed
        // Speed's dependence on strength
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
    float waitSpeed(GradeEnum strGrade)
    {
        return speedDep(strGrade) ? WAIT_SPEED[grades[Ability.WAIT_SPEED.ordinal()].ordinal()]
                : WAIT_SPEED[grades[Ability.WAIT_SPEED.ordinal() / 2].ordinal()];
    }
    float attackSpeed(GradeEnum strGrade)
    {
        return speedDep(strGrade) ? ATTACK_SPEED[grades[Ability.ATTACK_SPEED.ordinal()].ordinal()]
                : ATTACK_SPEED[grades[Ability.ATTACK_SPEED.ordinal() / 2].ordinal()];
    }
    ConditionApp[] inflictionApp() { return inflictApps; }
    ConditionApp[] selfInflictionApp() { return selfInflictApps; }
    GradeEnum damage() { return grades[Ability.DAMAGE.ordinal()]; }
    GradeEnum precision() { return grades[Ability.PRECISION.ordinal()]; }


    private boolean speedDep(GradeEnum strGrade)
    {
        return strGrade.ordinal() >= grades[Ability.SPEED_DEP.ordinal()].ordinal();
    }
}