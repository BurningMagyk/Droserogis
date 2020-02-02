package Gameplay;

import Gameplay.Weapons.Infliction;
import Util.GradeEnum;
import Util.Vec2;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    private Infliction.InflictionType[] infMaterials;
    private boolean isLiquid = false;
    private Color liquidColor = Color.rgb(150, 180, 230, 0.5);

    public Block(float xPos, float yPos, float width, float height,
                 ShapeEnum shape, Infliction.InflictionType[] infMaterials,
                 String[] spritePaths)
    {
        super(xPos, yPos, width, height, shape, spritePaths);

        if (infMaterials == null || infMaterials.length == 0)
        {
            this.infMaterials = new Infliction.InflictionType[]
                    { Infliction.InflictionType.EARTH };
        }
        else this.infMaterials = infMaterials;
    }

    public void setLiquid(boolean liquid) { isLiquid = liquid; }

    public boolean isLiquid() { return isLiquid; }

    public Infliction.InflictionType[] getInfMaterials() { return infMaterials; }

    @Override
    public Color getColor()
    {
        if (isLiquid) return liquidColor;
        return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }
}