/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities.Characters;

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

    int clamp(int ord) { return Math.min(Math.max(0, ord), GradeEnum.values().length - 1); }

    /****************************************************************************/
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

    private int strength() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int stamina() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int dexterity() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int agility() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int constitution() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int vitality() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int wisdom() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int will() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int intelligence() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int knowledge() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }
    private int presence() { return grades[Ability.STRENGTH.ordinal()].ordinal(); }

    private float strength(float base) { return STRENGTH[strength()] * base; }
    private float stamina(float base)
    {
        return STAMINA[stamina()] * base;
    }
    private float dexterity(float base)
    {
        return DEXTERITY[dexterity()] * base;
    }
    private float agility(float base)
    {
        return AGILITY[agility()] * base;
    }
    private float constitution(float base) { return CONSTITUTION[constitution()] * base; }
    private float vitality(float base) { return VITALITY[vitality()] * base; }
    private float wisdom(float base)
    {
        return WISDOM[wisdom()] * base;
    }
    private float will(float base)
    {
        return WILL[will()] * base;
    }
    private float intelligence(float base) { return INTELLIGENCE[intelligence()] * base; }
    private float knowledge(float base)
    {
        return KNOWLEDGE[knowledge()] * base;
    }
    private float presence(float base)
    {
        return PRESENCE[presence()] * base;
    }

    /*****************************************************************************/
    /****************************** Balancing Tools ******************************/
    /*****************************************************************************/

    public int hitPoints() { return (int) (vitality(90) + constitution(10)); }

    public float airSpeed() { return dexterity(0.15F); }
    public float swimSpeed() { return dexterity(3F); }
    public float crawlSpeed() { return agility(0.025F); }
    public float walkSpeed() { return agility(0.035F); }
    public float runSpeed() { return agility(0.07F); }
    public float lowerSprintSpeed() { return agility(0.05F); }
    public float sprintSpeed() { return agility(0.14F); }
    public float rushSpeed() { return agility(0.3F); }

    public float maxClimbSpeed() { return agility(0.15F) + dexterity(0.15F); }
    public float maxStickSpeed() { return agility(0.1F) + dexterity(0.1F); }
    public float maxSlideSpeed() { return agility(0.2F) + dexterity(0.2F); }
    public float maxLowerGroundSpeed() { return agility(0.05F) + dexterity(0.05F); }
    public float maxGroundSpeed() { return agility(0.1F) + dexterity(0.1F); }
    public float maxTotalSpeed() { return 3F; }

    public float airAccel() { return dexterity(0.2F); }
    public float swimAccel() { return agility(0.3F); }
    public float crawlAccel() { return agility(0.15F); }
    public float climbAccel(GradeEnum mass) {
        return AGILITY[clamp(agility() + strength() - mass.ordinal())] * 0.1F;
    }
    public float runAccel() { return agility(0.2F); }

    public float slopeAccelDiv() { return strength(1) + agility(1); }

    public float jumpVel(GradeEnum mass) {
        return STRENGTH[clamp(agility() + strength() - mass.ordinal())] * 0.15F;
    }

    public float climbLedgeTime(GradeEnum mass) {
        return AGILITY[clamp(agility() + strength() - mass.ordinal())] * 0.85F;
    }
    public float[] stairRecoverTime() { return new float[]
            { agility(0.25F), dexterity(0.2F),
                    stamina(0.25F) + constitution(0.25F) }; }
    public float dashRecoverTime() { return agility(0.25F) + constitution(0.25F); }
    public float minTumbleTime() { return agility(0.5F) + vitality(0.5F); }

    public float proneRecoverTime() { return constitution(0.3F) + vitality(0.3F) + agility(0.3F); }

    public GradeEnum[] landingThresh() {
        return new GradeEnum[] { GradeEnum.getGrade(agility() / 2), GradeEnum.getGrade(agility()) };
    }
    public GradeEnum[] staggerThresh() {
        return new GradeEnum[] { GradeEnum.getGrade(strength() / 2), GradeEnum.getGrade(strength()) };
    }

    public float friction() { return agility(0.35F); }

    public GradeEnum weaponGrip() { return GradeEnum.getGrade(strength()); }
}
