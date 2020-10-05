/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import Gameplay.Entities.Weapons.Infliction;
import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Block extends Entity
{
    public enum BlockEnum
    {
       WATER, MOSS_ALL, MOSS_TOP, MOSS_TOPLEFT, MOSS_TOPRIGHT, MOSS_TOPSIDES, NONE
    }


    private float hazardRating;
    private Infliction.InflictionType[] infMaterials;
    private static final Color NEARBLACK = Color.rgb(10,4,3);
    private BlockEnum blockEnum = BlockEnum.NONE;
    private BlockTexture[] blockTextureList;
    int gridWidth, gridHeight, textureCount;

    private static final int BLOCK_TEXTURE_PIXELS = 32;
    private static final float WORLD_TO_PIXEL = 100;

    public Block(float xPos, float yPos, float width, float height,
                 ShapeEnum shape, float hazardRating, Infliction.InflictionType[] infMaterials)
    {
        super(xPos, yPos, width, height, shape, null);

        this.blockEnum = BlockEnum.MOSS_ALL;
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
        if (blockEnum == BlockEnum.NONE) return;
        gridWidth = Math.round(getWidth()*WORLD_TO_PIXEL/BLOCK_TEXTURE_PIXELS);
        gridHeight = Math.round(getHeight()*WORLD_TO_PIXEL/BLOCK_TEXTURE_PIXELS);
        textureCount = 2*(gridWidth)+ 2*(gridHeight-2);

        if (getShape() == ShapeEnum.RECTANGLE)
        {
            blockTextureList = new BlockTexture[textureCount];
            for(int i=0; i<textureCount; i++)
            {
                blockTextureList[i] = BlockTexture.getRandomEdgeTexture(getEdgeType(i));
            }
        }
    }

    public BlockTexture.EdgeType getEdgeType(int edgeIndex)
    {
        if (edgeIndex == 0) return BlockTexture.EdgeType.TOPLEFT;
        if (edgeIndex == gridWidth-1) return BlockTexture.EdgeType.TOPRIGHT;
        if (edgeIndex == gridWidth) return BlockTexture.EdgeType.BOTLEFT;
        if (edgeIndex == gridWidth*2-1) return BlockTexture.EdgeType.BOTRIGHT;
        int startTop = 1;
        int startBot = gridWidth + 1;
        int startLeft = gridWidth * 2;
        int startRight = startLeft + gridHeight - 2;

        if ((edgeIndex >= startTop) && (edgeIndex < startBot))  return BlockTexture.EdgeType.TOP;
        if ((edgeIndex >= startBot) && (edgeIndex < startLeft))  return BlockTexture.EdgeType.BOT;
        if ((edgeIndex >= startLeft) && (edgeIndex < startRight))  return BlockTexture.EdgeType.LEFT;
        return BlockTexture.EdgeType.RIGHT;
    }


    public int getEdgeX(int edgeIndex)
    {
        int startBot = gridWidth;
        int startLeft = gridWidth * 2;
        int startRight = startLeft + gridHeight - 2;

        if (edgeIndex < startBot) return edgeIndex*BLOCK_TEXTURE_PIXELS;
        if (edgeIndex < startLeft) return (edgeIndex-gridWidth)*BLOCK_TEXTURE_PIXELS;
        if (edgeIndex < startRight)  return 0;
        return BLOCK_TEXTURE_PIXELS*(gridWidth-1);
    }


    public int getEdgeY(int edgeIndex)
    {
        int startBot = gridWidth;
        int startLeft = gridWidth * 2;
        int startRight = startLeft + gridHeight - 2;

        if (edgeIndex < startBot) return 0;
        if (edgeIndex < startLeft) return BLOCK_TEXTURE_PIXELS*(gridHeight-1);
        if (edgeIndex < startRight)  return (1+edgeIndex-startLeft)*BLOCK_TEXTURE_PIXELS;
        return (1+edgeIndex-startRight)*BLOCK_TEXTURE_PIXELS;
    }

    public void setTextureType(BlockEnum type)
    {
        this.blockEnum = type;
    }
    public boolean isLiquid() { return blockEnum == BlockEnum.WATER; }

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
        if (this.getShape().isTriangle())
        {
            double[] xPos = new double[3];
            double[] yPos = new double[3];
            for (int i = 0; i < 3; i++)
            {
                xPos[i] = (this.getVertexX(i) - camPosX + camOffX) * camZoom;
                yPos[i] = (this.getVertexY(i) - camPosY + camOffY) * camZoom;
            }
            //gfx.setFill(texturePatternBlock);
            gfx.fillPolygon(xPos, yPos, 3);
        }
        else if (this.getShape() == ShapeEnum.RECTANGLE)
        {
            double x = (this.getX() - this.getWidth() / 2 - camPosX + camOffX) * camZoom;
            double y = (this.getY() - this.getHeight() / 2 - camPosY + camOffY) * camZoom;
            double width = this.getWidth() * camZoom;
            double height = this.getHeight() * camZoom;
            if (this.isLiquid()) return;
            //BlockTexture type = this.getBlockType();
            //if (type.name.equals("RECTANGLE"))// || Main.debugEnum == DebugEnum.GAMEPLAY)
            //{
                gfx.setFill(NEARBLACK);
                //if (Main.debugEnum == DebugEnum.GAMEPLAY) gfx.setFill(entity.getColor());
                gfx.fillRect(x, y, width, height);
                for (int i=0; i<textureCount; i++)
                {
                    BlockTexture subtype = blockTextureList[i];
                    double xx = x + getEdgeX(i) - subtype.left;
                    double yy = y + getEdgeY(i) - subtype.top;
                    gfx.drawImage(subtype.getImage(), xx, yy);
                }
            //}
            //else
            //{
            //    x -= type.left;
            //    y -= type.top;
            //    gfx.drawImage(this.getBlockType().getImage(), x, y);
            //}
        }
    }

    //public BlockTexture getBlockType() {return type;}
}