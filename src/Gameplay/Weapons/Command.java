package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Vec2;

public class Command
{
    final static float AIRBORNE_RATIO = 1.5F;

    /* DIR is only considered with the MOMENTUM type */
    final DirEnum DIR;
    final int HORIZ, VERT;
    final boolean SPRINT;
    boolean hold;
    final StateType type;

    private enum StateType
    {
        LOW, MOMENTUM, FREE, STANDARD
    }

    Command(int horiz, int vert, boolean sprint, Actor.State state, Vec2 vel)
    {
        HORIZ = horiz;
        VERT = vert;
        SPRINT = sprint;

        hold = true;

        if (state.isAirborne())
        {
            if (vel.x == 0 && vel.y == 0)
            {
                type = StateType.FREE;
                DIR = DirEnum.NONE;
            }
            else if (Math.abs(vel.x) >= Math.abs(vel.y) * AIRBORNE_RATIO)
            {
                type = StateType.MOMENTUM;
                if (vel.x < 0) DIR = DirEnum.UP;
                else DIR = DirEnum.DOWN;
            }
            else if (Math.abs(vel.y) >= Math.abs(vel.x) * AIRBORNE_RATIO)
            {
                type = StateType.MOMENTUM;
                if (vel.y < 0) DIR = DirEnum.LEFT;
                else DIR = DirEnum.RIGHT;
            }
            else
            {
                type = StateType.FREE;
                DIR = DirEnum.NONE;
            }
        }
        else if (state == Actor.State.SLIDE)
        {
            type = StateType.MOMENTUM;
            if (vel.x == 0) DIR = DirEnum.NONE;
            else if (vel.x > 0) DIR = DirEnum.RIGHT;
            else DIR = DirEnum.LEFT;
        }
        else if (state == Actor.State.SWIM)
        {
            type = StateType.FREE;
            DIR = DirEnum.NONE;
        }
        else if (state.isLow())
        {
            type = StateType.LOW;
            DIR = DirEnum.NONE;
        }
        else
        {
            type = StateType.STANDARD;
            DIR = DirEnum.NONE;
        }

        sprint = state.isSprint();
    }

    public void letGo() { hold = false; }
}
