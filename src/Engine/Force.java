package Engine;

public class Force
{
    Integer xPos, yPos, xVel, yVel, xAcc, yAcc;
    boolean xPosRight, yPosDown, xJerk, yJerk, xFriction, yFriction;

    Force(boolean xPosRight, boolean yPosDown, boolean xJerk, boolean yJerk,
          boolean xFriction, boolean yFriction)
    {
        this.xPosRight = xPosRight;
        this.yPosDown = yPosDown;
        this.xJerk = xJerk;
        this.yJerk = yJerk;
        this.xFriction = xFriction;
        this.yFriction = yFriction;
    }

    void setValues(Integer xPos, Integer yPos, Integer xVel, Integer yVel,
          Integer xAcc, Integer yAcc)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
        this.xAcc = xAcc;
        this.yAcc = yAcc;
    }
}
