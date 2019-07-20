package Gameplay.Weapons;

import Gameplay.Characters.CharacterStat;
import Util.GradeEnum;
import Util.Print;

public class WeaponStat
{
    private String[] info;
    private WeaponGrade[] damageGrades, rangeGrades, speedGrades, warmupGrades, cooldownGrades;

    private class WeaponGrade
    {
        GradeEnum nativeGrade, charGrade1, charGrade2;
        WeaponGrade(GradeEnum nativeGrade, GradeEnum[] charGrades)
        {
            this.nativeGrade = nativeGrade;
            charGrade1 = charGrades[0];
            charGrade2 = charGrades[1];
        }
    }

    private GradeEnum[] getCharGrade(CharacterStat charStat, String string)
    {
        return GradeEnum.avg(charStat, charStat.parseAbilities(string));
    }

    WeaponStat(String[] info)
    {
        if (info.length % 10 != 0)
        {
            Print.red("Error: " + info.length + " string parameters used for WeaponStat constructor.");
            return;
        }

        int opCount = info.length / 10;
        damageGrades = new WeaponGrade[opCount];
        rangeGrades = new WeaponGrade[opCount];
        speedGrades = new WeaponGrade[opCount];
        warmupGrades = new WeaponGrade[opCount];
        cooldownGrades = new WeaponGrade[opCount];
        this.info = info;
    }


    public void setCharStat(CharacterStat charStat)
    {
        for (int i = 0; i < info.length; i += 10)
        {
            damageGrades[i] = new WeaponGrade(GradeEnum.parseGrade(info[i]),
                    getCharGrade(charStat, info[i + 1]));
            rangeGrades[i] = new WeaponGrade(GradeEnum.parseGrade(info[i + 2]),
                    getCharGrade(charStat, info[i + 3]));
            speedGrades[i] = new WeaponGrade(GradeEnum.parseGrade(info[i + 4]),
                    getCharGrade(charStat, info[i + 5]));
            warmupGrades[i] = new WeaponGrade(GradeEnum.parseGrade(info[i + 6]),
                    getCharGrade(charStat, info[i + 7]));
            cooldownGrades[i] = new WeaponGrade(GradeEnum.parseGrade(info[i + 8]),
                    getCharGrade(charStat, info[i + 9]));
        }
    }

    /*****************************************************************************/
    /****************************** Balancing Tools ******************************/
    /*****************************************************************************/

    private GradeEnum damage(int i)
    {
        WeaponGrade weaponGrade = damageGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return null;
    }

    private float range(int i)
    {
        WeaponGrade weaponGrade = rangeGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return 1;
    }

    private float speed(int i)
    {
        WeaponGrade weaponGrade = speedGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return 1;
    }

    private float warmup(int i)
    {
        WeaponGrade weaponGrade = warmupGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return 1;
    }

    private float cooldown(int i)
    {
        WeaponGrade weaponGrade = cooldownGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return 1;
    }

    GradeEnum[] damage()
    {
        GradeEnum[] vals = new GradeEnum[damageGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = damage(i); }
        return vals;
    }

    float[] range()
    {
        float[] vals = new float[rangeGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = range(i); }
        return vals;
    }

    float[] speed()
    {
        float[] vals = new float[speedGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = speed(i); }
        return vals;
    }

    float[] warmup()
    {
        float[] vals = new float[warmupGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = warmup(i); }
        return vals;
    }

    float[] cooldown()
    {
        float[] vals = new float[cooldownGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = cooldown(i); }
        return vals;
    }
}
