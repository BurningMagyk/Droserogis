package Gameplay;

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
                },
        TRIANGLE_UP_L
                {
                    public boolean isTriangle() {return true;}
                },
        TRIANGLE_DW_R
                {
                    public boolean isTriangle() {return true;}
                },
        TRIANGLE_DW_L
                {
                    public boolean isTriangle() {return true;}
                };

        public boolean isTriangle() {return false;}
    }

    public static final int UP=0;
    public static final int RIGHT=1;
    public static final int DOWN=2;
    public static final int LEFT=3;


    private Vec2 pos;
    private Vec2 velocity = new Vec2(Vec2.ZERO);
    private Vec2 accleration = new Vec2(Vec2.ZERO);

    private float width, height;
    private boolean dynamic;
    private Vec2[] vertexList;

    private final ShapeEnum shape;
    private Color color = Color.BLACK;
    ;

    private boolean triggered = false;
    private float gravityScale = 1;
    private float friction = 1;


    Entity(float xPos, float yPos, float width, float height, ShapeEnum shape, boolean dynamic)
    {
        this.width = width;
        this.height = height;
        this.shape = shape;

        pos = new Vec2(xPos, yPos);
        this.dynamic = dynamic;


        //For a triangle, the given (xPos,yPos) is the center of the hypotenuse.
        //  This is done to make it easy to align objects in level building. However, the center of the hypotenuse would NOT
        //  be the center of mass of a right triangle of a thin lamina of uniform density.
        if (shape == ShapeEnum.TRIANGLE_UP_R)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(-width / 2, height / 2);
            vertexList[1] = new Vec2(-width / 2, -height / 2);
            vertexList[2] = new Vec2(width / 2, height / 2);
        }

        else if (shape == ShapeEnum.TRIANGLE_UP_L)
        {
            vertexList = new Vec2[3];
            vertexList[0] = new Vec2(width / 2, height / 2);
            vertexList[1] = new Vec2(-width / 2, height / 2);
            vertexList[2] = new Vec2(width / 3, -height / 2);
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

        //bodyDef.position.set(xPos, yPos);
        //bodyDef.type = dynamic ? BodyType.DYNAMIC : BodyType.KINEMATIC;
        //body = world.createBody(bodyDef);

        //fixtureDef.density = 0.005F;
        //fixtureDef.friction = 0.3F;
        //fixtureDef.friction = 0F;
        //fixtureDef.shape = polygonShape;

        //body.setFixedRotation(true);
    }


    public Vec2 getPosition() { return new Vec2(pos); }

    public float getX() { return pos.x; }

    public float getY() { return pos.y; }

    public boolean isDynamic() {return dynamic;}

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

    public void setAccleration(float x, float y)
    {
        accleration.x = x;
        accleration.y = y;
    }

    public float getAccelerationX() {return accleration.x;}

    public float getAccelerationY() {return accleration.y;}

    public void setAccelerationX(float x) {accleration.x = x;}

    public void setAcclerationY(float y) {accleration.y = y;}

    //public Vec2 getNewPos(long)
    //{
    //  Vec2 p = new Vec2(pos);
    //  velocity
    //}


    public float getLeftEdge() { return pos.x - width/2; }

    public float getRightEdge() { return pos.x + width/2; }

    public float getTopEdge() { return pos.y - height/2; }

    public float getBottomEdge() { return pos.y + height/2; }

    public float getVertexX(int i)
    {
        return vertexList[i].x;
    }

    public float getVertexY(int i)
    {
        return vertexList[i].y;
    }

    public void setColor(Color c) {color = c;}

    public Color getColor() {return color;}

    public void resetFlags()
    {
        triggered = false;
    }

    public boolean getTriggered() { return triggered;}

    public void setTriggered(boolean triggered) { this.triggered = triggered;}

    public void setGravityScale(float scale) {gravityScale = scale;}

    public void setFriction(float friction) {this.friction = friction;}

    public ShapeEnum getShape() {return shape;}


    //================================================================================================================
    //
    //================================================================================================================
    public boolean isHit(Entity other, Vec2 goal)
    {
        if (goal.x + other.width / 2 <= pos.x - width / 2) return false;
        if (goal.x - other.width / 2 >= pos.x + width / 2) return false;
        if (goal.y + other.height / 2 <= pos.y - height / 2) return false;
        if (goal.y - other.height / 2 >= pos.y + height / 2) return false;

        return true;
    }

}
