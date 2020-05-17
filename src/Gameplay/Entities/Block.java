/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import Gameplay.Entities.Weapons.Infliction;
import Gameplay.Entities.Weapons.Weapon;
import Importer.ImageResource;
import Util.Print;
import Util.Vec2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    private float hazardRating;
    private Infliction.InflictionType[] infMaterials;
    private BlockType type;
    private static final Color NEARBLACK = Color.rgb(10,4,3);

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

    @Override
    public void render(GraphicsContext gfx, float camPosX, float camPosY, float camOffX, float camOffY, float camZoom) {
        if (this.getShape().isTriangle()) {
            double[] xPos = new double[3];
            double[] yPos = new double[3];
            for (int i = 0; i < 3; i++)
            {
                xPos[i] = (this.getVertexX(i) - camPosX + camOffX) * camZoom;
                yPos[i] = (this.getVertexY(i) - camPosY + camOffY) * camZoom;
            }
            //gfx.setFill(texturePatternBlock);
            gfx.fillPolygon(xPos, yPos, 3);
        } else if(this.getShape() == ShapeEnum.RECTANGLE) {
            double x = (this.getX() - this.getWidth() / 2 - camPosX + camOffX) * camZoom;
            double y = (this.getY() - this.getHeight() / 2 - camPosY + camOffY) * camZoom;
            double width = this.getWidth() * camZoom;
            double height = this.getHeight() * camZoom;
            if (this.isLiquid()) return;
            BlockType type = this.getBlockType();
            if (type.name.equals("RECTANGLE"))// || Main.debugEnum == DebugEnum.GAMEPLAY)
            {
                gfx.setFill(NEARBLACK);
                //if (Main.debugEnum == DebugEnum.GAMEPLAY) gfx.setFill(entity.getColor());
                gfx.fillRect(x, y, width, height);
            }
            else
            {
                x-=type.left;
                y-=type.top;
                gfx.drawImage(this.getBlockType().getImage(), x, y);
            }
        }
    }

    public BlockType getBlockType() {return type;}
}