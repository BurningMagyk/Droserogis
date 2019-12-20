package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Vec2;

public class Command
{
    private final int PREV_KEY;
    final int ATTACK_KEY;
    final DirEnum FACE, DIR;

    private enum StateType { LOW, PRONE, FREE, STANDARD }

    final MeleeOperation.MeleeEnum ENUM;
    private StateType TYPE;
    private boolean SPRINT;

    public Command(int attackKey, DirEnum face, DirEnum dir,
                   Actor.State state, boolean canStand)
    {
        ATTACK_KEY = attackKey;
        PREV_KEY = -1;
        FACE = face;
        DIR = dir;
        ENUM = getEnum(state, canStand);
    }
    private Command(int attackKey, int prevKey, DirEnum face, DirEnum dir,
                    Actor.State state, boolean canStand)
    {
        ATTACK_KEY = attackKey;
        PREV_KEY = prevKey;
        FACE = face;
        DIR = dir;
        ENUM = getEnum(state, canStand);
    }

    private MeleeOperation.MeleeEnum getEnum(Actor.State state, boolean canStand)
    {
        boolean forward = DIR.getHoriz() == DirEnum.NONE,
                up = DIR.getVert() == DirEnum.UP,
                down = DIR.getVert() == DirEnum.DOWN;

        if (state.isAirborne() || state == Actor.State.SWIM)
        {
            if (ATTACK_KEY == 1)
            {

            }
            if (ATTACK_KEY == 2)
            {

            }
            if (ATTACK_KEY == 3)
            {

            }
        }
        if (state == Actor.State.SLIDE || !canStand)
        {
            if (ATTACK_KEY == 1)
            {

            }
            if (ATTACK_KEY == 2)
            {

            }
            if (ATTACK_KEY == 3)
            {

            }
        }

        boolean sprint = state.isSprint() && forward;
        down = down || state.isLow();

        if (ATTACK_KEY == 1)
        {
            if (sprint) return MeleeOperation.MeleeEnum.LUNGE;
        }
        if (ATTACK_KEY == 2)
        {
            if (sprint) return MeleeOperation.MeleeEnum.SHOVE;
        }
        if (ATTACK_KEY == 3)
        {
            if (PREV_KEY == 1)
            {
                if (sprint) return MeleeOperation.MeleeEnum.TACKLE;
            }
        }

        // for debugging
        return MeleeOperation.MeleeEnum.THRUST;
    }

//    Command setStats(Actor.State state, boolean canStand)
//    {
//        if (state.isAirborne() || state == Actor.State.SWIM)
//            TYPE = StateType.FREE;
//        else if (state == Actor.State.SLIDE || !canStand)
//            TYPE = StateType.PRONE;
//        else if (state.isLow())
//            TYPE = StateType.LOW;
//        else TYPE = StateType.STANDARD;
//
//        SPRINT = state.isSprint();
//
//        return this;
//    }

    Command merge(Command command, Actor.State state, boolean canStand)
    {
        return new Command(command.ATTACK_KEY, ATTACK_KEY, FACE, DIR, state, canStand);
    }
}
