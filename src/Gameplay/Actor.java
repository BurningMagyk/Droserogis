package Gameplay;

import Util.Print;
import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.ArrayList;

public class Actor extends Entity
{
    RelPos actDirHoriz = null;
    RelPos actDirVert = null;
    boolean pressingLeft = false;
    boolean pressingRight = false;
    boolean pressingUp = false;
    boolean pressingDown = false;

    boolean pressingJump = false;
    State state = State.AIRBORNE;

    float airborneVel = 0;

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
            if (actDirHoriz == RelPos.LEFT) body.setLinearVelocity(new Vec2(Math.min(body.getLinearVelocity().x, -2F), body.getLinearVelocity().y));
            else if (actDirHoriz == RelPos.RIGHT) body.setLinearVelocity(new Vec2(Math.max(body.getLinearVelocity().x, 2F), body.getLinearVelocity().y));
        }
        if (actDirVert != null)
        {
            if (actDirVert == RelPos.UP) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.min(body.getLinearVelocity().y, -2F)));
            else if (actDirHoriz == RelPos.DOWN) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.max(body.getLinearVelocity().y, 2F)));
        }
    }

    void moveLeft(boolean pressed)
    {
        if (pressed)
        {
            actDirHoriz = RelPos.LEFT;
            pressingLeft = true;
        }
        else if (actDirHoriz == RelPos.LEFT)
        {
            if (pressingRight) actDirHoriz = RelPos.RIGHT;
            else actDirHoriz = null;
            pressingLeft = false;
        }
        else pressingLeft = false;
    }
    void moveRight(boolean pressed)
    {
        if (pressed)
        {
            actDirHoriz = RelPos.RIGHT;
            pressingRight = true;
        }
        else if (actDirHoriz == RelPos.RIGHT)
        {
            if (pressingLeft) actDirHoriz = RelPos.LEFT;
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
            if (!pressingJump)
            {
                airborneVel = body.getLinearVelocity().y;
                if (state == State.GROUNDED)
                {
                    body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, airborneVel - 6F));
                    pressingJump = true;
                }
                else if (state == State.WALL_STICK_RIGHT)
                {
                    /* TODO: Make it jump at an angle depending on what arrow key is held down */
                    pressingJump = true;
                }

            }
        }
        else
        {
            body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.max(body.getLinearVelocity().y, airborneVel)));
            pressingJump = false;
        }
    }

    private enum RelPos{
        UP { boolean up() { return true; } },
        LEFT { boolean left() { return true; } },
        DOWN { boolean down() { return true; } },
        RIGHT { boolean right() { return true; } },
        UP_IN { boolean in() { return true; } boolean up() { return true; } },
        LEFT_IN { boolean in() { return true; } boolean left() { return true; } },
        DOWN_IN { boolean in() { return true; } boolean down() { return true; } },
        RIGHT_IN{ boolean in() { return true; } boolean right() { return true; } },
        ERROR;
        boolean in() { return false; }
        boolean up() { return false; }
        boolean left() { return false; }
        boolean down() { return false; }
        boolean right() { return false; }
    }

    private enum State{ AIRBORNE, GROUNDED, WALL_STICK_LEFT, WALL_STICK_RIGHT, WALL_CLIMB_LEFT, WALL_CLIMB_RIGHT }

    @Override
    Color getColor()
    {
        if (state == State.GROUNDED) return Color.BLUE;
        if (state == State.WALL_STICK_LEFT) return Color.CYAN;
        if (state == State.WALL_STICK_RIGHT) return Color.INDIGO;
        return Color.GREEN;
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

                    RelPos horizBound = inBoundsHoriz(entity);
                    RelPos vertBound = inBoundsVert(entity);

                    if (horizBound.in() && vertBound.down())
                        state = State.GROUNDED;
                    else if (vertBound.in() && horizBound.left())
                        state = State.WALL_STICK_LEFT;
                    else if (vertBound.in() && horizBound.right())
                        state = State.WALL_STICK_RIGHT;
                }
            }
            contactEdge = contactEdge.next;
        }
    }

    private RelPos inBoundsHoriz(Entity other)
    {
        if (getRightEdge() < other.getLeftEdge()) return RelPos.RIGHT;
        if (getLeftEdge() > other.getRightEdge()) return RelPos.LEFT;

        if (getRightEdge() < other.getRightEdge()) return RelPos.RIGHT_IN;
        if (getLeftEdge() > other.getLeftEdge()) return RelPos.LEFT_IN;

        Print.red("Error: Could not determine relative position");
        return RelPos.ERROR;
    }

    private RelPos inBoundsVert(Entity other)
    {
        if (getBottomEdge() < other.getTopEdge()) return RelPos.DOWN;
        if (getTopEdge() > other.getBottomEdge()) return RelPos.UP;

        if (getBottomEdge() < other.getBottomEdge()) return RelPos.DOWN_IN;
        if (getTopEdge() > other.getTopEdge()) return RelPos.UP_IN;

        Print.red("Error: Could not determine relative position");
        return RelPos.ERROR;
    }

    @Override
    void resetFlags()
    {
        super.resetFlags();
        state = State.AIRBORNE;
    }
}
