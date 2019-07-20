package Gameplay.Weapons;

import Gameplay.Characters.CharacterStat;
import Util.GradeEnum;
import Util.Print;

public class WeaponStat
{
    private WeaponGrade[] damageGrades, rangeGrades, speedGrades, warmupGrades, cooldownGrades;

    class WeaponGrade
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

    WeaponStat(CharacterStat charStat, String[] info)
    {
        if (info.length % 10 != 0)
        {
            Print.red(info.length + " string parameters used for WeaponStat");
            return;
        }

        int opCount = info.length / 10;
        damageGrades = new WeaponGrade[opCount];
        rangeGrades = new WeaponGrade[opCount];
        speedGrades = new WeaponGrade[opCount];
        warmupGrades = new WeaponGrade[opCount];
        cooldownGrades = new WeaponGrade[opCount];
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

    GradeEnum damage(int i)
    {
        WeaponGrade weaponGrade = damageGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return null;
    }

    GradeEnum range(int i)
    {
        WeaponGrade weaponGrade = rangeGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return null;
    }

    GradeEnum speed(int i)
    {
        WeaponGrade weaponGrade = speedGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return null;
    }

    GradeEnum warmup(int i)
    {
        WeaponGrade weaponGrade = warmupGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return null;
    }

    GradeEnum cooldown(int i)
    {
        WeaponGrade weaponGrade = cooldownGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return null;
    }
}
