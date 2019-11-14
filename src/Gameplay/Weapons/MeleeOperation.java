package Gameplay.Weapons;

import Gameplay.DirEnum;
import Util.GradeEnum;
import Util.Vec2;

public class MeleeOperation
{
    enum MeleeEnum
    {
        THRUST, THRUST_UP, THRUST_DOWN, THRUST_DIAG_UP, THRUST_DIAG_DOWN,
        THRUST_LUNGE,
        STAB, STAB_UNTERHAU,
        SWING, SWING_UNTERHAU, SWING_UNTERHAU_CROUCH, SWING_UP_FORWARD,
        SWING_UP_BACKWARD, SWING_DOWN_FORWARD, SWING_DOWN_BACKWARD,
        SWING_LUNGE, SWING_LUNGE_UNTERHAU,
        GRAB,
        DRAW, LOAD, SHOOT;
    }

    private String name;
    private Vec2 waits;
    private DirEnum funcDir;
    private GradeEnum damage;
    private Weapon.Tick[] execJourney;

    // TODO: conditionAppCycle set in WeaponTypeEnum (daggers would be different)
    // TODO: conditionAppInfliction should be integrated into damage
    // TODO: easyToBlock set in WeaponTypeEnum (sword thrusts harder to block than hammer thrusts)
    // TODO: disruptive set universally in WeaponTypeEnum (swings true, others false)

    MeleeOperation(
            WeaponTypeEnum.Stat stat,
            String name,
            Vec2 waits,
            DirEnum funcDir,
            GradeEnum damage,
            Weapon.Tick[] execJourney
    )
    {
        this.name = name;
        this.waits = waits.clone();
        this.funcDir = funcDir;
        this.damage = damage;
        this.execJourney = execJourney;
    }

}
