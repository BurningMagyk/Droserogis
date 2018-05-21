package Gameplay;

import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

class Block extends Entity
{
    Block(World world, float xPos, float yPos, float width, float height, Orient orient)
    {
        super(world, xPos, yPos, width, height, false);

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
}