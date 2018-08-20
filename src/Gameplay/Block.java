package Gameplay;

import javafx.scene.paint.Color;


class Block extends Entity
{
    public Block(float xPos, float yPos, float width, float height, ShapeEnum shape)
    {
        super(xPos, yPos, width, height, shape);
    }

    @Override
    public Color getColor()
    {
        return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }
}