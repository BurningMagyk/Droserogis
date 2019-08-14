package Gameplay;

import Util.GradeEnum;
import Util.Vec2;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    private boolean isLiquid = false;
    private Color liquidColor = Color.rgb(150, 180, 230, 0.5);

    public Block(float xPos, float yPos, float width, float height,
                 ShapeEnum shape)
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


    //Added for use by LevelBuilder and, for now, only needed in Block, but this utility may be useful by other
    //   modules at the level of Entity.
    public boolean isInside(double x, double y) {
        if (getShape() == Entity.ShapeEnum.RECTANGLE)
        {
            Vec2 pos = getPosition();
            if (x < pos.x - getWidth() / 2) return false;
            if (x > pos.x + getWidth() / 2) return false;
            if (y < pos.y - getHeight() / 2) return false;
            if (y > pos.y + getHeight() / 2) return false;
            return true;
        }
        return false;
    }
}