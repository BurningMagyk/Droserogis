package Gameplay;

import Util.Print;

public enum DirEnum
{
    UP
            {
                public int getSign() { return -1; }
                public DirEnum getVert() { return this; }
                public DirEnum add(DirEnum other) {
                    if (other.getVert() == DOWN) return other.getHoriz();
                    if (other.getHoriz() == LEFT) return UPLEFT;
                    if (other.getHoriz() == RIGHT) return UPRIGHT;
                    return UP; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return _this.getY() > other.getY(); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    if (getCollisionPos(_this, other))
                    {
                        if (_this.getVelocityY() < other.getVelocityY())
                            return other.getVelocityY() - _this.getVelocityY();
                    }
                    return 0;
                }
            },
    DOWN
            {
                public int getSign() { return 1; }
                public DirEnum getVert() { return this; }
                public DirEnum add(DirEnum other) {
                    if (other.getVert() == UP) return other.getHoriz();
                    if (other.getHoriz() == LEFT) return DOWNLEFT;
                    if (other.getHoriz() == RIGHT) return DOWNRIGHT;
                    return DOWN; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return _this.getY() < other.getY(); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    if (getCollisionPos(_this, other))
                    {
                        if (_this.getVelocityY() > other.getVelocityY())
                            return _this.getVelocityY() - other.getVelocityY();
                    }
                    return 0;
                }
            },
    LEFT
            {
                public int getSign() { return -1; }
                public DirEnum getHoriz() { return this; }
                public DirEnum add(DirEnum other) {
                    if (other.getHoriz() == RIGHT) return other.getVert();
                    if (other.getVert() == UP) return UPLEFT;
                    if (other.getVert() == DOWN) return DOWNLEFT;
                    return LEFT; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return _this.getX() > other.getX(); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    if (getCollisionPos(_this, other))
                    {
                        if (_this.getVelocityX() < other.getVelocityX())
                            return other.getVelocityX() - _this.getVelocityX();
                    }
                    return 0;
                }
            },
    RIGHT
            {
                public int getSign() { return 1; }
                public DirEnum getHoriz() { return this; }
                public DirEnum add(DirEnum other) {
                    if (other.getHoriz() == LEFT) return other.getVert();
                    if (other.getVert() == UP) return UPRIGHT;
                    if (other.getVert() == DOWN) return DOWNRIGHT;
                    return RIGHT; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return _this.getX() < other.getX(); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    if (getCollisionPos(_this, other))
                    {
                        if (_this.getVelocityX() > other.getVelocityX())
                            return _this.getVelocityX() - other.getVelocityX();
                    }
                    return 0;
                }
            },
    UPLEFT
            {
                public DirEnum getHoriz() { return LEFT; }
                public DirEnum getVert() { return UP; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return getHoriz().getCollisionPos(_this, other)
                            || getVert().getCollisionPos(_this, other); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    return getHoriz().getCollisionSpeed(_this, other)
                            + getVert().getCollisionSpeed(_this, other);
                }
            },
    UPRIGHT
            {
                public DirEnum getHoriz()  { return RIGHT; }
                public DirEnum getVert() { return UP; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return getHoriz().getCollisionPos(_this, other)
                            || getVert().getCollisionPos(_this, other); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    return getHoriz().getCollisionSpeed(_this, other)
                            + getVert().getCollisionSpeed(_this, other);
                }
            },
    DOWNLEFT
            {
                public DirEnum getHoriz() { return LEFT; }
                public DirEnum getVert() { return DOWN; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return getHoriz().getCollisionPos(_this, other)
                            || getVert().getCollisionPos(_this, other); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    return getHoriz().getCollisionSpeed(_this, other)
                            + getVert().getCollisionSpeed(_this, other);
                }
            },
    DOWNRIGHT
            {
                public DirEnum getHoriz() { return RIGHT; }
                public DirEnum getVert() { return DOWN; }
                public boolean getCollisionPos(Item _this, Item other) {
                    return getHoriz().getCollisionPos(_this, other)
                            || getVert().getCollisionPos(_this, other); }
                public float getCollisionSpeed(Item _this, Item other)
                {
                    return getHoriz().getCollisionSpeed(_this, other)
                            + getVert().getCollisionSpeed(_this, other);
                }
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
    public DirEnum add(DirEnum other) { return NONE; }

    public boolean getCollisionPos(Item _this, Item other) { return false; }
    public float getCollisionSpeed(Item _this, Item other) { return 0; }
}
