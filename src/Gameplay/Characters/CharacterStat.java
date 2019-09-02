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

    public float airSpeed() { return 0.12F; } // dexterity
    public float swimSpeed() { return agility(3F); }
    public float crawlSpeed() { return 0.02F; } // agility
    public float walkSpeed() { return 0.03F; } // agility
    public float runSpeed() { return 0.06F; } // agility
    public float lowerSprintSpeed() { return 0.07F; } // agility
    public float sprintSpeed() { return 0.11F; } // agility
    public float rushSpeed() { return agility(0.3F); } // agility

    public float maxClimbSpeed() { return 0.3F; } // agility + dexterity
    public float maxStickSpeed() { return 0.05F; } // agility + dexterity
    public float maxSlideSpeed() { return 0.4F; } // agility + dexterity
    public float maxLowerGroundSpeed() { return 0.08F; } // agility + dexterity
    public float maxGroundSpeed() { return 0.15F; } // agility + dexterity
    public float maxTotalSpeed() { return 3F; } // none

    public float airAccel() { return 0.1F; } // dexterity
    public float swimAccel() { return 0.3F; } // agility
    public float crawlAccel() { return agility(0.3F); } // agility
    public float climbAccel() { return 0.1F; } // agility
    public float runAccel() { return agility(0.4F); } // agility

    public float jumpVel() { return 0.25F; } // strength

    public float climbLedgeTime() { return agility(1); } // agility
    public float[] stairRecoverTime() { return new float[]
            { agility(0.45F), dexterity(0.4F), stamina(0.5F) }; }
    public float dashRecoverTime() { return agility(1); } // agility
    public float minTumbleTime() { return agility(1F); } // agility

    public float proneRecoverTime() { return constitution(1); } // constitution
    public float staggerRecoverTime() { return strength(2); } // strength
    public float staggerParryMod() { return strength(2); } // strength
    public float staggerBlockMod() { return constitution(0.5F); } // strength

    public float[] landingThresh() { return new float[] { agility(0.5F), agility(1) }; }

    public float friction() { return agility(0.5F); } // agility

    public int maxCommandChain() { return (int) intelligence(3); } // intelligence
}
