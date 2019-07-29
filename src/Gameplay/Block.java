package Gameplay;

import Util.GradeEnum;
import javafx.scene.paint.Color;


class Block extends Entity
{
    private boolean isLiquid = false;
    private Color liquidColor = Color.rgb(150, 180, 230, 0.5);

    public Block(float xPos, float yPos, float width, float height, ShapeEnum shape)
    {
        super(xPos, yPos, width, height, shape);
    }

    public void setLiquid(boolean liquid) { isLiquid = liquid; }
    boolean isLiquid() { return isLiquid; }

    @Override
    public Color getColor()
    {
        if (isLiquid) return liquidColor;
        return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }

    @Override
    public void damage(GradeEnum gradeEnum) {}
}