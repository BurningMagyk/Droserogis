package Gameplay;

import javafx.scene.paint.Color;


class Block extends Entity
{


    //Bounding box: for an axis-aligned rectangle, this equals the box itself.
    //For a triangle or non-axis-aligned rectangle, the actual shape is contained within the bounding box.
    private float leftBound, rightBound, topBound, bottomBound;

    public Block(float xPos, float yPos, float width, float height, ShapeEnum shape)
    {
        super(xPos, yPos, width, height, shape);



        //body.createFixture(fixtureDef);
        //updateBoundValues();
    }

    /*
    float getLeftEdge()
    {
        if (triangular) return leftBound;
        else return super.getLeftEdge();
    }
    float getRightEdge()
    {
        if (triangular) return rightBound;
        else return super.getRightEdge();
    }
    float getTopEdge()
    {
        if (triangular) return topBound;
        else return super.getTopEdge();
    }
    float getBottomEdge()
    {
        if (triangular) return bottomBound;
        else return super.getBottomEdge();
    }
    */

    /*
    enum Orient
    {
        UP_LEFT
                {
                    int xa() { return 1; } int ya() { return 1; }
                    int xb() { return 1; } int yb() { return -1; }
                    int xc() { return -1; } int yc() { return 1; }
                    boolean isUp() { return true; }
                    boolean isLeft() { return true; }
                },
        UP_RIGHT
                {
                    int xa() { return -1; } int ya() { return 1; }
                    int xb() { return -1; } int yb() { return -1; }
                    int xc() { return 1; } int yc() { return 1; }
                    boolean isUp() { return true; }
                    boolean isRight() { return true; }
                },
        DOWN_LEFT
                {
                    int xa() { return 1; } int ya() { return -1; }
                    int xb() { return 1; } int yb() { return 1; }
                    int xc() { return -1; } int yc() { return -1; }
                    boolean isDown() { return true; }
                    boolean isLeft() { return true; }
                },
        DOWN_RIGHT
                {
                    int xa() { return -1; } int ya() { return -1; }
                    int xb() { return -1; } int yb() { return 1; }
                    int xc() { return 1; } int yc() { return -1; }
                    boolean isDown() { return true; }
                    boolean isRight() { return true; }
                },
        BOX;
        int xa() { return 0; } int xb() { return 0; } int xc() { return 0; }
        int ya() { return 0; } int yb() { return 0; } int yc() { return 0; }
        boolean isUp() { return false; } boolean isDown() { return false; }
        boolean isLeft() { return false; } boolean isRight() { return false; }
    }

    boolean isUp() { return orient.isUp(); }
    boolean isDown() { return orient.isDown(); }
    boolean isLeft() { return orient.isLeft(); }
    boolean isRight() { return orient.isRight(); }

    */

    @Override
    public Color getColor()
    {
      return getTriggered() ? Color.ORANGE : Color.YELLOW;
    }



    /**
     * Every time the block is moved, this method should be called.
     * These values stay the same if the block doesn't move, so it would
     * be more efficient to not call this method if the block is not
     * moving. Should be called once at the very beginning though.
     */
    /*
    private void updateBoundValues()
    {
        float xPos[] = new float[3];
        float yPos[] = new float[3];
        Vec2 points[] = polygonShape.getVertices();
        Vec2 cPos = getPosition();
        for (int i = 0; i < 3; i++)
        {
            xPos[i] = points[i].x + cPos.x;
            yPos[i] = points[i].y + cPos.y;
        }

        leftBound = Math.min(Math.min(xPos[0], xPos[1]), xPos[2]);
        rightBound = Math.max(Math.max(xPos[0], xPos[1]), xPos[2]);
        topBound = Math.min(Math.min(yPos[0], yPos[1]), yPos[2]);
        bottomBound = Math.max(Math.max(yPos[0], yPos[1]), yPos[2]);
    }
    */
}