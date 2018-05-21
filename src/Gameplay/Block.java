package Gameplay;

import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

class Block extends Entity
{
    Block(World world, float xPos, float yPos, float width, float height, TriangleOrient triOri)
    {
        super(world, xPos, yPos, width, height, false);

        if (triOri == null) polygonShape.setAsBox(width, height);
        else if (triOri == TriangleOrient.UP_RIGHT)
        {
            Vec2 vectors[] = new Vec2[3];
            vectors[0] = new Vec2(xPos - (width / 2F), yPos + (height / 2F));
            vectors[1] = new Vec2(xPos - (width / 2F), yPos - (height / 2F));
            vectors[2] = new Vec2(xPos + (width / 2F), yPos + (height / 2F));
            polygonShape.set(vectors, 3);
            triangular = true;
        }
        else if (triOri == TriangleOrient.UP_LEFT)
        {
            Vec2 vectors[] = new Vec2[3];
            vectors[0] = new Vec2(xPos + (width / 2F), yPos + (height / 2F));
            vectors[1] = new Vec2(xPos + (width / 2F), yPos - (height / 2F));
            vectors[2] = new Vec2(xPos - (width / 2F), yPos + (height / 2F));
            polygonShape.set(vectors, 3);
            triangular = true;
        }
        else if (triOri == TriangleOrient.DOWN_RIGHT)
        {
            Vec2 vectors[] = new Vec2[3];
            vectors[0] = new Vec2(xPos - (width / 2F), yPos - (height / 2F));
            vectors[1] = new Vec2(xPos - (width / 2F), yPos + (height / 2F));
            vectors[2] = new Vec2(xPos + (width / 2F), yPos - (height / 2F));
            polygonShape.set(vectors, 3);
            triangular = true;
        }
        else if (triOri == TriangleOrient.DOWN_LEFT)
        {
            Vec2 vectors[] = new Vec2[3];
            vectors[0] = new Vec2(xPos + (width / 2F), yPos - (height / 2F));
            vectors[1] = new Vec2(xPos + (width / 2F), yPos + (height / 2F));
            vectors[2] = new Vec2(xPos - (width / 2F), yPos - (height / 2F));
            polygonShape.set(vectors, 3);
            triangular = true;
        }

        body.createFixture(fixtureDef);
    }

    Color getColor()
    {
        return triggered ? Color.ORANGE : Color.YELLOW;
    }
}

enum TriangleOrient
{
    UP_LEFT
            {
                int xa() { return 1; } int ya() { return 1; }
                int xb() { return 1; } int yb() { return -1; }
                int xc() { return -1; } int yc() { return 1; }
            },
    UP_RIGHT
            {},
    DOWN_LEFT
            {},
    DOWN_RIGHT
            {}
}