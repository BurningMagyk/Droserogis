package Gameplay;

import javafx.scene.paint.Color;
import org.jbox2d.dynamics.*;

class Block extends Entity
{
    Block(World world, float xPos, float yPos, float width, float height)
    {
        super(world, xPos, yPos, width, height, false);
    }

    Color getColor()
    {
        return triggered ? Color.ORANGE : Color.YELLOW;
    }
}
