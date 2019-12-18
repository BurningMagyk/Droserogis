package Gameplay.Weapons;

import Gameplay.Actor;
import Gameplay.DirEnum;
import Util.Vec2;

public class Command
{
    private final int PREV_KEY;
    final int ATTACK_KEY;
    final DirEnum FACE, DIR;

    private StateType TYPE;
    private boolean SPRINT;

    enum StateType
    {
        LOW, PRONE, FREE, STANDARD
    }

    public Command(int attackKey, DirEnum face, DirEnum dir)
    {
        ATTACK_KEY = attackKey;
        PREV_KEY = -1;
        FACE = face;
        DIR = dir;
    }
    private Command(int attackKey, int prevKey, DirEnum face, DirEnum dir)
    {
        ATTACK_KEY = attackKey;
        PREV_KEY = prevKey;
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

    Command merge(Command command)
    {
        return new Command(command.ATTACK_KEY, ATTACK_KEY, FACE, DIR);
    }

    MeleeOperation.MeleeEnum getEnum() { return MeleeOperation.MeleeEnum.THRUST; } // for testing
}
