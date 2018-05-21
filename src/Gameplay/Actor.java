package Gameplay;

import Util.Print;
import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.ArrayList;

public class Actor extends Entity
{
    Dir actDirHoriz = null;
    Dir actDirVert = null;
    boolean pressingLeft = false;
    boolean pressingRight = false;
    boolean pressingUp = false;
    boolean pressingDown = false;

    boolean pressingJump = false;
    boolean grounded = false;

    Actor(World world, float xPos, float yPos, float width, float height)
    {
        super(world, xPos, yPos, width, height, true);

        polygonShape.setAsBox(width, height);

        body.createFixture(fixtureDef);
    }

    void act()
    {
        if (actDirHoriz != null)
        {
            if (actDirHoriz == Dir.LEFT) body.setLinearVelocity(new Vec2(-2F, body.getLinearVelocity().y));
            else if (actDirHoriz == Dir.RIGHT) body.setLinearVelocity(new Vec2(2F, body.getLinearVelocity().y));
        }
        if (actDirVert != null)
        {
            if (actDirVert == Dir.UP) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -2F));
            else if (actDirHoriz == Dir.DOWN) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, 2F));
        }
    }

    void moveLeft(boolean pressed)
    {
        if (pressed)
        {
            actDirHoriz = Dir.LEFT;
            pressingLeft = true;
        }
        else if (actDirHoriz == Dir.LEFT)
        {
            if (pressingRight) actDirHoriz = Dir.RIGHT;
            else actDirHoriz = null;
            pressingLeft = false;
        }
        else pressingLeft = false;
    }
    void moveRight(boolean pressed)
    {
        if (pressed)
        {
            actDirHoriz = Dir.RIGHT;
            pressingRight = true;
        }
        else if (actDirHoriz == Dir.RIGHT)
        {
            if (pressingLeft) actDirHoriz = Dir.LEFT;
            else actDirHoriz = null;
            pressingRight = false;
        }
        else pressingRight = false;
    }
    /*void moveUp(boolean pressed)
    {
        if (pressed)
        {
            actDirVert = Dir.UP;
            pressingUp = true;
        }
        else if (actDirVert == Dir.UP)
        {
            if (pressingDown) actDirVert = Dir.DOWN;
            else actDirVert = null;
            pressingUp = false;
        }
        else pressingUp = false;
    }
    void moveDown(boolean pressed)
    {
        if (pressed)
        {
            actDirVert = Dir.DOWN;
            pressingDown = true;
        }
        else if (actDirVert == Dir.DOWN)
        {
            if (pressingUp) actDirVert = Dir.UP;
            else actDirVert = null;
            pressingDown = false;
        }
        else pressingDown = false;
    }*/
    void jump(boolean pressed)
    {
        if (pressed)
        {
            if (pressingJump || !grounded) return;
            body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, body.getLinearVelocity().y - 6F));
            pressingJump = true;
        }
        else
        {
            body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.max(body.getLinearVelocity().y, 0)));
            pressingJump = false;
        }
    }

    private enum Dir{UP, LEFT, DOWN, RIGHT}

    @Override
    Color getColor()
    {
        return grounded ? Color.BLUE : Color.GREEN;
    }

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

                    if (inBoundsHoriz(entity) == null && inBoundsVert(entity) == Dir.DOWN)
                        grounded = true;
                }
            }
            contactEdge = contactEdge.next;
        }
    }

    private Dir inBoundsHoriz(Entity other)
    {
        if (getRightEdge() < other.getLeftEdge()) return Dir.RIGHT;
        if (getLeftEdge() > other.getRightEdge()) return Dir.LEFT;
        return null;
    }

    private Dir inBoundsVert(Entity other)
    {
        if (getBottomEdge() < other.getTopEdge()) return Dir.DOWN;
        if (getTopEdge() > other.getBottomEdge()) return Dir.UP;
        return null;
    }

    @Override
    void resetFlags()
    {
        super.resetFlags();
        grounded = false;
    }
}
