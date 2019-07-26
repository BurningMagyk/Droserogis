package Gameplay.Characters;

import Util.GradeEnum;
import Util.Print;

import static Util.GradeEnum.parseGrade;

public class CharacterStat
{
    public enum Ability {
        STRENGTH,       /* Physical strength */
        STAMINA,        /* Endurance */

        DEXTERITY,      /* How precise one is */
        AGILITY,        /* Speed */

        CONSTITUTION,   /* Natural ability to shrug off certain affects */
        VITALITY,       /* Physical hardiness */

        WISDOM,         /* Ability to make good judgements */
        WILL,           /* Mental fortitude */

        INTELLIGENCE,   /* Application of knowledge, memory, thinking speed */
        KNOWLEDGE,      /* Measure of how much someone knows */

        PRESENCE        /* Power of Personality */
    }



    private final static String[] abilityStrings = new String[] {
            "STRENGTH", "STAMINA",
            "DEXTERITY", "AGILITY",
            "CONSTITUTION", "VITALITY",
            "WISDOM", "WILL",
            "INTELLIGENCE", "KNOWLEDGE",
            "PRESENCE"};

    // TODO: may be redundant
    public static String getAbilityString(Ability ab)
    {
        String abStr = "";
        Ability abilitiesArr[] = Ability.values();

        for(int i = 0; i < abilityStrings.length; i++)
        {
            if(ab == abilitiesArr[i]) abStr = abilityStrings[i];
        }

        return abStr;
    }
    private Ability parseAbility(String string)
    {
        for (int i = 0; i < Ability.values().length; i++)
        {
            if (string.equalsIgnoreCase(abilityStrings[i]))
                return Ability.values()[i];
        }
        Print.red("Could not parse string \"" + string + "\" as ability name.");
        return null;
    }
    public Ability[] parseAbilities(String string)
    {
        String[] words = string.split(", ");
        Ability[] vals = new Ability[words.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = parseAbility(words[i]); }
        return vals;
    }

    private GradeEnum[] grades;
    public GradeEnum[] getGrades() { return grades; }
    public GradeEnum getGrade(Ability ability) { return grades[ability.ordinal()]; }
    public void setGrades(String[] g)
    {
        for(int i = 0; i < g.length; i++)
        {
            grades[i] = parseGrade(g[i]);
        }
    }
    public int[] increaseGrades(int[] sortedStats, int maxIncrease)
    {
        GradeEnum posGrades[] = GradeEnum.values();
        int increasedStats[] = new int[maxIncrease];
        int increasedSoFar = 0;

        for(int i = 0; i < sortedStats.length; i++)
        {
            int increase = grades[sortedStats[i]].ordinal()-1;
            if(increase <= 0) continue;

            grades[sortedStats[i]] = posGrades[increase];
            increasedStats[increasedSoFar] = sortedStats[i];
            increasedSoFar++;

            if(increasedSoFar == (maxIncrease)) break;
        }
        return increasedStats;
    }

    public CharacterStat(
            String str, String sta,
            String dex, String agi,
            String con, String vit,
            String wil, String wis, String kno, String nte,
            String pre)
    {
        grades = new GradeEnum[Ability.values().length];
        grades[0] = parseGrade(str); grades[1] = parseGrade(sta);
        grades[2] = parseGrade(dex); grades[3] = parseGrade(agi);
        grades[4] = parseGrade(con); grades[5] = parseGrade(vit);
        grades[6] = parseGrade(wil); grades[7] = parseGrade(wis);
        grades[8] = parseGrade(kno); grades[9] = parseGrade(nte);
        grades[10] = parseGrade(pre);
    }
    CharacterStat(String stats[])
    {
        grades = new GradeEnum[Ability.values().length];
        for(int i = 0; i < stats.length; i++)
        {
            grades[i] = parseGrade(stats[i]);
        }
    }

    /*****************************************************************************/
    /************************ Ability Score Charts *******************************/
    /*****************************************************************************/

    private final static float[] STRENGTH = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };
    private final static float[] STAMINA = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] DEXTERITY = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };
    private final static float[] AGILITY = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] CONSTITUTION = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };
    private final static float[] VITALITY = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] WISDOM = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };
    private final static float[] WILL = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] INTELLIGENCE = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };
    private final static float[] KNOWLEDGE = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] PRESENCE = new float[] {
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

    private float strength(float base, float offset)
    {
        return (STRENGTH[grades[Ability.STRENGTH.ordinal()].ordinal()] * base) + offset;
    }
    private float stamina(float base, float offset)
    {
        return (STAMINA[grades[Ability.STAMINA.ordinal()].ordinal()] * base) + offset;
    }
    private float dexterity(float base, float offset)
    {
        return (DEXTERITY[grades[Ability.DEXTERITY.ordinal()].ordinal()] * base) + offset;
    }
    private float agility(float base, float offset)
    {
        return (AGILITY[grades[Ability.AGILITY.ordinal()].ordinal()] * base) + offset;
    }
    private float constitution(float base, float offset)
    {
        return (CONSTITUTION[grades[Ability.CONSTITUTION.ordinal()].ordinal()] * base) + offset;
    }
    private float vitality(float base, float offset)
    {
        return (VITALITY[grades[Ability.VITALITY.ordinal()].ordinal()] * base) + offset;
    }
    private float wisdom(float base, float offset)
    {
        return (WISDOM[grades[Ability.WISDOM.ordinal()].ordinal()] * base) + offset;
    }
    private float will(float base, float offset)
    {
        return (WILL[grades[Ability.WILL.ordinal()].ordinal()] * base) + offset;
    }
    private float intelligence(float base, float offset)
    {
        return (INTELLIGENCE[grades[Ability.INTELLIGENCE.ordinal()].ordinal()] * base) + offset;
    }
    private float knowledge(float base, float offset)
    {
        return (KNOWLEDGE[grades[Ability.KNOWLEDGE.ordinal()].ordinal()] * base) + offset;
    }
    private float presence(float base, float offset)
    {
        return (PRESENCE[grades[Ability.PRESENCE.ordinal()].ordinal()] * base) + offset;
    }

    public float agility(GradeEnum grade) { return AGILITY[grade.ordinal()]; }

    /*****************************************************************************/
    /****************************** Balancing Tools ******************************/
    /*****************************************************************************/

    //float airSpeed() { return agility(0.4F, dexterity(0.6,0)); } - For two? (40%/60% split)

    public float airSpeed() { return agility(0.2F, 0); }
    public float swimSpeed() { return agility(3F, 0); }
    public float crawlSpeed() { return agility(0.05F, 0); }
    public float walkSpeed() { return agility(0.04F, 0); }
    public float runSpeed() { return agility(0.08F, 0); }
    public float lowerSprintSpeed() { return agility(0.10F, 0); }
    public float sprintSpeed() { return agility(0.13F, 0); }
    public float rushSpeed() { return agility(0.3F, 0); }

    public float maxClimbSpeed() { return agility(1F, 0); }
    public float maxStickSpeed() { return agility(1.5F, 0); }
    public float maxSlideSpeed() { return agility(0.3F, 0); }
    public float maxLowerGroundSpeed() { return agility(0.15F, 0); }
    public float maxGroundSpeed() { return agility(0.25F, 0); }
    public float maxTotalSpeed() { return agility(5F, 0); }

    public float airAccel() { return agility(0.1F, 0); }
    public float swimAccel() { return agility(0.3F, 0); }
    public float crawlAccel() { return agility(0.3F, 0); }
    public float climbAccel() { return agility(0.3F, 0); }
    public float runAccel() { return agility(0.4F, 0); }

    public float jumpVel() { return agility(0.4F, 0); }

    public float climbLedgeTime() { return agility(1, 0); }
    public float dashRecoverTime() { return agility(1, 0); }
    public float minTumbleTime() { return agility(1F, 0); }

    public float proneRecoverTime() { return constitution(1, 0); }
    public float staggerRecoverTime() { return strength(2, 0); }
    public float staggerParryMod() { return strength(2, 0); }
    public float staggerBlockMod() { return constitution(0.5F, 0); }

    public float friction() { return agility(0.5F, 0); }

    public int maxCommandChain() { return (int) intelligence(3, 0); }
}
