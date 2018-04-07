package Gameplay;

public class Direction
{
    Enum horiz;
    Enum vert;

    Direction()
    {
        horiz = Enum.NONE;
        vert = Enum.NONE;
    }

    Direction(Enum direction)
    {
        if (direction == Enum.UP || direction == Enum.DOWN)
        {
            horiz = Enum.NONE;
            vert = direction;
        }
        else
        {
            horiz = direction;
            vert = Enum.NONE;
        }
    }

    Direction(Enum horiz, Enum vert)
    {
        this.horiz = horiz;
        this.vert = vert;
    }

    boolean opposes(Direction direction)
    {
        if (horiz.opposite() == direction.horiz
                && horiz != Enum.NONE
                && direction.horiz != Enum.NONE) return true;
        if (vert.opposite() == direction.vert
                && vert != Enum.NONE
                && direction.vert != Enum.NONE) return true;

        return false;
    }

    public enum Enum
    {
        UP { Enum opposite() { return DOWN; } },
        LEFT { Enum opposite() { return RIGHT; } },
        DOWN { Enum opposite() { return UP; } },
        RIGHT { Enum opposite() { return LEFT; } },
        NONE;

        Enum opposite() { return NONE; }
    }
}
