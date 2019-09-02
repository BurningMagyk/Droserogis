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
            "STR", "STA",
            "DEX", "AGI",
            "CON", "VIT",
            "WIS", "WIL",
            "INT", "KNO",
            "PRE"};

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

    private float strength(float base)
    {
        return STRENGTH[grades[Ability.STRENGTH.ordinal()].ordinal()] * base;
    }
    private float stamina(float base)
    {
        return STAMINA[grades[Ability.STAMINA.ordinal()].ordinal()] * base;
    }
    private float dexterity(float base)
    {
        return DEXTERITY[grades[Ability.DEXTERITY.ordinal()].ordinal()] * base;
    }
    private float agility(float base)
    {
        return AGILITY[grades[Ability.AGILITY.ordinal()].ordinal()] * base;
    }
    private float constitution(float base)
    {
        return CONSTITUTION[grades[Ability.CONSTITUTION.ordinal()].ordinal()] * base;
    }
    private float vitality(float base)
    {
        return VITALITY[grades[Ability.VITALITY.ordinal()].ordinal()] * base;
    }
    private float wisdom(float base)
    {
        return WISDOM[grades[Ability.WISDOM.ordinal()].ordinal()] * base;
    }
    private float will(float base)
    {
        return WILL[grades[Ability.WILL.ordinal()].ordinal()] * base;
    }
    private float intelligence(float base)
    {
        return INTELLIGENCE[grades[Ability.INTELLIGENCE.ordinal()].ordinal()] * base;
    }
    private float knowledge(float base)
    {
        return KNOWLEDGE[grades[Ability.KNOWLEDGE.ordinal()].ordinal()] * base;
    }
    private float presence(float base)
    {
        return PRESENCE[grades[Ability.PRESENCE.ordinal()].ordinal()] * base;
    }

    public float agility(GradeEnum grade) { return AGILITY[grade.ordinal()]; }

    /*****************************************************************************/
    /****************************** Balancing Tools ******************************/
    /*****************************************************************************/

    //float airSpeed() { return agility(0.4F, dexterity(0.6,0)); } - For two? (40%/60% split)

    public float airSpeed() { return 0.2F; }
    public float swimSpeed() { return agility(3F); }
    public float crawlSpeed() { return 0.02F; } // agility
    public float walkSpeed() { return 0.03F; } // agility
    public float runSpeed() { return 0.06F; } // agility
    public float lowerSprintSpeed() { return 0.05F; } // agility
    public float sprintSpeed() { return 0.09F; } // agility
    public float rushSpeed() { return agility(0.3F); }

    public float maxClimbSpeed() { return agility(1F); }
    public float maxStickSpeed() { return agility(1.5F); }
    public float maxSlideSpeed() { return agility(0.3F); }
    public float maxLowerGroundSpeed() { return agility(0.15F); }
    public float maxGroundSpeed() { return agility(0.25F); }
    public float maxTotalSpeed() { return agility(5F); }

    public float airAccel() { return agility(0.1F); }
    public float swimAccel() { return agility(0.3F); }
    public float crawlAccel() { return agility(0.3F); }
    public float climbAccel() { return agility(0.3F); }
    public float runAccel() { return agility(0.4F); }

    public float jumpVel() { return 0.2F; } // strength

    public float climbLedgeTime() { return agility(1); }
    public float[] stairRecoverTime() { return new float[]
            { agility(0.45F), dexterity(0.4F), stamina(0.5F) }; }
    public float dashRecoverTime() { return agility(1); }
    public float minTumbleTime() { return agility(1F); }

    public float proneRecoverTime() { return constitution(1); }
    public float staggerRecoverTime() { return strength(2); }
    public float staggerParryMod() { return strength(2); }
    public float staggerBlockMod() { return constitution(0.5F); }

    public float[] landingThresh() { return new float[] { agility(0.5F), agility(1) }; }

    public float friction() { return agility(0.5F); }

    public int maxCommandChain() { return (int) intelligence(3); }
}
