package Gameplay;

import Gameplay.Weapons.Infliction;
import Util.GradeEnum;
import Util.Vec2;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    private float hazardRating;
    private Infliction.InflictionType[] infMaterials;
    private boolean isLiquid = false;
    private BlockType type;

    public Block(float xPos, float yPos, float width, float height,
                 BlockType type, float hazardRating, Infliction.InflictionType[] infMaterials)
    {
        super(xPos, yPos, width, height, type.shape, null);

        this.type = type;
        this.hazardRating = hazardRating;
        if (infMaterials == null || infMaterials.length == 0)
        {
            this.infMaterials = new Infliction.InflictionType[]
                    { Infliction.InflictionType.EARTH };
        }
        else this.infMaterials = infMaterials;
    }

    public void setLiquid(boolean liquid) { isLiquid = liquid; }

    public boolean isLiquid() { return isLiquid; }

    public float getHazardRating() { return hazardRating; }
    public Infliction.InflictionType[] getInfMaterials() { return infMaterials; }

    @Override
    public Color getColor()
    {
        return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }

    public BlockType getBlockType() {return type;}
}