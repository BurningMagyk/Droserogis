package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Vec2;

public class Command
{
    private final static float AIRBORNE_RATIO = 1.5F;

    /* DIR is only considered with the MOMENTUM type */
    final DirEnum DIR;
    final int HORIZ, VERT;
    final boolean SPRINT;
    final StateType TYPE;
    final int ATTACK_KEY;

    boolean hold = true;

    private enum StateType
    {
        LOW, MOMENTUM, FREE, STANDARD
    }

    Command(int attackKey, int horiz, int vert, Actor.State state, Vec2 vel)
    {
        ATTACK_KEY = attackKey;
        HORIZ = horiz;
        VERT = vert;

        if (state.isAirborne())
        {
            if (vel.x == 0 && vel.y == 0)
            {
                TYPE = StateType.FREE;
                DIR = DirEnum.NONE;
            }
            else if (Math.abs(vel.x) >= Math.abs(vel.y) * AIRBORNE_RATIO)
            {
                TYPE = StateType.MOMENTUM;
                if (vel.x < 0) DIR = DirEnum.UP;
                else DIR = DirEnum.DOWN;
            }
            else if (Math.abs(vel.y) >= Math.abs(vel.x) * AIRBORNE_RATIO)
            {
                TYPE = StateType.MOMENTUM;
                if (vel.y < 0) DIR = DirEnum.LEFT;
                else DIR = DirEnum.RIGHT;
            }
            else
            {
                TYPE = StateType.FREE;
                DIR = DirEnum.NONE;
            }
        }
        else if (state == Actor.State.SLIDE)
        {
            TYPE = StateType.MOMENTUM;
            if (vel.x == 0) DIR = DirEnum.NONE;
            else if (vel.x > 0) DIR = DirEnum.RIGHT;
            else DIR = DirEnum.LEFT;
        }
        else if (state == Actor.State.SWIM)
        {
            TYPE = StateType.FREE;
            DIR = DirEnum.NONE;
        }
        else if (state.isLow())
        {
            TYPE = StateType.LOW;
            DIR = DirEnum.NONE;
        }
        else
        {
            TYPE = StateType.STANDARD;
            DIR = DirEnum.NONE;
        }

        SPRINT = state.isSprint();
    }

    void letGo(int attackKey) { if (attackKey == ATTACK_KEY) hold = false; }
}
