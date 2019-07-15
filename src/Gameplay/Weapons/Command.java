package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Vec2;

public class Command
{
    final int ATTACK_KEY;
    final DirEnum FACE;
    final DirEnum DIR;

    StateType TYPE;
    boolean SPRINT;

    boolean hold = true;

    enum StateType
    {
        LOW, PRONE, FREE, STANDARD
    }

    public Command(int attackKey, DirEnum face, DirEnum dir)
    {
        ATTACK_KEY = attackKey;
        FACE = face;
        DIR = dir;
    }

    Command setStats(Actor.State state, boolean canStand)
    {
        if (state.isAirborne() || state == Actor.State.SWIM)
            TYPE = StateType.FREE;
        else if (state == Actor.State.SLIDE || !canStand)
            TYPE = StateType.PRONE;
        else if (state.isLow())
            TYPE = StateType.LOW;
        else TYPE = StateType.STANDARD;

        SPRINT = state.isSprint();

        return this;
    }

    void letGo(int attackKey)
    {
        if (attackKey == ATTACK_KEY
                || attackKey == ATTACK_KEY - Actor.ATTACK_KEY_MOD)
            hold = false;
    }
}
