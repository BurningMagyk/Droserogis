package Game;

public class Direction
{
    Enum direction[];

    Direction(Enum direction)
    {
        this(direction, direction);
    }

    Direction(Enum dir_1, Enum dir_2)
    {
        this.direction = new Enum[2];
        this.direction[0] = dir_1;
        this.direction[1] = dir_2;
    }

    public enum Enum
    {
        UP, LEFT, DOWN, RIGHT
    }
}
