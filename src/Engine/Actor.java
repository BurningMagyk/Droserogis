package Engine;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Actor
{
    int xPos, yPos, width, height;
    int xVel = 0, yVel = 0, xAcc = 0, yAcc = 0, xFrict = 0, yFrict = 0;
    int xTravelled = 0, yTravelled = 0, xSpeed, ySpeed;

    /* This is the direction that the Actor is moving in.
    *  Will be changed every increment that Actor moves. */
    Direction dir = Direction.NONE;

    int frictionStat = 0;

    Actor(int xPos, int yPos, int width, int height)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    void apply(Force force)
    {
        if (force.xPos != null)
        {
            if (force.xPosRight) xPos = force.xPos + width;
            else xPos = force.xPos;
        }
        if (force.yPos != null)
        {
            if (force.yPosDown) yPos = force.yPos + height;
            else yPos = force.yPos;
        }
        if (force.xVel != null)
        {
            if (force.xJerk) xPos = xVel;
            else xPos += xVel;
        }
        if (force.yVel != null)
        {
            if (force.yJerk) yPos = yVel;
            else yPos += yVel;
        }
        if (force.xAcc != null)
        {
            if (force.xFriction) xFrict += xAcc;
            else xAcc += xAcc;
        }
        if (force.yAcc != null)
        {
            if (force.yFriction) yFrict += yAcc;
            else yAcc += yAcc;
        }
    }

    void act(ArrayList<Block> blocks)
    {
        xSpeed = Math.abs(xVel);
        ySpeed = Math.abs(yVel);

        move(blocks);

        xTravelled = 0;
        yTravelled = 0;

        xAcc = Math.abs(xAcc) < xFrict
                ? 0 : xAcc > 0
                ? xAcc - xFrict : xAcc + xFrict;
        yAcc = Math.abs(yAcc) < yFrict
                ? 0 : yAcc > 0
                ? yAcc - yFrict : yAcc + yFrict;

        xVel += xAcc;
        yVel += yAcc;

        xAcc = 0;
        yAcc = 0;
    }

    void move(ArrayList<Block> blocks)
    {
        for (Block block : blocks)
        {
            Force collision = block.collide(this);
            if (collision != null) apply(collision);
        }
        xSpeed = Math.abs(xVel);
        ySpeed = Math.abs(yVel);

        if (xTravelled >= xSpeed
                && yTravelled >= ySpeed) return;

        if (xTravelled < xSpeed)
        {
            xPos += xVel > 0 ? 1 : -1;
            xTravelled++;
        }
        if (yTravelled < ySpeed)
        {
            yPos += yVel > 0 ? 1 : -1;
            yTravelled++;
        }

        move(blocks);
    }

    void draw(GraphicsContext context, int x, int y)
    {
        context.setFill(Color.BLUE);
        context.fillRect(xPos, yPos, width, height);
    }
}
