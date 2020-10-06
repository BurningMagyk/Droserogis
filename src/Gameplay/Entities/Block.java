/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import Gameplay.Entities.Weapons.Infliction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    public enum BlockMaterial
    {
       WATER, MOSS_ALL, MOSS_TOP, MOSS_TOP_LEFT, MOSS_TOP_RIGHT, MOSS_TOP_SIDES, NONE
    }

    private float hazardRating;
    private Infliction.InflictionType[] infMaterials;
    //private static final Color NEARBLACK = Color.rgb(10,4,3);
    private BlockMaterial blockMaterial = BlockMaterial.NONE;
    private BlockTexture[] blockTextureList;
    int gridWidth, gridHeight, textureCount;

    private static final int BLOCK_TEXTURE_PIXELS = 32;
    private static final float WORLD_TO_PIXEL = 100;

    public Block(float xPos, float yPos, float width, float height,
                 ShapeEnum shape, float hazardRating, Infliction.InflictionType[] infMaterials)
    {
        super(xPos, yPos, width, height, shape, null);

        this.blockMaterial = BlockMaterial.MOSS_ALL;
        this.hazardRating = hazardRating;
        if (infMaterials == null || infMaterials.length == 0)
        {
            this.infMaterials = new Infliction.InflictionType[]
                    { Infliction.InflictionType.EARTH };
        }
        else this.infMaterials = infMaterials;

        defineTextures();
    }


    public void setSize(float width, float height)
    {
        if (width == getWidth() && height == getHeight()) return;
        super.setSize(width, height);
        defineTextures();
    }

    public void defineTextures()
    {
        if (blockMaterial == BlockMaterial.NONE)
        {
            textureCount = 0;
            return;
        }
        if (getShape() == ShapeEnum.RECTANGLE)
        {
            gridWidth = Math.round(getWidth() * WORLD_TO_PIXEL / BLOCK_TEXTURE_PIXELS);
            gridHeight = Math.round(getHeight() * WORLD_TO_PIXEL / BLOCK_TEXTURE_PIXELS);
            if (blockMaterial == BlockMaterial.MOSS_ALL)
            {
                textureCount = 2 * (gridWidth) + 2 * (gridHeight - 2);
            }
            else if  (blockMaterial == BlockMaterial.MOSS_TOP)
            {
                textureCount = gridWidth;
            }
            blockTextureList = new BlockTexture[textureCount];
            for (int i = 0; i < textureCount; i++)
            {
                blockTextureList[i] = BlockTexture.getRandomEdgeTexture(getEdgeType(i));
            }
        }
        else if (getShape() == ShapeEnum.RAMP_RIGHT18)
        {
            gridWidth = Math.round(getWidth()*WORLD_TO_PIXEL/(3*BLOCK_TEXTURE_PIXELS));
            gridHeight = 1;
            textureCount = gridWidth;
            blockTextureList = new BlockTexture[textureCount];
            for(int i=0; i<textureCount; i++)
            {
                blockTextureList[i] = BlockTexture.getRandomEdgeTexture(getEdgeType(i));
            }
        }
    }

    public BlockTexture.EdgeType getEdgeType(int edgeIndex)
    {
        if (getShape() == ShapeEnum.RECTANGLE)
        {
            if (edgeIndex == 0)
            {
                if (blockMaterial == BlockMaterial.MOSS_TOP) return BlockTexture.EdgeType.END_TOP_LEFT;
                return BlockTexture.EdgeType.TOP_LEFT;
            }
            if (edgeIndex == gridWidth - 1)
            {
                if (blockMaterial == BlockMaterial.MOSS_TOP) return BlockTexture.EdgeType.END_TOP_RIGHT;
                return BlockTexture.EdgeType.TOP_RIGHT;
            }
            if (edgeIndex == gridWidth) return BlockTexture.EdgeType.BOT_LEFT;
            if (edgeIndex == gridWidth * 2 - 1) return BlockTexture.EdgeType.BOT_RIGHT;
            int startTop = 1;
            int startBot = gridWidth + 1;
            int startLeft = gridWidth * 2;
            int startRight = startLeft + gridHeight - 2;

            if ((edgeIndex >= startTop) && (edgeIndex < startBot)) return BlockTexture.EdgeType.TOP;
            if ((edgeIndex >= startBot) && (edgeIndex < startLeft)) return BlockTexture.EdgeType.BOT;
            if ((edgeIndex >= startLeft) && (edgeIndex < startRight)) return BlockTexture.EdgeType.LEFT;
            return BlockTexture.EdgeType.RIGHT;
        }
        else if (getShape() == ShapeEnum.RAMP_RIGHT18)
        {
            return BlockTexture.EdgeType.RAMP_RIGHT18;
        }
        return null;
    }


    public int getEdgeX(int edgeIndex)
    {
        if (this.getShape() == ShapeEnum.RECTANGLE)
        {
            int startBot = gridWidth;
            int startLeft = gridWidth * 2;
            int startRight = startLeft + gridHeight - 2;

            if (edgeIndex < startBot) return edgeIndex * BLOCK_TEXTURE_PIXELS;
            if (edgeIndex < startLeft) return (edgeIndex - gridWidth) * BLOCK_TEXTURE_PIXELS;
            if (edgeIndex < startRight) return 0;
            return BLOCK_TEXTURE_PIXELS * (gridWidth - 1);
        }
        if (this.getShape() == ShapeEnum.RAMP_RIGHT18)
        {
            return edgeIndex * 3 * BLOCK_TEXTURE_PIXELS;
        }
        return 0;
    }


    public int getEdgeY(int edgeIndex)
    {
        if (this.getShape() == ShapeEnum.RECTANGLE)
        {
            int startBot = gridWidth;
            int startLeft = gridWidth * 2;
            int startRight = startLeft + gridHeight - 2;

            if (edgeIndex < startBot) return 0;
            if (edgeIndex < startLeft) return BLOCK_TEXTURE_PIXELS*(gridHeight-1);
            if (edgeIndex < startRight)  return (1+edgeIndex-startLeft)*BLOCK_TEXTURE_PIXELS;
            return (1+edgeIndex-startRight)*BLOCK_TEXTURE_PIXELS;
        }
        if (this.getShape() == ShapeEnum.RAMP_RIGHT18)
        {
            return edgeIndex * BLOCK_TEXTURE_PIXELS;
        }
        return 0;
    }

    public void setTextureType(BlockMaterial type)
    {
        this.blockMaterial = type;
        defineTextures();
    }
    public BlockMaterial getTextureType()
    {
        return blockMaterial;
    }
    public boolean isLiquid() { return blockMaterial == BlockMaterial.WATER; }

    public float getHazardRating() { return hazardRating; }
    public Infliction.InflictionType[] getInfMaterials() { return infMaterials; }

    @Override
    public Color getColor()
    {
        return touchBlock[RIGHT] != null ? Color.ORANGE : Color.YELLOW;

        //return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }

    @Override
    public void render(GraphicsContext gfx, float camPosX, float camPosY, float camOffX, float camOffY, float camZoom)
    {
        if (this.getShape().isRamp())
        {
            double[] xPos = new double[3];
            double[] yPos = new double[3];
            for (int i = 0; i < 3; i++)
            {
                xPos[i] = (this.getVertexX(i) - camPosX + camOffX) * camZoom;
                yPos[i] = (this.getVertexY(i) - camPosY + camOffY) * camZoom;
            }
            gfx.fillPolygon(xPos, yPos, 3);
            if (this.getShape() == ShapeEnum.RAMP_RIGHT18)
            {
                for (int i = 0; i < textureCount; i++)
                {
                    BlockTexture subtype = blockTextureList[i];
                    double xx = xPos[0] + getEdgeX(i) - subtype.left;
                    double yy = yPos[0] + getEdgeY(i) - subtype.top;
                    gfx.drawImage(subtype.getImage(), xx, yy);
                }
            }
        }
        else if (this.getShape() == ShapeEnum.RECTANGLE)
        {
            double x = (this.getX() - this.getWidth() / 2 - camPosX + camOffX) * camZoom;
            double y = (this.getY() - this.getHeight() / 2 - camPosY + camOffY) * camZoom;
            double width = this.getWidth() * camZoom;
            double height = this.getHeight() * camZoom;
            if (this.isLiquid()) return;
            gfx.setFill(Color.BLACK);
            gfx.fillRect(x, y, width, height);
            for (int i = 0; i < textureCount; i++)
            {
                BlockTexture subtype = blockTextureList[i];
                if (subtype != null)
                {
                    double xx = x + getEdgeX(i) - subtype.left;
                    double yy = y + getEdgeY(i) - subtype.top;
                    gfx.drawImage(subtype.getImage(), xx, yy);
                }
            }
        }
    }
}