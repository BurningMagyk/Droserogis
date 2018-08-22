package Gameplay;

public class Item extends Entity
{
    private boolean reactive = false;
    private boolean receptive = false;

    Item(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE);
    }
}
