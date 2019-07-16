package Gameplay;

public class CharacterStat
{
    enum Grade {
        SSS__, SSS_, SSS,
        SS__ , SS_ , SS ,
        S__  , S_  , S  ,

        A__  , A_  , A  ,
        B__  , B_  , B  ,
        C__  , C_  , C  ,

        D__  , D_  , D  ,
        E__  , E_  , E  ,
        F__  , F_  , F  }

    private final static String[] gradeStrings = new String[] {
        "SSS+", "SSS", "SSS-",  // Transcendent
        "SS+" , "SS" , "SS-" ,  // Super-human
        "S+"  , "S"  , "S-"  ,  // Meta-human
        "A+"  , "A"  , "A-"  ,  // Master
        "B+"  , "B"  , "B-"  ,  // Veteran
        "C+"  , "C"  , "C-"  ,  // Trained
        "D+"  , "D"  , "D-"  ,  // Experienced
        "E+"  , "E"  , "E-"  ,  // Inexperienced
        "F+"  , "F"  , "F-" };  // Impaired, small child

    private Grade parseGrade(String string)
    {
        for (int i = 0; i < Grade.values().length; i++)
        {
            if (string.equalsIgnoreCase(gradeStrings[i]))
                return Grade.values()[i];
        }
        return null;
    }

    private Grade[] grades;

    enum Ability {
        STRENGTH,
        STAMINA,

        DEXTERITY,
        AGILITY,

        CONSTITUTION,
        VITALITY,

        WILL,
        WISDOM,
        KNOWLEDGE,
        INTELLIGENCE,

        PRESENCE
    }

    CharacterStat(
            String str, String sta,
            String dex, String agi,
            String con, String vit,
            String wil, String wis, String kno, String nte,
            String pre)
    {
        grades = new Grade[Ability.values().length];
        grades[0] = parseGrade(str); grades[1] = parseGrade(sta);
        grades[2] = parseGrade(dex); grades[3] = parseGrade(agi);
        grades[4] = parseGrade(con); grades[5] = parseGrade(vit);
        grades[6] = parseGrade(wil); grades[7] = parseGrade(wis);
        grades[8] = parseGrade(kno); grades[9] = parseGrade(nte);
        grades[10] = parseGrade(pre);
    }

    private final static int iMASS = 0, iWIDTH = 0, iHEIGHT = 0;

    private final static float[] MASS = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] WIDTH = new float[] {
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,

            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F };

    private final static float[] HEIGHT = new float[] {
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
    private float agility(float base, float offset)
    {
        return (AGILITY[grades[Ability.AGILITY.ordinal()].ordinal()] * base) + offset;
    }

    float mass() { return MASS[grades[iMASS].ordinal()]; }
    float width() { return WIDTH[grades[iWIDTH].ordinal()]; }
    float height() { return HEIGHT[grades[iHEIGHT].ordinal()]; }

    //float attackPower() { return ATTACK_POWER[grades[Ability.STRENGTH.ordinal()]]; }
    float airSpeed() { return agility(0.2F, 0); }
    float swimSpeed() { return agility(3F, 0); }

    //private float airSpeed = 0.2F;
    private float swimSpeed = 3F;
    private float crawlSpeed = 0.05F;
    private float walkSpeed = 0.04F;
    private float runSpeed = 0.08F;
    private float lowerSprintSpeed = 0.10F;
    private float sprintSpeed = 0.13F;
    private float rushSpeed = 0.3F;

    private float maxClimbSpeed = 1F;
    private float maxStickSpeed = 1.5F;
    private float maxSlideSpeed = 0.3F;
    private float maxLowerGroundSpeed = 0.15F;
    private float maxGroundSpeed = 0.25F;
    private float maxTotalSpeed = 5F;

    private float airAccel = 0.1F;
    private float swimAccel = 0.3F;
    private float crawlAccel = 0.3F;
    private float climbAccel = crawlAccel;
    private float runAccel = 0.4F;

    private float jumpVel = 0.4F;

    private float climbLedgeTime = 1;
    private float dashRecoverTime = 1;
    private float minTumbleTime = 1F;

    private float proneRecoverTime = 1;
    private float staggerAttackedTime = 2;
    private float staggerBlockedMod = 0.5F;

    // friction

    private int maxCommandChain = 3;
}
