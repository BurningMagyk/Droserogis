package Gameplay.Weapons;

import Gameplay.Characters.CharacterStat;
import Util.GradeEnum;
import Util.Print;
import Util.Vec2;

public class WeaponStat
{
    private String[] info;
    private WeaponGrade[] damageGrades, rangeGrades, speedGrades,
            critThreshSpeedGrades, warmupGrades, cooldownGrades;
    private GradeEnum disruptThreshGrade;
    private float conditionModFloat;

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

    public WeaponStat(String disruptThresh, String... info)
    {
        disruptThreshGrade = GradeEnum.parseGrade(disruptThresh);

        if (info.length % 12 != 0)
        {
            Print.red("Error: " + info.length + " string parameters used for WeaponStat constructor.");
            return;
        }

        int opCount = info.length / 12;
        damageGrades = new WeaponGrade[opCount];
        rangeGrades = new WeaponGrade[opCount];
        speedGrades = new WeaponGrade[opCount];
        critThreshSpeedGrades = new WeaponGrade[opCount];
        warmupGrades = new WeaponGrade[opCount];
        cooldownGrades = new WeaponGrade[opCount];
        this.info = info;
    }

    private GradeEnum[] getCharGrade(CharacterStat charStat, String string)
    {
        return GradeEnum.avg(charStat, charStat.parseAbilities(string));
    }


    public void setCharStat(CharacterStat charStat)
    {
        for (int i = 0, j = 0; i < info.length; i += 12, j++)
        {
            damageGrades[j] = new WeaponGrade(GradeEnum.parseGrade(info[i]),
                    getCharGrade(charStat, info[i + 1]));
            rangeGrades[j] = new WeaponGrade(GradeEnum.parseGrade(info[i + 2]),
                    getCharGrade(charStat, info[i + 3]));
            speedGrades[j] = new WeaponGrade(GradeEnum.parseGrade(info[i + 4]),
                    getCharGrade(charStat, info[i + 5]));
            critThreshSpeedGrades[j] = new WeaponGrade(GradeEnum.parseGrade(info[i + 6]),
                    getCharGrade(charStat, info[i + 7]));
            warmupGrades[j] = new WeaponGrade(GradeEnum.parseGrade(info[i + 8]),
                    getCharGrade(charStat, info[i + 9]));
            cooldownGrades[j] = new WeaponGrade(GradeEnum.parseGrade(info[i + 10]),
                    getCharGrade(charStat, info[i + 11]));
        }

        conditionModFloat = charStat.agility(getCharGrade(charStat, "AGI")[0]);
    }

    /*****************************************************************************/
    /****************************** Balancing Tools ******************************/
    /*****************************************************************************/

    private GradeEnum damage(int i)
    {
        WeaponGrade weaponGrade = damageGrades[i];

        // TODO: return final grade based on weaponGrade member values
        return GradeEnum.D;
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

    private float critThreshSpeed(int i)
    {
        WeaponGrade weaponGrade = critThreshSpeedGrades[i];

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

    GradeEnum disruptThresh() { return disruptThreshGrade; }

    float conditionMod() { return conditionModFloat; }

    GradeEnum[] damages()
    {
        GradeEnum[] vals = new GradeEnum[damageGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = damage(i); }
        return vals;
    }

    float[] ranges()
    {
        float[] vals = new float[rangeGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = range(i); }
        return vals;
    }

    float[] speeds()
    {
        float[] vals = new float[speedGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = speed(i); }
        return vals;
    }

    float[] critThreshSpeeds()
    {
        float[] vals = new float[speedGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = critThreshSpeed(i); }
        return vals;
    }

    Vec2[] waits()
    {
        Vec2[] vals = new Vec2[warmupGrades.length];
        for (int i = 0; i < vals.length; i++) { vals[i] = new Vec2(warmup(i), cooldown(i)); }
        return vals;
    }
}