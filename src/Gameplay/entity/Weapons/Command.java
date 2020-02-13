package Gameplay.entity.Weapons;

import Gameplay.entity.Actor;
import Gameplay.DirEnum;

import static Gameplay.entity.Weapons.MeleeOperation.MeleeEnum.*;

public class Command
{
    private final int PREV_KEY;
    public final int ATTACK_KEY;
    public final DirEnum FACE, DIR;

    private enum StateType { LOW, PRONE, FREE, STANDARD }

    public final MeleeOperation.MeleeEnum ENUM;
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
        boolean forward = DIR.getHoriz() != DirEnum.NONE,
                up = DIR.getVert() == DirEnum.UP,
                down = DIR.getVert() == DirEnum.DOWN;

        if (state.isAirborne() || state == Actor.State.SWIM)
        {
            if (ATTACK_KEY == 1)
            {
                if (PREV_KEY == 2)
                {
                    if (down) return SWING_DOWN_BACKWARD;
                    return SWING_UP_BACKWARD;
                }
                if (up)
                {
                    if (forward) return THRUST_DIAG_UP;
                    return THRUST_UP;
                }
                if (down)
                {
                    if (forward) return THRUST_DIAG_DOWN;
                    return THRUST_DOWN;
                }
                return THRUST;
            }
            if (ATTACK_KEY == 2)
            {
                if (PREV_KEY == 3)
                {
                    if (up)
                    {
                        if (forward) return GRAB_DIAG_UP;
                        return GRAB_UP;
                    }
                    if (down) return POUNCE;
                    return GRAB;
                }
                if (up) return SWING_UP_FORWARD;
                if (down) return SWING_DOWN_FORWARD;
                return SWING_AERIAL;
            }
            if (ATTACK_KEY == 3)
            {
                if (PREV_KEY == 2)
                {
                    if (up)
                    {
                        if (forward) return THROW_UP_DIAG;
                        return THROW_UP;
                    }
                    if (down)
                    {
                        if (forward) return THROW_DOWN_DIAG;
                        return THROW_DOWN;
                    }
                    if (forward) return THROW;
                    return TOSS;
                }
                return INTERACT;
            }
        }
        if (state == Actor.State.SLIDE || !canStand)
        {
            if (ATTACK_KEY == 1)
            {
                if (PREV_KEY == 2)
                {
                    if (down) return THRUST;
                    return THRUST_UP;
                }
                if (up)
                {
                    if (forward) return THRUST_DIAG_UP;
                    return THRUST_UP;
                }
                if (down) return THRUST;
                if (forward) return THRUST_DIAG_UP;
                return THRUST;
            }
            if (ATTACK_KEY == 2)
            {
                if (PREV_KEY == 3)
                {
                    if (up) return GRAB_UP;
                    if (down) return GRAB_ALT;
                    return GRAB;
                }
                return SWING_PRONE;
            }
            if (ATTACK_KEY == 3)
            {
                if (PREV_KEY == 2)
                {
                    if (up)
                    {
                        if (forward) return THROW_UP_DIAG;
                        return THROW_UP;
                    }
                    if (down) return TOSS;
                    if (forward) return THROW_UP_DIAG;
                    return TOSS;
                }
                return INTERACT;
            }
        }

        boolean sprint = state.isSprint() && forward;
        down = down || state.isLow();

        if (ATTACK_KEY == 1)
        {
            if (PREV_KEY == 2)
            {
                if (down) return STAB_UNTERHAU;
                return STAB;
            }
            if (sprint) return LUNGE;
            if (up)
            {
                if (forward) return THRUST_DIAG_UP;
                return THRUST_UP;
            }
            if (down) return THRUST_UNTERHAU;
            return THRUST;
        }
        if (ATTACK_KEY == 2)
        {
            if (PREV_KEY == 3)
            {
                if (sprint) return TACKLE;
                if (up)
                {
                    if (forward) return GRAB_DIAG_UP;
                    return GRAB_UP;
                }
                return GRAB;
            }
            if (sprint) return SHOVE;
            if (up) return SWING_UP_FORWARD;
            if (down) return SWING_UNTERHAU_C;
            return SWING;
        }
        if (ATTACK_KEY == 3)
        {
            if (PREV_KEY == 2)
            {
                if (up)
                {
                    if (forward) return THROW_UP_DIAG;
                    return THROW_UP;
                }
                if (down) return DROP;
                if (forward) return THROW;
                return TOSS;
            }
            return INTERACT;
        }

        // for debugging
        return THRUST;
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

    public Command merge(Command command, Actor.State state, boolean canStand)
    {
        return new Command(command.ATTACK_KEY, ATTACK_KEY, FACE, DIR, state, canStand);
    }
}
