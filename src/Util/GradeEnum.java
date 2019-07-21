package Util;

import Gameplay.Characters.CharacterStat;

public enum GradeEnum
{
        F    , F_   , F__  ,
        E    , E_   , E__  ,
        D    , D_   , D__  ,

        C    , C_   , C__  ,
        B    , B_   , B__  ,
        A    , A_   , A__  ,

        S    , S_   , S__  ,
        SS   , SS_  , SS__ ,
        SSS  , SSS_ , SSS__;

    private final static String[] gradeStrings = new String[]{
        "F-"  , "F"  , "F+"  ,  // Impaired, small child
        "E-"  , "E"  , "E+"  ,  // Inexperienced
        "D-"  , "D"  , "D+"  ,  // Experienced

        "C-"  , "C"  , "C+"  ,  // Trained
        "B-"  , "B"  , "B+"  ,  // Veteran
        "A-"  , "A"  , "A+"  ,  // Master

        "S-"  , "S"  , "S+"  ,  // Meta-human
        "SS-" , "SS" , "SS+" ,  // Super-human
        "SSS-", "SSS", "SSS+"}; // Transcendent


    @Override
    public String toString() { return gradeStrings[ordinal()]; }

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
