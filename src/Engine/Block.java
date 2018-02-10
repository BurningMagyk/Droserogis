package Engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Block
{
    int xPos, yPos, width, height;
    Face faces[];
    /* TODO: have friction value come from the block's material */
    int friction = 10;

    Block(int xPos, int yPos, int width, int height)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;

        faces = new Face[4];
        faces[0] = new LeftFace();
        faces[1] = new RightFace();
        faces[2] = new UpFace();
        faces[3] = new DownFace();
    }

    void draw(GraphicsContext context, int x, int y)
    {
        context.setFill(Color.BLACK);
        context.fillRect(xPos, yPos, width, height);
    }

    Force collide(Actor actor)
    {
        for (Face face : faces)
        {
            return face.collide(actor);
        }
        return null;
    }

    abstract class Face
    {
        private Direction dirs[];
        Face(Direction... dirs) { this.dirs = dirs; }

        Force collide(Actor actor)
        {
            for (Direction dir : dirs)
            {
                if (dir.opposite() == actor.dir)
                    return checkBounds(actor);
            }
            return null;
        }

        abstract Force checkBounds(Actor actor);
    }

    class LeftFace extends Face
    {
        Force force = new Force(true, false,
                true, false,
                false, true);
        LeftFace()
        {
            super(Direction.LEFT);
            force.setValues(xPos, null,
                    0, null,
                    null, friction);
        }

        @Override
        Force checkBounds(Actor actor)
        {
            if (actor.xPos + actor.width == xPos + 1
                    && actor.yPos + actor.height > yPos
                    && actor.yPos < yPos + height)
                return force;
            else return null;
        }
    }

    class RightFace extends Face
    {
        Force force = new Force(false, false,
                true, false,
                false, true);
        RightFace()
        {
            super(Direction.RIGHT);
            force.setValues(xPos, null,
                    0, null,
                    null, friction);
        }

        @Override
        Force checkBounds(Actor actor)
        {
            if (actor.xPos == xPos + width - 1
                    && actor.yPos + actor.height > yPos
                    && actor.yPos < yPos + height)
                return force;
            else return null;
        }
    }

    class UpFace extends Face
    {
        Force force = new Force(false, true,
                false, true,
                true, false);
        UpFace()
        {
            super(Direction.UP);
            force.setValues(null, yPos,
                    null, 0,
                    friction, null);
        }

        @Override
        Force checkBounds(Actor actor)
        {
            if (actor.yPos + actor.height == yPos + 1
                    && actor.xPos + actor.width > xPos
                    && actor.xPos < xPos + width)
                return force;
            else return null;
        }
    }

    class DownFace extends Face
    {
        Force force = new Force(false, false,
                false, true,
                true, false);
        DownFace()
        {
            super(Direction.DOWN);
            force.setValues(null, yPos + height,
                    null, 0,
                    friction, null);
        }

        @Override
        Force checkBounds(Actor actor)
        {
            if (actor.yPos == yPos + height - 1
                    && actor.xPos + actor.width > xPos
                    && actor.xPos < xPos + width)
                return force;
            else return null;
        }
    }
}
