package Game;

import Util.Print;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Start with just using rectangular platforms
 */
public class Block
{
    GraphicsContext context;
    int xPos, yPos, width, height;
    Face levelFaces[];
    Face slant;

    Block(GraphicsContext context, int xPos, int yPos, int width, int height)
    {
        this.context = context;
        this.xPos = xPos; this.yPos = yPos;
        this.width = width; this.height = height;

        /* Right now, the constructor only makes rectangles */
        /* TODO: If the face is the slant, do not include it in the levelFaces array*/
        levelFaces = new Face[]{
                new Face(new Direction(Direction.Enum.UP), Incline.LEVEL),
                new Face(new Direction(Direction.Enum.LEFT), Incline.LEVEL),
                new Face(new Direction(Direction.Enum.DOWN), Incline.LEVEL),
                new Face(new Direction(Direction.Enum.RIGHT), Incline.LEVEL)};
        slant = null;

        /* TODO: When making a triangle, the slanted face is assigned to 'slant' */
    }

    public void act()
    {
    }

    public void draw(int x, int y)
    {
        context.setFill(Color.BLACK);
        context.fillRect(xPos, yPos, width, height);
    }

    /**
     *
     * @param direction - Where the creature is moving
     * @return - The face that the creature will collide with
     */
    public Face checkCollision(int x1, int x2, int y1, int y2, Direction direction)
    {
        /* Only check faces that oppose the creature's direction */
        /* Start with the level faces */
        for (Face face : levelFaces)
        {
            if (direction.opposes(face.direction))
            {
                if (face.checkCollision(x1, x2, y1, y2)) return face;
            }
        }

        /* Check the slant face */
        if (slant != null)
        {
            /* TODO: Here is where collision will be checked for the slant */
        }

        /* Returns null if there is no collision */
        return null;
    }

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
                    _yPos = yPos;
                }
                else if (direction.vert == Direction.Enum.DOWN)
                {
                    _xPos = null;
                    _yPos = yPos + height;
                }
                else if (direction.horiz == Direction.Enum.LEFT)
                {
                    _xPos = xPos;
                    _yPos = null;
                }
                else if (direction.horiz == Direction.Enum.RIGHT)
                {
                    _xPos = xPos + width;
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
            if (direction.horiz == Direction.Enum.LEFT)
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
            }

            return true;
        }

        int getX(int cretPosY)
        {
            /* TODO: Have it return a different value if incline is not LEVEL */
            return _xPos;
        }

        int getY(int cretPosX)
        {
            /* TODO: Have it return a different value if incline is not LEVEL */
            return _yPos;
        }
    }
}
