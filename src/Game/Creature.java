package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Creature implements Actor
{
    GraphicsContext context;
    int xPos, yPos, width, height;
    Direction direction;
    Block.Face face;

    int xSpeed, ySpeed, xAcc, yAcc;

    Creature(GraphicsContext context,
             int xPos, int yPos, int width, int height)
    {
        this.context = context;
        this.xPos = xPos; this.yPos = yPos;
        this.width = width; this.height = height;
        direction = new Direction();
        face = null;

        ySpeed = 0;
        yAcc = 1;
    }

    @Override
    public void act()
    {
        if (ySpeed == 0) direction.vert = Direction.Enum.NONE;
        else if (ySpeed > 0) direction.vert = Direction.Enum.DOWN;
        else if (ySpeed < 0) direction.vert = Direction.Enum.UP;
        if (xSpeed == 0) direction.horiz = Direction.Enum.NONE;
        else if (xSpeed > 0) direction.horiz = Direction.Enum.RIGHT;
        else if (xSpeed < 0) direction.horiz = Direction.Enum.LEFT;

        if (face != null
                && face.direction.vert == Direction.Enum.UP
                && face.incline == Block.Incline.LEVEL)
        {
            yAcc = 0;
            ySpeed = 0;
        }
        else yAcc = 1;

        xSpeed += xAcc;
        ySpeed += yAcc;
        xPos += xSpeed;
        yPos += ySpeed;
    }

    @Override
    public void draw(int x, int y)
    {
        context.setFill(Color.BLACK);
        context.fillRect(xPos, yPos, width, height);
    }

    public Direction getDirection()
    {
        return direction;
    }

    void collide(Block.Face face)
    {
        if (face == null) return;
        this.face = face;
    }
}
