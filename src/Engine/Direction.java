package Engine;

public enum Direction
{
    UP { Direction opposite() { return DOWN; } },
    LEFT { Direction opposite() { return RIGHT; } },
    DOWN { Direction opposite() { return UP; } },
    RIGHT { Direction opposite() { return LEFT; } },
    NONE;

    Direction opposite() { return NONE; }
}
