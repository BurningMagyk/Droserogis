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

//        for (int i = 0; i < grades.length; i++)
//        {
//            Print.blue(grades[i]);
//        }
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

    private static float[] floatVals() { return new float[] {
            0.25F, 0.30F, 0.35F,
            0.40F, 0.45F, 0.50F,
            0.55F, 0.60F, 0.65F,

            0.70F, 0.75F, 0.80F,
            0.85F, 0.90F, 0.95F,
            1.00F, 1.05F, 1.10F,

            1.15F, 1.20F, 1.25F,
            1.30F, 1.35F, 1.40F,
            1.45F, 1.50F, 1.55F }; }


    private final static float[] STRENGTH = floatVals();
    private final static float[] STAMINA = floatVals();
    private final static float[] DEXTERITY = floatVals();
    private final static float[] AGILITY = floatVals();
    private final static float[] CONSTITUTION = floatVals();
    private final static float[] VITALITY = floatVals();
    private final static float[] WISDOM = floatVals();
    private final static float[] WILL = floatVals();
    private final static float[] INTELLIGENCE = floatVals();
    private final static float[] KNOWLEDGE = floatVals();
    private final static float[] PRESENCE = floatVals();

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

    private int maxOrd = GradeEnum.SSS__.ordinal();
    private float strengthNeg(float base) { return STRENGTH[clamp(maxOrd - strength())] * base; }
    private float staminaNeg(float base) { return STRENGTH[clamp(maxOrd - stamina())] * base; }
    private float dexterityNeg(float base) { return STRENGTH[clamp(maxOrd - dexterity())] * base; }
    private float agilityNeg(float base) { return STRENGTH[clamp(maxOrd - agility())] * base; }
    private float constitutionNeg(float base) { return STRENGTH[clamp(maxOrd - constitution())] * base; }
    private float vitalityNeg(float base) { return STRENGTH[clamp(maxOrd - vitality())] * base; }
    private float wisdomNeg(float base) { return STRENGTH[clamp(maxOrd - wisdom())] * base; }
    private float willNeg(float base) { return STRENGTH[clamp(maxOrd - will())] * base; }
    private float intelligenceNeg(float base) { return STRENGTH[clamp(maxOrd - intelligence())] * base; }
    private float knowledgeNeg(float base) { return STRENGTH[clamp(maxOrd - knowledge())] * base; }
    private float presenceNeg(float base) { return STRENGTH[clamp(maxOrd - presence())] * base; }

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

    public float airSpeed() { return dexterity(0.10F); }
    public float swimSpeed() { return dexterity(0.1F); }
    public float crawlSpeed() { return agility(0.025F); }
    public float walkSpeed() { return agility(0.035F); }
    public float runSpeed() { return agility(0.07F); }
    public float lowerSprintSpeed() { return agility(0.05F); }
    public float sprintSpeed() { return agility(0.12F); }
    public float rushSpeed() { return agility(0.18F); }
    public float jutSpeed() { return agility(0.13F); }

    public float maxClimbSpeed() { return agility(0.15F) + dexterity(0.15F); }
    public float maxStickSpeed() { return agility(0.1F) + dexterity(0.1F); }
    public float maxSlideSpeed() { return agility(0.2F) + dexterity(0.2F); }
    public float maxLowerGroundSpeed() { return agility(0.05F) + dexterity(0.05F); }
    public float maxGroundSpeed() { return agility(0.25F) + dexterity(0.25F); }
    public float maxTotalSpeed() { return 3F; }

    public float airAccel() { return dexterity(0.15F); }
    public float swimAccel() { return agility(0.3F); }
    public float crawlAccel() { return agility(0.15F); }
    public float climbAccel(GradeEnum mass) {
        return AGILITY[clamp(agility() + strength() - mass.ordinal())] * 0.12F;
    }
    public float runAccel() { return agility(0.2F); }

    public float slopeAccelDiv() { return strength(0.3F) + agility(0.3F) + stamina(0.3F); }

    public float jumpVel(GradeEnum mass) {
        return STRENGTH[clamp(agility() + strength() - mass.ordinal())] * 0.12F;
    }

    public float climbLedgeTime(GradeEnum mass) {
        return AGILITY[clamp(GradeEnum.SSS__.ordinal() + mass.ordinal() - agility() - strength())] * 0.5F;
    }
    public float[] stairRecoverTime() { return new float[]
            { dexterityNeg(0.4F), staminaNeg(0.1F) }; }
    public float dashRecoverTime() { return agilityNeg(0.25F) + constitutionNeg(0.25F); }  // TODO: balance for chad
    public float minTumbleTime() { return agilityNeg(0.5F) + vitalityNeg(0.5F); } // TODO: balance for chad

    public float proneRecoverTime() { return constitutionNeg(0.3F) + vitalityNeg(0.3F) + agilityNeg(0.3F); }

    public GradeEnum[] landingThresh() {
        return new GradeEnum[] { GradeEnum.getGrade(agility() / 2),
                GradeEnum.getGrade(agility()) };  // TODO: balance for chad
    }
    public GradeEnum[] staggerThresh(GradeEnum mass) {
        return new GradeEnum[] { GradeEnum.getGrade(clamp((strength() + mass.ordinal()) / 4)),
                GradeEnum.getGrade(clamp(strength() + mass.ordinal()) / 2) };  // TODO: balance for chad
    }

    public float friction(GradeEnum mass) { return agility(1F); }

    public GradeEnum weaponGrip() { return GradeEnum.getGrade(strength()); }
}
