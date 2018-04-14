package Gameplay;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

class Block implements Entity
{
    private BodyDef bodyDef = new BodyDef();
    private PolygonShape polygonShape = new PolygonShape();
    private Body body;
    private FixtureDef fixtureDef = new FixtureDef();

    private float width, height;

    Block(World world, float xPos, float yPos, float width, float height)
    {
        this.width = width; this.height = height;

        bodyDef.position.set(xPos, yPos);
        bodyDef.type = BodyType.DYNAMIC;
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(width, height);
        fixtureDef.density = 1;
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);
    }

    @Override
    public Vec2 getPosition()
    {
        return body.getPosition();
    }

    @Override
    public float getWidth() { return width; }
    @Override
    public float getHeight() { return height; }

    public void test(){Print.blue(body.getPosition().x);}

    enum Incline
    {
        LEVEL,
        STEEP,
        GRADUAL
    }

    class Face
    {
        Direction direction;
        Incline incline;

        Integer _xPos, _yPos;

        Face(Direction direction, Incline incline)
        {
            this.direction = direction;
            this.incline = incline;

            if (incline == Incline.LEVEL)
            {
                if (direction.vert == Direction.Enum.UP)
                {
                    _xPos = null;
                    //_yPos = yPos;
                }
                else if (direction.vert == Direction.Enum.DOWN)
                {
                    _xPos = null;
                    //_yPos = yPos + height;
                }
                else if (direction.horiz == Direction.Enum.LEFT)
                {
                    //_xPos = xPos;
                    _yPos = null;
                }
                else if (direction.horiz == Direction.Enum.RIGHT)
                {
                    //_xPos = xPos + width;
                    _yPos = null;
                }
                else Print.red("Error: Bad LEVEL stats");
            }
            else
            {
                _xPos = null;
                _yPos = null;
            }
        }

        /* TODO: Assumes LEVEL incline for now */
        boolean checkCollision(int x1, int x2, int y1, int y2)
        {
            /*if (direction.horiz == Direction.Enum.LEFT)
            {
                if (x2 != xPos + 1) return false;
                if (y2 <= yPos) return false;
                if (y1 >= yPos + height) return false;
            }
            else if (direction.horiz == Direction.Enum.RIGHT)
            {
                if (x1 != xPos + width - 1) return false;
                if (y2 <= yPos) return false;
                if (y1 >= yPos + height) return false;
            }
            else if (direction.vert == Direction.Enum.UP)
            {
                if (y2 != yPos + 1) return false;
                if (x2 <= xPos) return false;
                if (x1 >= xPos + width) return false;
            }
            else if (direction.vert == Direction.Enum.DOWN)
            {
                if (y1 != yPos + height - 1) return false;
                if (x2 <= xPos) return false;
                if (x1 >= xPos + width) return false;
            }*/

            return true;
        }
    }
}
