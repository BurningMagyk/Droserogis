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
        "SSS+", "SSS", "SSS-",
        "SS+" , "SS" , "SS-" ,
        "S+"  , "S"  , "S-"  ,
        "A+"  , "A"  , "A-"  ,
        "B+"  , "B"  , "B-"  ,
        "C+"  , "C"  , "C-"  ,
        "D+"  , "D"  , "D-"  ,
        "E+"  , "E"  , "E-"  ,
        "F+"  , "F"  , "F-" };

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

    CharacterStat(String... args)
    {
        grades = new Grade[args.length];
        for (int i = 0; i < args.length; i++)
        {
            grades[i] = parseGrade(args[i]);
        }
    }

    private final static int iMASS = 0;

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

    float mass() { return MASS[grades[iMASS].ordinal()]; }
}
