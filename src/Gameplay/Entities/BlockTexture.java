package Gameplay.Entities;

import Importer.ImageResource;
import Menus.Main;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;


public class BlockTexture
{
    public static final int PIXEL_HIT_WIDTH = 32;
    public static final int PIXEL_HIT_HEIGHT = 32;
    public final String name;
    public final int left;
    public final int top;
    private ImageResource image;

    public static BlockTexture[] blockTextureList;

    public enum EdgeType
    {
        TOP, BOT, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOT_LEFT, BOT_RIGHT, END_TOP_LEFT, END_TOP_RIGHT, RAMP_RIGHT18
    }
    public static ArrayList<BlockTexture> edgeTextureList[] = new ArrayList[EdgeType.values().length];
    private static Random random = new Random();

    public BlockTexture(String fileName, int left, int top, EdgeType edgeType)
    {
        this.left = left;
        this.top = top;

        int numIdx = fileName.length() - 1;
        this.name = fileName.substring(0, numIdx) + " " + fileName.charAt(numIdx);
        image = Main.IMPORTER.getImage("Block/" + fileName + ".png");

        if (edgeType != null)
        {
            edgeTextureList[edgeType.ordinal()].add(this);
        }
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

        blockTextureList = new BlockTexture[41];

        blockTextureList[0] = new BlockTexture("Left0",  4,0, EdgeType.LEFT);
        blockTextureList[1] = new BlockTexture("Left1",  2,0, EdgeType.LEFT);
        blockTextureList[2] = new BlockTexture("Left2",  2,0, EdgeType.LEFT);
        blockTextureList[3] = new BlockTexture("Left3",  2,0, EdgeType.LEFT);
        blockTextureList[4] = new BlockTexture("Left4",  2,0, EdgeType.LEFT);
        blockTextureList[5] = new BlockTexture("Left5",  0,0, EdgeType.LEFT);

        blockTextureList[6] = new BlockTexture("Right0",  0,0, EdgeType.RIGHT);
        blockTextureList[7] = new BlockTexture("Right1",  0,0, EdgeType.RIGHT);
        blockTextureList[8] = new BlockTexture("Right2",  0,0, EdgeType.RIGHT);
        blockTextureList[9] = new BlockTexture("Right3",  0,0, EdgeType.RIGHT);
        blockTextureList[10] = new BlockTexture("Right4",  0,0, EdgeType.RIGHT);
        blockTextureList[11] = new BlockTexture("Right5",  0,0, EdgeType.RIGHT);

        blockTextureList[12] = new BlockTexture("Top0",  0,0, EdgeType.TOP);
        blockTextureList[13] = new BlockTexture("Top1",  0,1, EdgeType.TOP);
        blockTextureList[14] = new BlockTexture("Top2",  0,1, EdgeType.TOP);
        blockTextureList[15] = new BlockTexture("Top3",  0,1, EdgeType.TOP);
        blockTextureList[16] = new BlockTexture("Top4",  0,2, EdgeType.TOP);
        blockTextureList[17] = new BlockTexture("Top5",  0,2, EdgeType.TOP);
        blockTextureList[18] = new BlockTexture("Top6",  0,4, EdgeType.TOP);
        blockTextureList[19] = new BlockTexture("Top7",  0,1, EdgeType.TOP);
        blockTextureList[20] = new BlockTexture("Top8",  0,2, EdgeType.TOP);
        blockTextureList[21] = new BlockTexture("Top9",  0,5, EdgeType.TOP);

        blockTextureList[22] = new BlockTexture("Top-Left0",  2,3, EdgeType.TOP_LEFT);
        blockTextureList[23] = new BlockTexture("Top-Left1",  3,1, EdgeType.TOP_LEFT);
        blockTextureList[24] = new BlockTexture("Top-Left2",  0,1, EdgeType.TOP_LEFT);

        blockTextureList[25] = new BlockTexture("Top-Right0",  0,3, EdgeType.TOP_RIGHT);
        blockTextureList[26] = new BlockTexture("Top-Right1",  0,1, EdgeType.TOP_RIGHT);
        blockTextureList[27] = new BlockTexture("Top-Right2",  0,1, EdgeType.TOP_RIGHT);

        blockTextureList[28] = new BlockTexture("EndTop-Right0",  0,0, EdgeType.END_TOP_RIGHT);
        blockTextureList[29] = new BlockTexture("EndTop-Right1",  0,0, EdgeType.END_TOP_RIGHT);
        blockTextureList[30] = new BlockTexture("EndTop-Right2",  0,0, EdgeType.END_TOP_RIGHT);

        blockTextureList[31] = new BlockTexture("EndTop-Left0",  0,0, EdgeType.END_TOP_LEFT);
        blockTextureList[32] = new BlockTexture("EndTop-Left1",  0,0, EdgeType.END_TOP_LEFT);
        blockTextureList[33] = new BlockTexture("EndTop-Left2",  0,0, EdgeType.END_TOP_LEFT);

        blockTextureList[34] = new BlockTexture("Bottom-Left0",  1,0, EdgeType.BOT_LEFT);
        blockTextureList[35] = new BlockTexture("Bottom-Right0",  0,0, EdgeType.BOT_RIGHT);

        blockTextureList[36] = new BlockTexture("Bottom0",  0,0, EdgeType.BOT);
        blockTextureList[37] = new BlockTexture("Bottom1",  0,0, EdgeType.BOT);

        blockTextureList[38] = new BlockTexture("Ramp-Right18.4-0",  0,0, EdgeType.RAMP_RIGHT18);
        blockTextureList[39] = new BlockTexture("Ramp-Right18.4-1",  0,0, EdgeType.RAMP_RIGHT18);
        blockTextureList[40] = new BlockTexture("Ramp-Right18.4-2",  0,0, EdgeType.RAMP_RIGHT18);

    }
}
