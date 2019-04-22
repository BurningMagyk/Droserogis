package Gameplay.Weapons;

import Gameplay.DirEnum;

public class Infliction
{
    private Weapon source;

    private DirEnum dir;
    private boolean finished = false;

    Infliction(Weapon source, DirEnum dir)
    {
        this.source = source;
        this.dir = dir;
    }

    void finish() { finished = true; }
    public boolean isFinished() { return finished; }

    public boolean sameSource(Infliction other) { return this.source == other.source; }

    @Override
    public String toString() {
        return "Inflicted with dir " + dir;
    }
}