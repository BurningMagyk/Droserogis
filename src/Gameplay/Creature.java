package Gameplay;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Creature
{
    GraphicsContext context;
    int xPos, yPos, width, height;
    Direction direction;
    Block.Face face;
    ArrayList<Block> blocks;

    int xSpeed, ySpeed, xAcc, yAcc;

    Creature(GraphicsContext context,
             int xPos, int yPos, int width, int height)
    {
        this.context = context;
        this.xPos = xPos; this.yPos = yPos;
        this.width = width; this.height = height;
        direction = new Direction();
        face = null;
        blocks = new ArrayList<>();

        ySpeed = 0;
        yAcc = 1;
    }

    public void act()
    {
        if (ySpeed == 0) direction.vert = Direction.Enum.NONE;
        else if (ySpeed > 0) direction.vert = Direction.Enum.DOWN;
        else if (ySpeed < 0) direction.vert = Direction.Enum.UP;
        if (xSpeed == 0) direction.horiz = Direction.Enum.NONE;
        else if (xSpeed > 0) direction.horiz = Direction.Enum.RIGHT;
        else if (xSpeed < 0) direction.horiz = Direction.Enum.LEFT;

        xSpeed += xAcc;
        ySpeed += yAcc;

        move(xSpeed, ySpeed);
    }

    public void draw(int x, int y)
    {
        context.setFill(Color.BLUE);
        context.fillRect(xPos, yPos, width, height);
    }

    public Direction getDirection()
    {
        return direction;
    }

    public void setBlocks(ArrayList<Block> blocks)
    {
        this.blocks = blocks;
    }

    /**
     * x and y are how far it should move during this frame.
     * This is called recursively so that it moves in small increments and
     * checks block collision between each time.
     */
    void move(int x, int y)
    {
        /* TODO: newX and newY need to be affected by the collide() for this to work */
        for (Block block : blocks)
        {
            collide(block.checkCollision(xPos, xPos + width, yPos, yPos + height, direction));
        }

        if (x == 0 && y == 0) return;

        /*int newX;
        if (x != 0)
        {
            if (Math.abs(x) < width)
            {
                xPos += x;
                newX = 0;
            }
            else
            {
                if (x < 0)
                {
                    newX = x + width;
                    xPos -= width;
                }
                else
                {
                    newX = x - width;
                    xPos += width;
                }
            }
        }
        else newX = 0;

        int newY;
        if (y != 0)
        {
            if (Math.abs(y) < height)
            {
                yPos += y;
                newY = 0;
            }
            else
            {
                if (y < 0)
                {
                    newY = y + height;
                    yPos -= height;
                }
                else
                {
                    newY = y - height;
                    yPos += height;
                }
            }
        }
        else newY = 0;*/

        int newX;
        if (x != 0)
        {
            if (x < 0)
            {
                newX = x + 1;
                xPos--;
            }
            else
            {
                newX = x - 1;
                xPos++;
            }
        }
        else newX = 0;

        int newY;
        if (y != 0)
        {
            if (y < 0)
            {
                newY = y + 1;
                yPos--;
            }
            else
            {
                newY = y - 1;
                yPos++;
            }
        }
        else newY = 0;

        move(newX, newY);
    }

    void collide(Block.Face face)
    {
        this.face = face;

        if (face == null)
        {
            /* Airborne */
            yAcc = 1;
        }
        else
        {
            if (face.incline == Block.Incline.LEVEL)
            {
                if (face.direction.vert == Direction.Enum.UP)
                {
                    /* Grounded */
                    yAcc = 0;
                    ySpeed = 0;
                    yPos = face.getY(xPos) - height;
                }
                else if (face.direction.vert == Direction.Enum.DOWN)
                {
                    /* TODO: Not tested yet */
                    /* Hit the ceiling */
                    yAcc = 1;
                    ySpeed = 0;
                    yPos = face.getY(xPos);
                }
                else if (face.direction.horiz == Direction.Enum.LEFT)
                {
                    /* TODO: Not tested yet */
                    /* Sliding down the wall */
                    xAcc = 0;
                    xSpeed = 0;
                    xPos = face.getX(yPos);
                    yAcc = 0; /* TODO: Should still accelerate, just less */
                    ySpeed = 1;
                }
                else if (face.direction.horiz == Direction.Enum.RIGHT)
                {
                    Print.blue("Right");
                    /* TODO: Not tested yet */
                    /* Sliding down the wall */
                    xAcc = 0;
                    xSpeed = 0;
                    xPos = face.getX(yPos) - width;
                    yAcc = 0; /* TODO: Should still accelerate, just less */
                    ySpeed = 1;
                }
            }
        }
    }

    /* TODO: Change so that the moving is done in act() */
    void moveLeft(boolean moving)
    {
        if (moving)
        {
            move(-5, 0);
        }
    }

    /* TODO: Change so that the moving is done in act() */
    void moveRight(boolean moving)
    {
        if (moving)
        {
            move(5, 0);
        }
    }

    void jump(boolean moving)
    {
        if (moving)
        {
            ySpeed = -20;
        }
    }
}
