package Gameplay;

import Util.GradeEnum;
import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

abstract public class Entity
{
    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;

    int opp(int dir)
    {
        if (dir == UP) return DOWN;
        if (dir == DOWN) return UP;
        if (dir == LEFT) return RIGHT;
        if (dir == RIGHT) return LEFT;
        return -1;
    }

    public enum ShapeEnum
    {
        RECTANGLE
                {
                    public String getText() {return "Rectangle";}
                },
        TRIANGLE_UP_R
                {
                    public boolean isTriangle() {return true;}
                    public boolean[] getDirs() {return new boolean[]
                            {false, false, true, true};}
                    public String getText() {return "Triangle (upward right)";}
                },
        TRIANGLE_UP_L
                {
                    public boolean isTriangle() {return true;}
                    public boolean[] getDirs() {return new boolean[]
                            {false, true, true, false};}
                    public String getText() {return "Triangle (upward left)";}
                },
        TRIANGLE_DW_R
                {
                    public boolean isTriangle() {return true;}
                    public boolean[] getDirs() {return new boolean[]
                            {true, false, false, true};}
                    public String getText() {return "Triangle (downward right)";}
                },
        TRIANGLE_DW_L
                {
                    public boolean isTriangle() {return true;}
                    public boolean[] getDirs() {return new boolean[]
                            {true, true, false, false};}
                    public String getText() {return "Triangle (downward left)";}
                };

        public abstract String getText();
        public boolean isTriangle() {return false;}
        public boolean[] getDirs() {return new boolean[]
                {true, true, true, true};}
    }

    //pos is the position of the entity.
    //  For a rectangle, pos is the intersection of the two diagonals.
    //  For a triangle, pos is the midpoint of the hypotenuse.
    private Vec2 pos;
    private Vec2 velocity = new Vec2(Vec2.ZERO);
    private Vec2 acceleration = new Vec2(Vec2.ZERO);

    private float width, height;
    private double sinTheta, cosTheta;
    private Vec2 normal = null;
    private Vec2[] vertexList;

    private final ShapeEnum shape;
    private Color color = Color.BLACK;

    private boolean triggered = false;
    private float friction = 0.5F;
    private float mass = 5;


    Entity(float xPos, float yPos, float width, float height, ShapeEnum shape)
    {
        this.width = width;
        this.height = height;
        this.shape = shape;

        pos = new Vec2(xPos, yPos);

        //For a triangle, the given (xPos,yPos) is the center of the hypotenuse.
        //  This is done to make it easy to align objects in level building. However, the center of the hypotenuse would NOT
        //  be the center of mass of a right triangle of a thin lamina of uniform density.
        if (shape == ShapeEnum.TRIANGLE_UP_R)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(-width / 2, height / 2);   // Lower-left corner (square corner)
            vertexList[1] = new Vec2(-width / 2, -height / 2);  // Upper-left corner
            vertexList[2] = new Vec2(width / 2, height / 2);    // Lower-right corner
            normal = new Vec2(width, -height);
        }

        else if (shape == ShapeEnum.TRIANGLE_UP_L)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(width / 2, height / 2);    // Lower-right corner (square corner)
            vertexList[1] = new Vec2(-width / 2, height / 2);   // Lower-left corner
            vertexList[2] = new Vec2(width / 2, -height / 2);   // Upper-right corner
            normal = new Vec2(-width, -height);
        }

        else if (shape == ShapeEnum.TRIANGLE_DW_R)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(-width / 2, -height / 2);
            vertexList[1] = new Vec2(width / 2, -height / 2);
            vertexList[2] = new Vec2(-width / 2, height / 2);
            normal = new Vec2(width, height);
        }
        else if (shape == ShapeEnum.TRIANGLE_DW_L)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(width / 2, -height / 2);
            vertexList[1] = new Vec2(width / 2, height / 2);
            vertexList[2] = new Vec2(-width / 2, -height / 2);
            normal = new Vec2(-width, height);
        }
        else if (shape == ShapeEnum.RECTANGLE)
        {
            vertexList = new Vec2[4];
            vertexList[0] = new Vec2(width / 2, height / 2);
            vertexList[1] = new Vec2(width / 2, -height / 2);
            vertexList[2] = new Vec2(-width / 2, -height / 2);
            vertexList[3] = new Vec2(-width / 2, height / 2);
        }

        double hypotenuse = Math.sqrt(width * width + height * height);
        sinTheta = (double) height / hypotenuse;
        cosTheta = (double) width / hypotenuse;
    }

    public Vec2 getPosition() { return new Vec2(pos); }
    public float getX() { return pos.x; }
    public float getY() { return pos.y; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Vec2 getDims() { return new Vec2(width, height); }

    void setWidth(float width) { this.width = width; }
    void setHeight(float height) { this.height = height; }

    public Vec2 getVelocity() { return new Vec2(velocity); }
    public float getVelocityX() { return velocity.x; }
    public float getVelocityY() { return velocity.y; }

    public void setVelocity(Vec2 v)
    {
        velocity.x = v.x;
        velocity.y = v.y;
    }
    public void setPosition(Vec2 p)
    {
        pos.x = p.x;
        pos.y = p.y;
    }
    public void setPositionX(float x) { pos.x = x; }
    public void setPositionY(float y) { pos.y = y; }

    public void setPosition(float x, float y)
    {
        pos.x = x;
        pos.y = y;
    }

    public void setVelocityX(float x) { velocity.x = x; }
    public void setVelocityY(float y)
    {
        velocity.y = y;
    }
    public void setVelocity(float x, float y)
    {
        velocity.x = x;
        velocity.y = y;
    }

    void addVelocity(Vec2 v) { velocity.add(v); }
    void addVelocity(float x, float y) { velocity.add(new Vec2(x, y)); }
    void addVelocityX(float x) { velocity.x += x; }
    void addVelocityY(float y) { velocity.y += y; }

    Vec2 getAcceleration() {return new Vec2(acceleration);}
    float getAccelerationX() {return acceleration.x;}
    float getAccelerationY() {return acceleration.y;}

    public void setAcceleration(Vec2 v)
    {
        acceleration.x = v.x;
        acceleration.y = v.y;
    }
    public void setAcceleration(float x, float y)
    {
        acceleration.x = x;
        acceleration.y = y;
    }
    void setAccelerationX(float x) {acceleration.x = x;}
    void setAccelerationY(float y) {acceleration.y = y;}

    void addAcceleration(Vec2 a) { acceleration.add(a); }
    void addAcceleration(float x, float y) { acceleration.add(new Vec2(x, y)); }
    void addAccelerationX(float x) { acceleration.x += x; }
    void addAccelerationY(float y) { acceleration.y += y; }


    public float getLeftEdge() { return pos.x - width / 2; }

    public float getRightEdge() { return pos.x + width / 2; }

    public float getTopEdge() { return pos.y - height / 2; }

    public float getBottomEdge() { return pos.y + height / 2; }

    public float getVertexX(int i) { return vertexList[i].x + pos.x; }

    public float getVertexY(int i) { return vertexList[i].y + pos.y; }

    public void setColor(Color c) { color = c; }

    public Color getColor() {return color;}

    void resetFlags() { triggered = false; }
    boolean getTriggered() { return triggered;}
    boolean setTriggered(boolean triggered)
    {
        this.triggered = triggered;
        return true;
    }

    float getFriction() { return friction; }
    void setFriction(float friction) { this.friction = friction; }

    float getMass() { return mass; }
    void setMass(float mass) { this.mass = mass; }

    public ShapeEnum getShape() { return shape; }

    /**
     * Returns the direction that the other entity would move in according to
     * what velocity it had.
     */
    public Vec2 applySlope(Vec2 origin)
    {
//        if (shape != ShapeEnum.TRIANGLE_UP_L
//                && shape != ShapeEnum.TRIANGLE_UP_R) return new Vec2(0, 0);
//
//        double originMagnitude = Math.sqrt(origin.x * origin.x + origin.y * origin.y);
//        if (originMagnitude == 0F) return new Vec2(0, 0);
//
//        /* Dot product of origin and normal */
//        float dotProduct = origin.x * normal.x + origin.y * normal.y;
//        double cosTheta = dotProduct / (originMagnitude * normalMagnitude);
//
//        /* The direction of the sloped surface multiplied by cos(theta) */
//        float xComp = (float) (-width * cosTheta / normalMagnitude);
//        float yComp = (float) (-height * cosTheta / normalMagnitude);
//        Print.yellow("x: " + xComp + ", y: " + yComp);
//        return new Vec2(xComp, yComp);

        Vec2 fromX = applySlopeX(origin.x);
        Vec2 fromY = applySlopeY(origin.y);
        return new Vec2(fromX.x + fromY.x, fromX.y + fromY.y);
    }

    /**
     * Returns the direction that the other entity would move in according to
     * what horizontal velocity it has.
     */
    public Vec2 applySlopeX(float totalVel)
    {
        switch (shape)
        {
            case TRIANGLE_UP_L:
                return new Vec2((float) (totalVel * cosTheta),
                        (float) (-totalVel * sinTheta));
            case TRIANGLE_UP_R:
                return new Vec2((float) (totalVel * cosTheta),
                        (float) (totalVel * sinTheta));
            case TRIANGLE_DW_L:
                return new Vec2((float) (totalVel * cosTheta),
                        (float) (totalVel * sinTheta));
            case TRIANGLE_DW_R:
                return new Vec2((float) (totalVel * cosTheta),
                        (float) (-totalVel * sinTheta));
            default:
                return new Vec2(totalVel, 0);
        }
    }

    /**
     * Returns the direction that the other entity would move in according to
     * what vertical velocity it has.
     */
    public Vec2 applySlopeY(float totalVel)
    {
        switch (shape)
        {
            case TRIANGLE_UP_L:
                return new Vec2((float) (-totalVel * sinTheta),
                        (float) (totalVel * cosTheta));
            case TRIANGLE_UP_R:
                return new Vec2((float) (totalVel * sinTheta),
                        (float) (totalVel * cosTheta));
            case TRIANGLE_DW_L:
                return new Vec2((float) (totalVel * sinTheta),
                        (float) (totalVel * cosTheta));
            case TRIANGLE_DW_R:
                return new Vec2((float) (-totalVel * sinTheta),
                        (float) (totalVel * cosTheta));
            default:
                return new Vec2(0, totalVel);
        }
    }

    //================================================================================================================
    //
    //================================================================================================================
    boolean withinBoundsX(Entity other)
    {
        return other.getX() - other.width / 2 <= pos.x + width / 1.999
                && other.getX() + other.width / 2 >= pos.x - width / 1.999;
    }

    boolean withinBoundsY(Entity other)
    {
        return other.getY() - other.height / 2 <= pos.y + height / 1.999
                && other.getY() + other.width / 2 >= pos.y - height / 1.999;
    }

    protected boolean withinBounds(Entity other)
    {
        return withinBoundsX(other) && withinBoundsY(other);
    }

    boolean surroundsX(Entity other)
    {
        return other.getX() - other.width / 2 >= pos.x - width / 2
                && other.getX() + other.width / 2 <= pos.x + width / 2;
    }

    boolean surroundsY(Entity other)
    {
        return other.getY() - other.height / 2 >= pos.y - height / 2
                && other.getY() + other.height / 2 <= pos.y + height / 2;
    }

    boolean surrounds(Entity other)
    {
        return surroundsX(other) && surroundsY(other);
    }


    //====================================================================================================
    // Added for use by levelBuilder.
    // Currently only used for Block subclass of Entity.
    // If a vertex is near the given point, then that vertex index is returned.
    // Otherwise, getVertexNear() returns -1;
    //====================================================================================================
    public int getVertexNear(double x, double y) {
        double radiusSquared = 16; //4 pixels squared
        for (int i=0; i<vertexList.length; i++) {
            double dx = pos.x + vertexList[i].x - x;
            double dy = pos.y + vertexList[i].y - y;
            double distSquared = dx*dx + dy*dy;
            if (distSquared < radiusSquared) return i;
        }
        return -1;
    }






    public int[] getTouchEdge(Entity other, Vec2 goal)
    {
        int[] directions = {-1, -1};

        if (goal.x + other.width / 2 <= pos.x - width / 1.999) return directions;
        if (goal.x - other.width / 2 >= pos.x + width / 1.999) return  directions;
        if (goal.y + other.height / 2 <= pos.y - height / 1.999) return  directions;
        if (goal.y - other.height / 2 >= pos.y + height / 1.999) return  directions;

        boolean[] shapeDirs = shape.getDirs();

        /* The Actor is within the x-bounds */
        if (withinBoundsX(other))
        {
            /* If this Entity has a level bottom side */
            if (shapeDirs[DOWN]
                    /* And the other is in contact with it */
                    && other.getY() > getBottomEdge()
                    && goal.y - other.height / 1.999 < getBottomEdge())
            {
                directions[0] = UP;
                return directions;
            }

            /* If this Entity has a level top side */
            if (shapeDirs[UP]
                    /* And the other is in contact with it */
                    && other.getY() < getTopEdge()
                    && goal.y + other.height / 1.999 > getTopEdge())
            {
                directions[0] = DOWN;
                return directions;
            }

            /* If this Entity has an upper-left slope */
            if (!shapeDirs[LEFT] && !shapeDirs[UP]
                    /* And the other is in contact with it */
                    && other.getY() < getTopEdge(other.getX())
                    && goal.y + other.height / 1.999 > getTopEdge(goal.x))
            {
                directions[0] = DOWN;
                directions[1] = RIGHT;
                return directions;
            }

            /* If this Entity has an upper-right slope */
            if (!shapeDirs[RIGHT] && !shapeDirs[UP]
                    /* And the other is in contact with it */
                    && other.getY() < getTopEdge(other.getX())
                    && goal.y + other.height / 1.999 > getTopEdge(goal.x))
            {
                directions[0] = DOWN;
                directions[1] = LEFT;
                return directions;
            }

            /* If this Entity has a lower-left slope */
            if (!shapeDirs[LEFT] && !shapeDirs[DOWN]
                    /* And the other is in contact with it */
                    && other.getY() > getBottomEdge(other.getX())
                    && goal.y - other.height / 1.999 < getBottomEdge(goal.x))
            {
                directions[0] = UP;
                directions[1] = RIGHT;
                return directions;
            }

            /* If this Entity has an lower-right slope */
            if (!shapeDirs[RIGHT] && !shapeDirs[DOWN]
                    /* And the other is in contact with it */
                    && other.getY() > getBottomEdge(other.getX())
                    && goal.y - other.height / 1.999 < getBottomEdge(goal.x))
            {
                directions[0] = UP;
                directions[1] = LEFT;
                return directions;
            }
        }

        /* This Actor is within the y-bounds */
        if (withinBoundsY(other))
        {
            /* This Entity has a level left side */
            if (shapeDirs[LEFT]
                    /* And the other is in contact with it */
                    && other.getX() < getLeftEdge()
                    && goal.x + other.width /1.999 > getLeftEdge())
            {
                directions[0] = RIGHT;
                return directions;
            }

            /* This Entity has a level right side */
            if (shapeDirs[RIGHT]
                    /* And the other is in contact with it */
                    && other.getX() > getRightEdge()
                    && goal.x - other.width / 1.999 < getRightEdge())
            {
                directions[0] = LEFT;
                return directions;
            }
        }

        return directions;
    }

    float getTopEdge(float otherX)
    {
        /* Assumes that the Actor is within the x-bounds */

        if (!shape.isTriangle() || shape.getDirs()[UP])
            return getTopEdge();
        float xRatio = (getVertexX(2) - otherX) / width;
        /* Up-left */
        if (shape.getDirs()[RIGHT])
        {
            return getVertexY(2) + (xRatio * height);
        }
        /* Up-right */
        else // if (shape.getDirs()[LEFT])
        {
            return getVertexY(0) - (xRatio * height);
        }
    }

    float getBottomEdge(float otherX)
    {
        /* Assumes that the Actor is within the x-bounds */

        if (!shape.isTriangle() || shape.getDirs()[DOWN])
            return getBottomEdge();
        float xRatio = (getVertexX(1) - otherX) / width;
        /* Down-left */
        if (shape.getDirs()[RIGHT])
        {
            return getVertexY(1) - (xRatio * height);
        }
        /* Down-right */
        else // if (shape.getDirs()[RIGHT])
        {
            return getVertexY(0) + (xRatio * height);
        }
    }

    public abstract void damage(GradeEnum amount); // TODO: include damage type
}