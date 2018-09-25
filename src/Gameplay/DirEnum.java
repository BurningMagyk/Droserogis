package Gameplay;

public enum DirEnum
{
    UP
            {
                public int getSign() { return -1; }
                public DirEnum getVert() { return this; }
            },
    DOWN
            {
                public int getSign() { return 1; }
                public DirEnum getVert() { return this; }
            },
    LEFT
            {
                public int getSign() { return -1; }
                public DirEnum getHoriz() { return this; }
            },
    RIGHT
            {
                public int getSign() { return 1; }
                public DirEnum getHoriz() { return this; }
            },
    UPLEFT
            {
                public DirEnum getHoriz() { return LEFT; }
                public DirEnum getVert() { return UP; }
            },
    UPRIGHT
            {
                public DirEnum getHoriz()  { return RIGHT; }
                public DirEnum getVert() { return UP; }
            },
    DOWNLEFT
            {
                public DirEnum getHoriz() { return LEFT; }
                public DirEnum getVert() { return DOWN; }
            },
    DOWNRIGHT
            {
                public DirEnum getHoriz() { return RIGHT; }
                public DirEnum getVert() { return DOWN; }
            },
    NONE;

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
        return NONE;
    }

    public int getSign() { return 0; }
    public DirEnum getHoriz() { return NONE; }
    public DirEnum getVert() { return NONE; }
}
