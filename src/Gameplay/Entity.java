package Gameplay;

import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

abstract public class Entity
{

    enum ShapeEnum
    {
        RECTANGLE,
        TRIANGLE_UP_R
                {
                    public boolean isTriangle() {return true;}
                    public int[] getDirs() {return new int[]{UP, RIGHT};}
                },
        TRIANGLE_UP_L
                {
                    public boolean isTriangle() {return true;}
                    public int[] getDirs() {return new int[]{UP, LEFT};}
                },
        TRIANGLE_DW_R
                {
                    public boolean isTriangle() {return true;}
                    public int[] getDirs() {return new int[]{DOWN, RIGHT};}
                },
        TRIANGLE_DW_L
                {
                    public boolean isTriangle() {return true;}
                    public int[] getDirs() {return new int[]{DOWN, LEFT};}
                };

        public boolean isTriangle() {return false;}
        public int[] getDirs() {return new int[]{-1, -1};}
    }

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    private Vec2 pos;
    private Vec2 velocity = new Vec2(Vec2.ZERO);
    private Vec2 acceleration = new Vec2(Vec2.ZERO);

    private float width, height;
    private Vec2[] vertexList;

    private final ShapeEnum shape;
    private Color color = Color.BLACK;

    private boolean triggered = false;
    private float friction = 1;


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
        }

        else if (shape == ShapeEnum.TRIANGLE_UP_L)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(width / 2, height / 2);    // Lower-right corner (square corner)
            vertexList[1] = new Vec2(-width / 2, height / 2);   // Lower-left corner
            vertexList[2] = new Vec2(width / 3, -height / 2);   // Upper-right corner
        }

        else if (shape == ShapeEnum.TRIANGLE_DW_R)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(-width / 2, -height / 2);
            vertexList[1] = new Vec2(width / 2, -height / 2);
            vertexList[2] = new Vec2(-width / 2, height / 2);
        }
        else if (shape == ShapeEnum.TRIANGLE_DW_L)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(width / 2, -height / 2);
            vertexList[1] = new Vec2(width / 2, height / 2);
            vertexList[2] = new Vec2(-width / 2, -height / 2);
        }
    }


    public Vec2 getPosition() { return new Vec2(pos); }

    public float getX() { return pos.x; }

    public float getY() { return pos.y; }

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    public Vec2 getVelocity() {return new Vec2(velocity);}

    public float getVelocityX() {return velocity.x;}

    public float getVelocityY() {return velocity.y;}


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

    public void setPositionX(float x) {pos.x = x;}

    public void setPositionY(float y) {pos.y = y;}

    public void setVelocityX(float x)
    {
        velocity.x = x;
    }

    public void setVelocityY(float y)
    {
        velocity.y = y;
    }

    public void setVelocity(float x, float y)
    {
        velocity.x = x;
        velocity.y = y;
    }

    void addVelocity(float x, float y) { velocity.add(new Vec2(x, y)); }
    void addVelocityX(float x) { velocity.x += x; }
    void addVelocityY(float y) { velocity.y += y; }

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

    Vec2 getAcceleration() {return new Vec2(acceleration);}

    float getAccelerationX() {return acceleration.x;}

    float getAccelerationY() {return acceleration.y;}

    void setAccelerationX(float x) {acceleration.x = x;}

    void setAccelerationY(float y) {acceleration.y = y;}

    void addAcceleration(float x, float y) {acceleration.add(new Vec2(x, y));}
    void addAccelerationX(float x) { acceleration.x += x; }
    void addAccelerationY(float y) { acceleration.y += y; }

    //public Vec2 getNewPos(long)
    //{
    //  Vec2 p = new Vec2(pos);
    //  velocity
    //}


    public float getLeftEdge() { return pos.x - width / 2; }

    public float getRightEdge() { return pos.x + width / 2; }

    public float getTopEdge() { return pos.y - height / 2; }

    public float getBottomEdge() { return pos.y + height / 2; }

    public float getVertexX(int i)
    {
        return vertexList[i].x + pos.x;
    }

    public float getVertexY(int i)
    {
        return vertexList[i].y + pos.y;
    }

    public void setColor(Color c) {color = c;}

    public Color getColor() {return color;}

    public void resetFlags()
    {
        triggered = false;
    }

    public boolean getTriggered() { return triggered;}

    public void setTriggered(boolean triggered) { this.triggered = triggered;}

    public float getFriction() { return friction; }

    public void setFriction(float friction) {this.friction = friction;}

    public ShapeEnum getShape() {return shape;}

    //================================================================================================================
    //
    //================================================================================================================
    public int[] getTouchEdge(Entity other, Vec2 goal)
    {
        int[] directions = {-1, -1};

        if (goal.x + other.width / 2 <= pos.x - width / 1.999) return directions;
        if (goal.x - other.width / 2 >= pos.x + width / 1.999) return  directions;
        if (goal.y + other.height / 2 <= pos.y - height / 1.999) return  directions;
        if (goal.y - other.height / 2 >= pos.y + height / 1.999) return  directions;

        /* The Actor is within the x-bounds */
        if (other.getX() - other.width / 2 <= pos.x + width / 1.999
                && other.getX() + other.width / 2 >= pos.x - width / 1.999)
        {
            /* This Entity has a level bottom side */
            if (shape.getDirs()[0] != DOWN)
            {
                if (other.getY() > getBottomEdge()
                        && goal.y - other.height / 1.999 < getBottomEdge())
                {
                    directions[0] = UP;
                    return directions;
                }
            }
            /* This Entity has a level top side */
            if (shape.getDirs()[0] != UP)
            {
                if (other.getY() < getTopEdge()
                        && goal.y + other.height / 1.999 > getTopEdge())
                {
                    directions[0] = DOWN;
                    return directions;
                }
            }
            /* This entity has an upper-left slope */
            else if (shape.getDirs()[1] == LEFT)
            {
                /*vertexList[0] = new Vec2(width / 2, height / 2);    // Lower-right corner (square corner)
                vertexList[1] = new Vec2(-width / 2, height / 2);   // Lower-left corner
                vertexList[2] = new Vec2(width / 3, -height / 2);   // Upper-right corner*/

                if (other.getY() < getTopEdge(other.getX()))
                {
                    if (goal.y + other.height / 1.999 > getTopEdge(goal.x))
                    {
                        directions[0] = DOWN;
                        directions[1] = RIGHT;
                        return directions;
                    }
                }
            }
            else // if (shape.getDirs()[1] == RIGHT)
            {
                if (other.getY() < getTopEdge(other.getX()))
                {
                    if (goal.y + other.height / 1.999 > getTopEdge(goal.x))
                    {
                        directions[0] = DOWN;
                        directions[1] = RIGHT;
                        return directions;
                    }
                }
            }
        }

        /* This Actor is within the y-bounds */
        if (other.getY() - other.height / 2 <= pos.y + height / 1.999
                && other.getY() + other.width / 2 >= pos.y - height / 1.999)
        {
            /* This Entity has a level left side */
            if (shape.getDirs()[1] != LEFT)
            {
                if (other.getX() < getLeftEdge()
                        && goal.x + other.width /1.999 > getLeftEdge())
                {
                    directions[0] = RIGHT;
                    return directions;
                }
            }
            /* This Entity has a level right side */
            if (shape.getDirs()[1] != RIGHT)
            {
                if (other.getX() > getRightEdge()
                        && goal.x - other.width / 1.999 < getRightEdge())
                {
                    directions[0] = LEFT;
                    return directions;
                }
            }
        }

        return directions;
    }

    public float getTopEdge(float otherX)
    {
        if (!shape.isTriangle()) return pos.y - height / 2;
        if (shape.getDirs()[1] == LEFT)
        {
            float xRatio = width / (getVertexX(0)- otherX);
            return getVertexY(0) - (xRatio * height);
        }
        else // if (shape.getDirs()[1] == RIGHT)
        {
            float xRatio = (getVertexX(2) - otherX) / width;
            return getVertexY(0) - (xRatio * height);
        }
    }

    //================================================================================================================
    //
    //================================================================================================================
    //public boolean isNear(Entity other, Vec2 goal)
    //{
    //    if (goal.x + other.width / 2 <= pos.x - width / 2) return false;
   //     if (goal.x - other.width / 2 >= pos.x + width / 2) return false;
     //   if (goal.y + other.height / 2 <= pos.y - height / 2) return false;
    //    if (goal.y - other.height / 2 >= pos.y + height / 2) return false;
//
     //   return true;
    //}

}
