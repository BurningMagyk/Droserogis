package Gameplay;

public enum DirEnum
{
    UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT;

    static public DirEnum get(int horiz, int vert)
    {
        if (vert == Entity.UP)
        {
            if (horiz == Entity.LEFT) return UPLEFT;
            if (horiz == Entity.RIGHT) return UPRIGHT;
            else return UP;
        }
        if (vert == Entity.DOWN)
        {
            if (horiz == Entity.LEFT) return DOWNLEFT;
            if (horiz == Entity.RIGHT) return DOWNRIGHT;
            else return DOWN;
        }
        if (horiz == Entity.LEFT) return LEFT;
        if (horiz == Entity.RIGHT) return RIGHT;
        return null;
    }
}
