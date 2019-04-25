package Gameplay.Weapons;

import Gameplay.DirEnum;

public class Infliction
{
    private Weapon source;

    private DirEnum dir;
    private int damage;
    // TODO: add damage-type
    private boolean instant;

    private boolean finished = false;

    Infliction(Weapon source, DirEnum dir, int damage, boolean instant)
    {
        this.source = source;
        this.dir = dir;
        this.damage = damage;
        this.instant = instant;
    }

    void finish() { finished = true; }
    public boolean isFinished() { return finished; }

    public boolean sameSource(Infliction other) { return this.source == other.source; }

    public DirEnum getDir() { return dir; }
    public int getDamage() { return damage; }
    public boolean isInstant() { return instant; }

    @Override
    public String toString() {
        return "Inflicted with dir " + dir;
    }
}