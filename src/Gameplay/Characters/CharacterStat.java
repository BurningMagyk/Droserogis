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

    private float strength(float base) { return STRENGTH[grades[Ability.STRENGTH.ordinal()].ordinal()] * base; }
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
    private float constitution(float base) { return CONSTITUTION[grades[Ability.CONSTITUTION.ordinal()].ordinal()] * base; }
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
    private float intelligence(float base) { return INTELLIGENCE[grades[Ability.INTELLIGENCE.ordinal()].ordinal()] * base; }
    private float knowledge(float base)
    {
        return KNOWLEDGE[grades[Ability.KNOWLEDGE.ordinal()].ordinal()] * base;
    }
    private float presence(float base)
    {
        return PRESENCE[grades[Ability.PRESENCE.ordinal()].ordinal()] * base;
    }
    private float agility(GradeEnum grade) { return AGILITY[grade.ordinal()]; }

    /*****************************************************************************/
    /****************************** Balancing Tools ******************************/
    /*****************************************************************************/

    //float airSpeed() { return agility(0.4F, dexterity(0.6,0)); } - For two? (40%/60% split)

    public int hitPoints() { return 100; } // vitality

    public float airSpeed() { return 9.6F; } // dexterity
    public float swimSpeed() { return agility(100F); }
    public float crawlSpeed() { return 1.6F; } // agility
    public float walkSpeed() { return 2.4F; } // agility
    public float runSpeed() { return 4.8F; } // agility
    public float lowerSprintSpeed() { return 5.6F; } // agility
    public float sprintSpeed() { return 8.8F; } // agility
    public float rushSpeed() { return agility(14.4F); } // agility

    public float maxClimbSpeed() { return 24F; } // agility + dexterity
    public float maxStickSpeed() { return 4F; } // agility + dexterity
    public float maxSlideSpeed() { return 32F; } // agility + dexterity
    public float maxLowerGroundSpeed() { return 8F; } // agility + dexterity
    public float maxGroundSpeed() { return 16F; } // agility + dexterity
    public float maxTotalSpeed() { return 200F; } // none

    public float airAccel() { return 8F; } // dexterity
    public float swimAccel() { return 24F; } // agility
    public float crawlAccel() { return 12F; } // agility
    public float climbAccel() { return 8F; } // agility
    public float runAccel() { return 16F; } // agility

    public float jumpVel() { return 8F; } // strength + agility

    public float climbLedgeTime() { return 0.85F; } // agility
    public float[] stairRecoverTime() { return new float[]
            { agility(0.25F), dexterity(0.2F), stamina(0.5F) }; }
    public float dashRecoverTime() { return 0.5F; } // agility
    public float minTumbleTime() { return 1F; } // agility

    public float proneRecoverTime() { return 0.85F; } // constitution

    public GradeEnum[] landingThresh()
    {
        GradeEnum agilityGrade = grades[Ability.AGILITY.ordinal()];
        return new GradeEnum[] { GradeEnum.getGrade(agilityGrade.ordinal() / 2), agilityGrade };
    }
    public GradeEnum[] staggerThresh()
    {
        GradeEnum strengthGrade = grades[Ability.STRENGTH.ordinal()];
        return new GradeEnum[] { GradeEnum.getGrade(strengthGrade.ordinal() / 2), strengthGrade };
    }

    public float friction() { return 2F; } // agility

    public int maxCommandChain() { return (int) 2; } // intelligence
}
