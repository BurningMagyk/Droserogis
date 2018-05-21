package Gameplay;

import javafx.scene.paint.Color;
import org.jbox2d.dynamics.*;

class Block extends Entity
{
    Block(World world, float xPos, float yPos, float width, float height, TriangleOrient triOri)
    {
        super(world, xPos, yPos, width, height, triOri, false);
    }

    Color getColor()
    {
        return triggered ? Color.ORANGE : Color.YELLOW;
    }
}

enum TriangleOrient
{
    UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
}