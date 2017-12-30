package Game;

/**
 * Start with just using rectangular platforms
 */
public class Block
{
    int xPos, yPos, width, height;
    Face faces[];

    Block(int xPos, int yPos, int width, int height)
    {
        this.xPos = xPos; this.yPos = yPos;
        this.width = width; this.height = height;

        /* Right now, the constructor only makes rectangles */
        faces = new Face[]{
                new Face(FaceEnum.UP),
                new Face(FaceEnum.RIGHT),
                new Face(FaceEnum.DOWN),
                new Face(FaceEnum.LEFT)};
    }

    public void act()
    {
    }

    public void draw(int x, int y) {

    }

    public Direction checkCollision(Direction direction)
    {
        return null;
    }

    private FaceEnum[] checkCollision(Creature cret)
    {
        /* TODO: This needs to be redone to work with multi-direction */
        if (cret.xPos + cret.width < xPos) return null;
        if (cret.yPos + cret.height < yPos) return null;
        if (cret.xPos > xPos + width) return null;
        if (cret.yPos > yPos + height) return null;

        if (cret.xPos + cret.width < xPos + width) return null;

        return null;
    }

    private enum FaceEnum
    {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    class Face
    {
        FaceEnum faceEnums[];

        Face(FaceEnum... faceEnums)
        {
            this.faceEnums = faceEnums;
        }

        FaceEnum[] getFaceEnums()
        {
            return faceEnums;
        }
    }
}
