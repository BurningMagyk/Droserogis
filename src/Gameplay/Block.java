package Gameplay;

import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

class Block extends Entity
{
    private float leftBound, rightBound, topBound, bottomBound;

    Block(World world, float xPos, float yPos, float width, float height, Orient orient)
    {
        super(world, xPos, yPos, width, height, false);

        /* Blocks can be shaped as either a rectangle or a triangle.
         * If the variable "orient" is null, it'll be a rectangle,
         * otherwise it will be a triangle depending on the value. */
        if (orient == null) polygonShape.setAsBox(width, height);
        else
        {
            Vec2 vectors[] = new Vec2[3];
            vectors[0] = new Vec2(
                    xPos + (orient.xa() * width / 2F),
                    yPos + (orient.ya() * height / 2F));
            vectors[1] = new Vec2(
                    xPos + (orient.xb() * width / 2F),
                    yPos + (orient.yb() * height / 2F));
            vectors[2] = new Vec2(
                    xPos + (orient.xc() * width / 2F),
                    yPos + (orient.yc() * height / 2F));
            polygonShape.set(vectors, 3);
            triangular = true;
        }

        body.createFixture(fixtureDef);
        updateBoundValues();
    }

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

    enum Orient
    {
        UP_LEFT
                {
                    int xa() { return 1; } int ya() { return 1; }
                    int xb() { return 1; } int yb() { return -1; }
                    int xc() { return -1; } int yc() { return 1; }
                },
        UP_RIGHT
                {
                    int xa() { return -1; } int ya() { return 1; }
                    int xb() { return -1; } int yb() { return -1; }
                    int xc() { return 1; } int yc() { return 1; }
                },
        DOWN_LEFT
                {
                    int xa() { return 1; } int ya() { return -1; }
                    int xb() { return 1; } int yb() { return 1; }
                    int xc() { return -1; } int yc() { return -1; }
                },
        DOWN_RIGHT
                {
                    int xa() { return -1; } int ya() { return -1; }
                    int xb() { return -1; } int yb() { return 1; }
                    int xc() { return 1; } int yc() { return -1; }
                };
        int xa() { return 0; } int xb() { return 0; } int xc() { return 0; }
        int ya() { return 0; } int yb() { return 0; } int yc() { return 0; }
    }

    Color getColor()
    {
        return triggered ? Color.ORANGE : Color.YELLOW;
    }

    /**
     * Every time the block is moved, this method should be called.
     * These values stay the same if the block doesn't move, so it would
     * be more efficient to not call this method if the block is not
     * moving. Should be called once at the very beginning though.
     */
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
}