package Gameplay.Entities;

import Importer.ImageResource;
import Menus.Main;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;


public class BlockTexture
{
    public final String name;
    public final float pixelHitWidth, pixelHitHeight;
    public final int left;
    public final int top;
    public final Entity.ShapeEnum shape;
    public final boolean isResizeable;
    public final boolean isWater;
    //public Image image;
    private ImageResource image;

    public static BlockTexture[] blockTextureList;

    public enum EdgeType
    {
        TOP, BOT, LEFT, RIGHT, TOPLEFT, TOPRIGHT, BOTLEFT, BOTRIGHT
    }
    public static ArrayList<BlockTexture> edgeTextureList[] = new ArrayList[EdgeType.values().length];
    private static Random random = new Random();

    public BlockTexture(String fileName, int pixelHitWidth, int pixelHitHeight, int left, int top, EdgeType edgeType)
    {
        this.pixelHitWidth = pixelHitWidth;
        this.pixelHitHeight = pixelHitHeight;
        this.left = left;
        this.top = top;
        this.shape = Entity.ShapeEnum.RECTANGLE;
        this.isResizeable = false;
        this.isWater = false;
        if (fileName == null)
        {
            this.name = "BLACK";
        }
        else
        {
            int numIdx = fileName.length() - 1;
            this.name = fileName.substring(0, numIdx) + " " + fileName.charAt(numIdx);
            image = Main.IMPORTER.getImage("Block/" + fileName + ".png");
        }

        if (edgeType != null)
        {
            edgeTextureList[edgeType.ordinal()].add(this);
        }
    }

    public BlockTexture(Entity.ShapeEnum shape, boolean isWater)
    {
        this.shape = shape;
        if (isWater) this.name = "WATER";
        else this.name = shape.toString();
        this.pixelHitWidth = Float.NaN;
        this.pixelHitHeight = Float.NaN;
        this.left = 0;
        this.top = 0;
        this.isResizeable = true;
        this.isWater = isWater;
    }

    public Image getImage() { return image.getImage(); }

    public String toString()
    {
        return name;
    }

    public static BlockTexture getRandomEdgeTexture(EdgeType edgeType)
    {
        ArrayList<BlockTexture> list = edgeTextureList[edgeType.ordinal()];
        int r = random.nextInt(list.size());
        return list.get(r);
    }


    public static void loadBlockTypes()
    {
        for (int i=0; i<EdgeType.values().length; i++)
        {
            edgeTextureList[i] = new ArrayList<BlockTexture>();
        }

        blockTextureList = new BlockTexture[40];
        blockTextureList[0] = new BlockTexture(Entity.ShapeEnum.RECTANGLE, false);
        blockTextureList[1] = new BlockTexture(Entity.ShapeEnum.RECTANGLE, true);
        blockTextureList[2] = new BlockTexture(null,  32,32,0,0, null);

        blockTextureList[3] = new BlockTexture("Left0",  32,32,4,0, EdgeType.LEFT);
        blockTextureList[4] = new BlockTexture("Left1",  32,32,2,0, EdgeType.LEFT);
        blockTextureList[5] = new BlockTexture("Left2",  32,32,2,0, EdgeType.LEFT);
        blockTextureList[6] = new BlockTexture("Left3",  32,32,2,0, EdgeType.LEFT);
        blockTextureList[7] = new BlockTexture("Left4",  32,32,2,0, EdgeType.LEFT);
        blockTextureList[8] = new BlockTexture("Left5",  32,32,0,0, EdgeType.LEFT);

        blockTextureList[9] = new BlockTexture("Right0",  32,32,0,0, EdgeType.RIGHT);
        blockTextureList[10] = new BlockTexture("Right1",  32,32,0,0, EdgeType.RIGHT);
        blockTextureList[11] = new BlockTexture("Right2",  32,32,0,0, EdgeType.RIGHT);
        blockTextureList[12] = new BlockTexture("Right3",  32,32,0,0, EdgeType.RIGHT);
        blockTextureList[13] = new BlockTexture("Right4",  32,32,0,0, EdgeType.RIGHT);
        blockTextureList[14] = new BlockTexture("Right5",  32,32,0,0, EdgeType.RIGHT);

        blockTextureList[15] = new BlockTexture("Top0",  32,32,0,0, EdgeType.TOP);
        blockTextureList[16] = new BlockTexture("Top1",  32,32,0,1, EdgeType.TOP);
        blockTextureList[17] = new BlockTexture("Top2",  32,32,0,1, EdgeType.TOP);
        blockTextureList[18] = new BlockTexture("Top3",  32,32,0,1, EdgeType.TOP);
        blockTextureList[19] = new BlockTexture("Top4",  32,32,0,2, EdgeType.TOP);
        blockTextureList[20] = new BlockTexture("Top5",  32,32,0,2, EdgeType.TOP);
        blockTextureList[21] = new BlockTexture("Top6",  32,32,0,4, EdgeType.TOP);
        blockTextureList[22] = new BlockTexture("Top7",  32,32,0,1, EdgeType.TOP);
        blockTextureList[23] = new BlockTexture("Top8",  32,32,0,2, EdgeType.TOP);

        blockTextureList[24] = new BlockTexture("Top-Left0",  32,32,2,3, EdgeType.TOPLEFT);
        blockTextureList[25] = new BlockTexture("Top-Left1",  32,32,3,1, EdgeType.TOPLEFT);
        blockTextureList[26] = new BlockTexture("Top-Left2",  32,32,0,1, EdgeType.TOPLEFT);

        blockTextureList[27] = new BlockTexture("Top-Right0",  32,32,0,3, EdgeType.TOPRIGHT);
        blockTextureList[28] = new BlockTexture("Top-Right1",  32,32,0,1, EdgeType.TOPRIGHT);
        blockTextureList[29] = new BlockTexture("Top-Right2",  32,32,0,1, EdgeType.TOPRIGHT);

        blockTextureList[30] = new BlockTexture("Top-Left-Inside0",  32,32,0,0, null);
        blockTextureList[31] = new BlockTexture("Top-Left-Inside1",  32,32,0,0, null);
        blockTextureList[32] = new BlockTexture("Top-Left-Inside2",  32,32,0,0, null);

        blockTextureList[33] = new BlockTexture("Top-Right-Inside0",  32,32,0,0, null);
        blockTextureList[34] = new BlockTexture("Top-Right-Inside1",  32,32,0,0, null);
        blockTextureList[35] = new BlockTexture("Top-Right-Inside2",  32,32,0,0, null);

        blockTextureList[36] = new BlockTexture("Bottom-Left0",  32,32,1,0, EdgeType.BOTLEFT);
        blockTextureList[37] = new BlockTexture("Bottom-Right0",  32,32,0,0, EdgeType.BOTRIGHT);

        blockTextureList[38] = new BlockTexture("Bottom0",  32,32,0,0, EdgeType.BOT);
        blockTextureList[39] = new BlockTexture("Bottom1",  32,32,0,0, EdgeType.BOT);

    }
}
