package Gameplay.Entities;

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
    public Image image;

    public static BlockType[] blockTypeList;

    public BlockType(String fileName, int pixelHitWidth, int pixelHitHeight, int left, int top)
    {
        int numIdx = fileName.length()-1;
        this.name = fileName.substring(0,numIdx)+" "+fileName.charAt(numIdx) + ": " + pixelHitWidth + "x" + pixelHitHeight;
        this.pixelHitWidth = pixelHitWidth;
        this.pixelHitHeight = pixelHitHeight;
        this.left = left;
        this.top = top;
        this.shape = Entity.ShapeEnum.RECTANGLE;
        this.isResizeable = false;

        //String path = "Resources/Image/block/"+fileName+".png";
        String path = "Image/block/"+fileName+".png";
        //Print.cyan("BlockType: Load image ["+path+"]");
        image = new Image(path);
    }

    public BlockType(Entity.ShapeEnum shape)
    {
        this.shape = shape;
        this.name = shape.toString();
        this.pixelHitWidth = Float.NaN;
        this.pixelHitHeight = Float.NaN;
        this.left = 0;
        this.top = 0;
        this.isResizeable = true;
    }

    public String toString()
    {
        return name;
    }


    public static void loadBlockTypes()
    {
        blockTypeList = new BlockType[30];
        blockTypeList[0] = new BlockType(Entity.ShapeEnum.RECTANGLE);
        blockTypeList[1] = new BlockType("Left0",  32,32,4,0);
        blockTypeList[2] = new BlockType("Left1",  32,32,2,0);
        blockTypeList[3] = new BlockType("Left2",  32,32,2,0);
        blockTypeList[4] = new BlockType("Left3",  32,16,2,0);
        blockTypeList[5] = new BlockType("Left4",  32,16,2,0);

        blockTypeList[6] = new BlockType("Right0",  32,32,0,0);
        blockTypeList[7] = new BlockType("Right1",  32,32,0,0);
        blockTypeList[8] = new BlockType("Right2",  32,32,0,0);
        blockTypeList[9] = new BlockType("Right3",  32,16,0,0);
        blockTypeList[10] = new BlockType("Right4",  32,16,0,0);

        blockTypeList[11] = new BlockType("Top0",  32,32,0,0);
        blockTypeList[12] = new BlockType("Top1",  32,32,0,1);
        blockTypeList[13] = new BlockType("Top2",  32,32,0,1);
        blockTypeList[14] = new BlockType("Top3",  32,32,0,1);
        blockTypeList[15] = new BlockType("Top4",  48,32,0,1);
        blockTypeList[16] = new BlockType("Top5",  48,32,0,2);
        blockTypeList[17] = new BlockType("Top6",  16,32,0,1);

        blockTypeList[18] = new BlockType("Top-Left0",  32,32,2,3);
        blockTypeList[19] = new BlockType("Top-Left1",  32,32,3,1);
        blockTypeList[20] = new BlockType("Top-Right0",  32,32,0,3);
        blockTypeList[21] = new BlockType("Top-Right1",  32,32,0,1);

        blockTypeList[22] = new BlockType("Left-Bend0",  32,64,2,0);
        blockTypeList[23] = new BlockType("Right-Bend0",  32,64,0,0);

        blockTypeList[24] = new BlockType("Top-Left-Bend0",  32,48,2,3);
        blockTypeList[25] = new BlockType("Top-Right-Bend0",  32,48,0,3);

        blockTypeList[26] = new BlockType("Top-Left-Inside0",  32,32,0,0);
        blockTypeList[27] = new BlockType("Top-Right-Inside0",  32,32,0,0);

        blockTypeList[28] = new BlockType("Bottom-Left0",  32,32,1,0);
        blockTypeList[29] = new BlockType("Bottom-Right0",  32,32,0,0);
    }
}
