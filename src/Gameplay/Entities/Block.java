/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import Gameplay.Entities.Weapons.Infliction;
import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    private float hazardRating;
    private Infliction.InflictionType[] infMaterials;
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


        //Print.purple("new Block: pos=("+xPos + ", " + yPos +")   size=("+width+", "+height+")  "+ type);
    }

    public boolean isLiquid() { return type.isWater; }

    public float getHazardRating() { return hazardRating; }
    public Infliction.InflictionType[] getInfMaterials() { return infMaterials; }

    @Override
    public Color getColor()
    {
        return touchBlock[RIGHT] != null ? Color.ORANGE : Color.YELLOW;

        //return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }

    public BlockType getBlockType() {return type;}
}