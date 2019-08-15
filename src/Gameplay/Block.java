package Gameplay;

import Util.GradeEnum;
import Util.Vec2;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    private boolean isLiquid = false;
    private Color liquidColor = Color.rgb(150, 180, 230, 0.5);

    public Block(float xPos, float yPos, float width, float height,
                 ShapeEnum shape, String[] spritePaths)
    {
        super(xPos, yPos, width, height, shape, spritePaths);
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


    //====================================================================================================
    // Added for use by levelBuilder.
    // This is in Block (not Entity) since, for now at least, only Block needs it and Blocks can only
    // be two convex shapes: axis aligned rectangles and axis aligned right-triangles. These are easy shapes
    // to calculate contains.
    // This method must be fast as it is called on mouse move events.
    //====================================================================================================
    public boolean isInside(double x, double y) {
        Vec2 pos = getPosition();
        if (x < pos.x - getWidth() / 2) return false;
        if (x > pos.x + getWidth() / 2) return false;
        if (y < pos.y - getHeight() / 2) return false;
        if (y > pos.y + getHeight() / 2) return false;
        if (getShape() == Entity.ShapeEnum.RECTANGLE)
        {
            return true;
        }
        if (getShape() == ShapeEnum.TRIANGLE_UP_R)
        {
            if (x > pos.x) return false;
            if (y < pos.y) return false;
            return true;
        }
        if (getShape() == ShapeEnum.TRIANGLE_UP_L)
        {
            if (x < pos.x) return false;
            if (y < pos.y) return false;
            return true;
        }
        if (getShape() == ShapeEnum.TRIANGLE_DW_R)
        {
            if (x > pos.x) return false;
            if (y > pos.y) return false;
            return true;
        }
        if (getShape() == ShapeEnum.TRIANGLE_DW_L)
        {
            if (x < pos.x) return false;
            if (y > pos.y) return false;
            return true;
        }
        return false;
    }
}