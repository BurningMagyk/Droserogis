package Util;

import Gameplay.Characters.CharacterStat;

public enum GradeEnum
{
        SSS__, SSS_, SSS,
        SS__ , SS_ , SS ,
        S__  , S_  , S  ,

        A__  , A_  , A  ,
        B__  , B_  , B  ,
        C__  , C_  , C  ,

        D__  , D_  , D  ,
        E__  , E_  , E  ,
        F__  , F_  , F;

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

    public static GradeEnum parseGrade(String string)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (string.equalsIgnoreCase(gradeStrings[i]))
                return values()[i];
        }
        return null;
    }

    public static String getGradeString(GradeEnum gr)
    {
        String grStr = "";
        GradeEnum gradesArr[] = values();

        for(int i = 0; i < gradeStrings.length; i++)
        {
            if(gr == gradesArr[i]) grStr = gradeStrings[i];
        }

        return grStr;
    }

    public static GradeEnum[] avg(CharacterStat stat, CharacterStat.Ability... args)
    {
        float sum = 0;
        for (CharacterStat.Ability arg : args) { sum += stat.getGrade(arg).ordinal(); }
        float a = sum / args.length;
        GradeEnum[] vals = values();
        if (a < 0.1F) return new GradeEnum[] { vals[0], vals[0] };
        for (int i = 1; i < vals.length; i++)
        {
            if (Math.abs(a - vals[i].ordinal()) < 0.1F) return new GradeEnum[] { vals[i], vals[i] };
            if (a > vals[i - 1].ordinal() && a < vals[i].ordinal()) return new GradeEnum[] { vals[i - 1], vals[i] };
        }
        return new GradeEnum[]{vals[vals.length - 1], vals[vals.length - 1]};
    }
}
