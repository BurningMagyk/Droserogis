package Game;

/**
 * Start with just using rectangular platforms
 */
public class Platform
{
    int xPos, yPos, width, height;

    Platform(int xPos, int yPos, int width, int height)
    {
        this.xPos = xPos; this.yPos = yPos;
        this.width = width; this.height = height;
    }

    public void act()
    {

    }

    public void draw(int x, int y) {

    }

    private FaceEnum checkCollision(Creature creature)
    {
        //if (creature.xPos + )

        return null;
    }

    private enum FaceEnum
    {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
