/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import Gameplay.Entities.Weapons.Infliction;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class Block extends Entity
{
    public enum BlockMaterial
    {
        WATER, MOSS, EDGE_TOP, EDGE_RIGHT, EDGE_BOTTOM, EDGE_LEFT, CORNER_LEFT, CORNER_RIGHT
    }
    public static final int EDGE_TOP = 1;
    public static final int EDGE_RIGHT = 2;
    public static final int EDGE_BOT = 4;
    public static final int EDGE_LEFT = 8;
    public static final int CORNER_LEFT = 16;
    public static final int CORNER_RIGHT = 32;
    private int edgeBits = 15;

    private class TextureData
    {
        public int left;
        public int top;
        public Image image;

        public TextureData(BlockTexture.EdgeType edgeType, int idx, int gridWidth, int gridHeight)
        {
            BlockTexture texture = BlockTexture.getRandomEdgeTexture(edgeType);
            this.image = texture.getImage();
            this.left = getEdgeX(idx, gridWidth, gridHeight) - texture.left;
            this.top = getEdgeY(idx, gridWidth, gridHeight) - texture.top;
        }
    }
    private ArrayList<TextureData> textureList = new ArrayList<>();

    private float hazardRating;
    private Infliction.InflictionType[] infMaterials;
    private BlockMaterial blockMaterial = null;
    //private BlockTexture[] blockTextureList;
    //int gridWidth, gridHeight, textureCount;

    private static final int BLOCK_TEXTURE_PIXELS = 32;
    private static final float WORLD_TO_PIXEL = 100;

    public Block(float xPos, float yPos, float width, float height,
                 ShapeEnum shape, float hazardRating, Infliction.InflictionType[] infMaterials)
    {
        super(xPos, yPos, width, height, shape, null);

        this.blockMaterial = BlockMaterial.MOSS;
        this.hazardRating = hazardRating;
        if (infMaterials == null || infMaterials.length == 0)
        {
            this.infMaterials = new Infliction.InflictionType[]
                    { Infliction.InflictionType.EARTH };
        }
        else this.infMaterials = infMaterials;

        if (shape.isRamp()) edgeBits = 1;

        defineTextures();
    }


    public void setSize(float width, float height)
    {
        if (width == getWidth() && height == getHeight()) return;
        super.setSize(width, height);
        if (blockMaterial != null) defineTextures();
    }

    public void defineTextures()
    {
        textureList.clear();
        if (edgeBits == 0) return;

        if (getShape() == ShapeEnum.RECTANGLE)
        {
            int gridWidth = Math.round(getWidth() * WORLD_TO_PIXEL / BLOCK_TEXTURE_PIXELS);
            int gridHeight = Math.round(getHeight() * WORLD_TO_PIXEL / BLOCK_TEXTURE_PIXELS);
            int endTop = gridWidth;
            int endBot = gridWidth*2;
            int endLeft = gridWidth*2 + gridHeight - 2;
            int endRight = gridWidth*2 + (gridHeight - 2)*2;

            if ((edgeBits & CORNER_LEFT) > 0)
            {
                BlockTexture.EdgeType edgeType = BlockTexture.EdgeType.CORNER_LEFT;
                textureList.add( new TextureData(edgeType, 0, gridWidth, gridHeight));
                return;
            }
            if ((edgeBits & CORNER_RIGHT) > 0)
            {
                BlockTexture.EdgeType edgeType = BlockTexture.EdgeType.CORNER_RIGHT;
                textureList.add( new TextureData(edgeType, endTop-1, gridWidth, gridHeight));
                return;
            }

            if ((edgeBits & EDGE_TOP) > 0)
            {
                for (int i = 0; i < endTop; i++)
                {
                    BlockTexture.EdgeType edgeType = getEdgeType(i, gridWidth, gridHeight);
                    textureList.add( new TextureData(edgeType, i, gridWidth, gridHeight));
                }
            }
            if ((edgeBits & EDGE_BOT) > 0)
            {
                for (int i = endTop; i < endBot; i++)
                {
                    BlockTexture.EdgeType edgeType = getEdgeType(i, gridWidth, gridHeight);
                    textureList.add( new TextureData(edgeType, i, gridWidth, gridHeight));
                }
            }
            if ((edgeBits & EDGE_LEFT) > 0)
            {
                BlockTexture.EdgeType edgeType = BlockTexture.EdgeType.LEFT;
                if ((edgeBits & EDGE_TOP) == 0)
                {
                    textureList.add( new TextureData(edgeType, 0, gridWidth, gridHeight));
                }
                if ((edgeBits & EDGE_BOT) == 0)
                {
                    textureList.add( new TextureData(edgeType, endTop, gridWidth, gridHeight));
                }
                for (int i = endBot; i < endLeft; i++)
                {
                    textureList.add( new TextureData(edgeType, i, gridWidth, gridHeight));
                }
            }
            if ((edgeBits & EDGE_RIGHT) > 0)
            {
                BlockTexture.EdgeType edgeType = BlockTexture.EdgeType.RIGHT;
                if ((edgeBits & EDGE_TOP) == 0)
                {
                    textureList.add( new TextureData(edgeType, endTop-1, gridWidth, gridHeight));
                }
                if ((edgeBits & EDGE_BOT) == 0)
                {
                    textureList.add( new TextureData(edgeType, endBot-1, gridWidth, gridHeight));
                }
                for (int i = endLeft; i < endRight; i++)
                {
                    textureList.add( new TextureData(edgeType, i, gridWidth, gridHeight));
                }
            }
        }
        else if (getShape() == ShapeEnum.RAMP_RIGHT18)
        {
            int gridWidth = Math.round(getWidth()*WORLD_TO_PIXEL/(3*BLOCK_TEXTURE_PIXELS));
            int textureCount = gridWidth;
            BlockTexture.EdgeType edgeType = BlockTexture.EdgeType.RAMP_RIGHT18;
            for(int i=0; i<textureCount; i++)
            {
                textureList.add( new TextureData(edgeType, i, gridWidth, 1));
            }
        }
        else if (getShape() == ShapeEnum.RAMP_LEFT18)
        {
            int gridWidth = Math.round(getWidth()*WORLD_TO_PIXEL/(3*BLOCK_TEXTURE_PIXELS));
            int textureCount = gridWidth;
            BlockTexture.EdgeType edgeType = BlockTexture.EdgeType.RAMP_LEFT18;
            for(int i=0; i<textureCount; i++)
            {
                textureList.add( new TextureData(edgeType, i, gridWidth, 1));
            }
        }
    }

    public BlockTexture.EdgeType getEdgeType(int edgeIndex, int gridWidth, int gridHeight)
    {
        if (getShape() == ShapeEnum.RECTANGLE)
        {
            if (edgeIndex == 0)
            {
                if ((edgeBits & EDGE_LEFT) == 0) return BlockTexture.EdgeType.TOP;
                return BlockTexture.EdgeType.TOP_LEFT;
            }
            if (edgeIndex == gridWidth - 1)
            {
                if ((edgeBits & EDGE_RIGHT) == 0) return BlockTexture.EdgeType.TOP;
                return BlockTexture.EdgeType.TOP_RIGHT;
            }
            if (edgeIndex == gridWidth)
            {
                if ((edgeBits & EDGE_LEFT) == 0) return BlockTexture.EdgeType.BOT;
                return BlockTexture.EdgeType.BOT_LEFT;
            }
            if (edgeIndex == gridWidth * 2 - 1)
            {   if ((edgeBits & EDGE_RIGHT) == 0) return BlockTexture.EdgeType.BOT;
                return BlockTexture.EdgeType.BOT_RIGHT;
            }
            int startTop = 1;
            int startBot = gridWidth + 1;
            int startLeft = gridWidth * 2;
            int startRight = startLeft + gridHeight - 2;

            if ((edgeIndex >= startTop) && (edgeIndex < startBot)) return BlockTexture.EdgeType.TOP;
            if ((edgeIndex >= startBot) && (edgeIndex < startLeft)) return BlockTexture.EdgeType.BOT;
            if ((edgeIndex >= startLeft) && (edgeIndex < startRight)) return BlockTexture.EdgeType.LEFT;
            return BlockTexture.EdgeType.RIGHT;
        }
        return null;
    }


    public int getEdgeX(int edgeIndex, int gridWidth, int gridHeight)
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
        if (getShape() == ShapeEnum.RAMP_RIGHT18 || getShape() == ShapeEnum.RAMP_LEFT18)
        {
            return edgeIndex * 3 * BLOCK_TEXTURE_PIXELS;
        }
        return 0;
    }


    public int getEdgeY(int edgeIndex, int gridWidth, int gridHeight)
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
        if (this.getShape() == ShapeEnum.RAMP_LEFT18)
        {
            return -(1+edgeIndex) * BLOCK_TEXTURE_PIXELS;
        }
        return 0;
    }

    public void setTextureType(BlockMaterial type)
    {
        if (type == BlockMaterial.WATER)
        {
            if (blockMaterial == BlockMaterial.WATER) blockMaterial = BlockMaterial.MOSS;
            else blockMaterial = BlockMaterial.WATER;
        }
        else if (type == BlockMaterial.EDGE_TOP) edgeBits = edgeBits ^ EDGE_TOP;
        else if (type == BlockMaterial.EDGE_RIGHT) edgeBits = edgeBits ^ EDGE_RIGHT;
        else if (type == BlockMaterial.EDGE_BOTTOM) edgeBits = edgeBits ^ EDGE_BOT;
        else if (type == BlockMaterial.EDGE_LEFT) edgeBits = edgeBits ^ EDGE_LEFT;
        else if (type == BlockMaterial.CORNER_LEFT) edgeBits = CORNER_LEFT;
        else if (type == BlockMaterial.CORNER_RIGHT) edgeBits = CORNER_RIGHT;

        defineTextures();
    }

    public void setTextureType(int bits)
    {
        edgeBits = bits;
        defineTextures();
    }

    public int getEdgeBits() {return edgeBits;}

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
            gfx.setFill(Color.BLACK);
            gfx.fillPolygon(xPos, yPos, 3);
            for (TextureData data : textureList)
            {
                gfx.drawImage(data.image, xPos[0] + data.left, yPos[0] + data.top);
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
            for (TextureData data : textureList)
            {
                gfx.drawImage(data.image, x + data.left, y + data.top);
            }
        }
    }
}