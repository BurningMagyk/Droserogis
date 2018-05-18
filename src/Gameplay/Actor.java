package Gameplay;

import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.ArrayList;

public class Actor extends Entity
{
    Dir currentDir = null;
    boolean pressingLeft = false;
    boolean pressingRight = false;
    boolean pressingUp = false;
    boolean pressingDown = false;

    Actor(World world, float xPos, float yPos, float width, float height)
    {
        super(world, xPos, yPos, width, height, true);
    }

    @Override
    void act()
    {
        if (currentDir != null)
        {
            if (currentDir == Dir.UP) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -2F));
            else if (currentDir == Dir.LEFT) body.setLinearVelocity(new Vec2(-2F, body.getLinearVelocity().y));
            else if (currentDir == Dir.DOWN) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, 2F));
            else if (currentDir == Dir.RIGHT) body.setLinearVelocity(new Vec2(2F, body.getLinearVelocity().y));
        }

    }

    void moveLeft(boolean pressed)
    {
        if (pressed)
        {
            currentDir = Dir.LEFT;
            pressingLeft = true;
        }
        else if (currentDir == Dir.LEFT)
        {
            if (pressingRight) currentDir = Dir.RIGHT;
            else currentDir = null;
            pressingLeft = false;
        }
        else pressingLeft = false;
    }
    void moveRight(boolean pressed)
    {
        if (pressed)
        {
            currentDir = Dir.RIGHT;
            pressingRight = true;
        }
        else if (currentDir == Dir.RIGHT)
        {
            if (pressingLeft) currentDir = Dir.LEFT;
            else currentDir = null;
            pressingRight = false;
        }
        else pressingRight = false;
    }
    void moveUp(boolean pressed)
    {
        if (pressed)
        {
            currentDir = Dir.UP;
            pressingUp = true;
        }
        else if (currentDir == Dir.UP)
        {
            if (pressingDown) currentDir = Dir.DOWN;
            else currentDir = null;
            pressingUp = false;
        }
        else pressingUp = false;
    }
    void moveDown(boolean pressed)
    {
        if (pressed)
        {
            currentDir = Dir.DOWN;
            pressingDown = true;
        }
        else if (currentDir == Dir.DOWN)
        {
            if (pressingUp) currentDir = Dir.UP;
            else currentDir = null;
            pressingDown = false;
        }
        else pressingDown = false;
    }

    private enum Dir{UP, LEFT, DOWN, RIGHT}

    Color getColor()
    {
        return triggered ? Color.BLUE : Color.GREEN;
    }

    @Override
    void triggerContacts(ArrayList<Entity> entities)
    {
        ContactEdge contactEdge = body.getContactList();
        while (contactEdge != null)
        {
            for (Entity entity : entities)
            {
                if (contactEdge.other == entity.body && contactEdge.contact.isTouching())
                {
                    entity.triggered = true;
                    triggered = true;
                }
            }
            contactEdge = contactEdge.next;
        }
    }
}
