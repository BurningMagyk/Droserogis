package Gameplay.Entities;

import Importer.ImageResource;
import Importer.Importer;
import Menus.Main;
import Util.Print;
import javafx.scene.image.Image;


public class BlockType
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

    public static BlockType[] blockTypeList;

    public BlockType(String fileName, int pixelHitWidth, int pixelHitHeight, int left, int top)
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
    }

    public BlockType(Entity.ShapeEnum shape, boolean isWater)
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


    public static void loadBlockTypes()
    {
        blockTypeList = new BlockType[38];
        blockTypeList[0] = new BlockType(Entity.ShapeEnum.RECTANGLE, false);
        blockTypeList[1] = new BlockType(Entity.ShapeEnum.RECTANGLE, true);
        blockTypeList[2] = new BlockType(null,  32,32,0,0);

        blockTypeList[3] = new BlockType("Left0",  32,32,4,0);
        blockTypeList[4] = new BlockType("Left1",  32,32,2,0);
        blockTypeList[5] = new BlockType("Left2",  32,32,2,0);
        blockTypeList[6] = new BlockType("Left3",  32,32,2,0);
        blockTypeList[7] = new BlockType("Left4",  32,32,2,0);
        blockTypeList[8] = new BlockType("Left5",  32,32,0,0);

        blockTypeList[9] = new BlockType("Right0",  32,32,0,0);
        blockTypeList[10] = new BlockType("Right1",  32,32,0,0);
        blockTypeList[11] = new BlockType("Right2",  32,32,0,0);
        blockTypeList[12] = new BlockType("Right3",  32,32,0,0);
        blockTypeList[13] = new BlockType("Right4",  32,32,0,0);
        blockTypeList[14] = new BlockType("Right5",  32,32,0,0);

        blockTypeList[15] = new BlockType("Top0",  32,32,0,0);
        blockTypeList[16] = new BlockType("Top1",  32,32,0,1);
        blockTypeList[17] = new BlockType("Top2",  32,32,0,1);
        blockTypeList[18] = new BlockType("Top3",  32,32,0,1);
        blockTypeList[19] = new BlockType("Top4",  32,32,0,2);
        blockTypeList[20] = new BlockType("Top5",  32,32,0,2);
        blockTypeList[21] = new BlockType("Top6",  32,32,0,4);
        blockTypeList[22] = new BlockType("Top7",  32,32,0,1);
        blockTypeList[23] = new BlockType("Top8",  32,32,0,2);

        blockTypeList[24] = new BlockType("Top-Left0",  32,32,2,3);
        blockTypeList[25] = new BlockType("Top-Left1",  32,32,3,1);
        blockTypeList[26] = new BlockType("Top-Left2",  32,32,0,1);

        blockTypeList[27] = new BlockType("Top-Right0",  32,32,0,3);
        blockTypeList[28] = new BlockType("Top-Right1",  32,32,0,1);
        blockTypeList[29] = new BlockType("Top-Right2",  32,32,0,1);

        blockTypeList[30] = new BlockType("Top-Left-Inside0",  32,32,0,0);
        blockTypeList[31] = new BlockType("Top-Left-Inside1",  32,32,0,0);
        blockTypeList[32] = new BlockType("Top-Left-Inside2",  32,32,0,0);

        blockTypeList[33] = new BlockType("Top-Right-Inside0",  32,32,0,0);
        blockTypeList[34] = new BlockType("Top-Right-Inside1",  32,32,0,0);
        blockTypeList[35] = new BlockType("Top-Right-Inside2",  32,32,0,0);

        blockTypeList[36] = new BlockType("Bottom-Left0",  32,32,1,0);
        blockTypeList[37] = new BlockType("Bottom-Right0",  32,32,0,0);
    }
}
