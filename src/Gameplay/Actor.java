package Gameplay;

import Util.Print;
import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.ArrayList;

/**
 * Players and NPCs will be "Actors" that are controlled by the user
 * or by some AI.
 */
public class Actor extends Entity
{
    /* actDirHoriz and actDirVert keep track of which directions
     * the Actor is pressing towards */
    Direction actDirHoriz = null;
    Direction actDirVert = null;
    boolean pressingLeft = false;
    boolean pressingRight = false;
    boolean pressingUp = false;
    boolean pressingDown = false;
    boolean pressingJump = false;

    /* The reason we need the variable "actDirHoriz" alongside
     * the variables "pressingLeft" and "pressingRight" is because the
     * player may be pressing the left and right keys at the same time,
     * and the variable "actDirHoriz" changes depending on which key was
     * pressed last and which one is let go first, so the direction that
     * the player moves in will be ultimately dependent on the "actDirHoriz"
     * variable. The same goes for "actDirVert" and its respective
     * boolean variables. */

    /* This variable determines what state the actor is in
     * See the enum list below to see what's available */
    State state = State.AIRBORNE;

    float airborneVel = 0;
    private boolean jumped = false;
    public boolean usingReducedGravity = false;

    private float maxSpeed = 2F;

    Actor(World world, float xPos, float yPos, float width, float height)
    {
        super(world, xPos, yPos, width, height, true);

        /* For now, all actors are rectangles */
        polygonShape.setAsBox(width, height);

        body.createFixture(fixtureDef);

        /* For testing */
        //body.getFixtureList().setFriction(0.5F);
    }

    /**
     * Called every frame to update the Actor's movement.
     */
    void act()
    {
        if (state == State.GROUNDED)
        {
            if (actDirHoriz == Direction.LEFT) body.setLinearVelocity(new Vec2(Math.min(body.getLinearVelocity().x, getNewVel(-0.2F)), body.getLinearVelocity().y));
            else if (actDirHoriz == Direction.RIGHT) body.setLinearVelocity(new Vec2(Math.max(body.getLinearVelocity().x, getNewVel(0.2F)), body.getLinearVelocity().y));
            else if (actDirVert == Direction.DOWN)
            {
                // TODO: Make the body half in height
                state = State.CROUCHING;
            }
        }
        else if (state == State.AIRBORNE)
        {
            if (actDirHoriz == Direction.LEFT) body.setLinearVelocity(new Vec2(Math.min(body.getLinearVelocity().x, getNewVel(-0.1F)), body.getLinearVelocity().y));
            else if (actDirHoriz == Direction.RIGHT) body.setLinearVelocity(new Vec2(Math.max(body.getLinearVelocity().x, getNewVel(0.1F)), body.getLinearVelocity().y));
        }
        else if (state == State.WALL_STICK_LEFT)
        {
            if (actDirHoriz == null && actDirVert == null) state = State.AIRBORNE;
            else if (!jumped) body.setLinearVelocity(new Vec2(Math.min(body.getLinearVelocity().x, -2F), body.getLinearVelocity().y));
            else jumped = false;
        }
        else if (state == State.WALL_STICK_RIGHT)
        {
            if (actDirHoriz == null && actDirVert == null) state = State.AIRBORNE;
            else if (!jumped) body.setLinearVelocity(new Vec2(Math.max(body.getLinearVelocity().x, 2F), body.getLinearVelocity().y));
            else jumped = false;
        }
        if (actDirVert != null)
        {
            /*if (actDirVert == RelPos.UP) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.min(body.getLinearVelocity().y, -2F)));
            else if (actDirHoriz == RelPos.DOWN) body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.max(body.getLinearVelocity().y, 2F)));*/
        }
    }

    void pressLeft(boolean pressed)
    {
        if (pressed)
        {
            actDirHoriz = Direction.LEFT;
            pressingLeft = true;
            if (pressingJump && state.wallRight())
            {
                pressingJump = false;
                jump(true);
            }
        }
        else if (actDirHoriz == Direction.LEFT)
        {
            if (pressingRight) actDirHoriz = Direction.RIGHT;
            else actDirHoriz = null;
            pressingLeft = false;
        }
        else pressingLeft = false;
    }
    void pressRight(boolean pressed)
    {
        if (pressed)
        {
            actDirHoriz = Direction.RIGHT;
            pressingRight = true;
            if (pressingJump && state.wallLeft())
            {
                pressingJump = false;
                jump(true);
            }
        }
        else if (actDirHoriz == Direction.RIGHT)
        {
            if (pressingLeft) actDirHoriz = Direction.LEFT;
            else actDirHoriz = null;
            pressingRight = false;
        }
        else pressingRight = false;
    }
    void pressUp(boolean pressed)
    {
        if (pressed)
        {
            actDirVert = Direction.UP;
            pressingUp = true;
            if (pressingJump && (state.wallLeft() || state.wallRight()))
            {
                pressingJump = false;
                jump(true);
            }
        }
        else if (actDirVert == Direction.UP)
        {
            if (pressingDown) actDirVert = Direction.DOWN;
            else actDirVert = null;
            pressingUp = false;
        }
        else pressingUp = false;
    }
    void pressDown(boolean pressed)
    {
        if (pressed)
        {
            actDirVert = Direction.DOWN;
            pressingDown = true;
            if (pressingJump && (state.wallLeft() || state.wallRight()))
            {
                pressingJump = false;
                jump(true);
            }
        }
        else if (actDirVert == Direction.DOWN)
        {
            if (pressingUp) actDirVert = Direction.UP;
            else actDirVert = null;
            pressingDown = false;
        }
        else pressingDown = false;
    }
    void jump(boolean pressed)
    {
        if (pressed)
        {
            if (!pressingJump)
            {
                useReducedGravity();
                airborneVel = body.getLinearVelocity().y;
                if (state == State.GROUNDED)
                {
                    body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, airborneVel - 6F));
                }
                else if (state.wallLeft())
                {
                    if (actDirVert == Direction.UP)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x + 2.5F, airborneVel - 5.5F));
                    else if (actDirHoriz == Direction.RIGHT && actDirVert == Direction.DOWN)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x + 6F, airborneVel));
                    else if (actDirHoriz == Direction.RIGHT)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x + 4F, airborneVel - 4F));
                    else if (actDirVert == Direction.DOWN)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x + 4F, airborneVel + 4F));
                }
                else if (state.wallRight())
                {
                    if (actDirVert == Direction.UP)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x - 2.5F, airborneVel - 5.5F));
                    else if (actDirHoriz == Direction.LEFT && actDirVert == Direction.DOWN)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x - 6F, airborneVel));
                    else if (actDirHoriz == Direction.LEFT)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x - 4F, airborneVel - 4F));
                    else if (actDirVert == Direction.DOWN)
                        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x - 4F, airborneVel + 4F));
                }
                else return;
                pressingJump = true;
                jumped = true;
            }
        }
        else
        {
            body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, Math.max(body.getLinearVelocity().y, airborneVel)));
            pressingJump = false;
            if (state.wallLeft() || state.wallRight())
            {
                jump(true);
                pressingJump = false;
            }
        }
    }

    private enum Direction
    {
        UP { boolean up() { return true; } boolean vertical() { return true; } },
        LEFT { boolean left() { return true; } boolean horizontal() { return true; } },
        DOWN { boolean down() { return true; } boolean vertical() { return true; } },
        RIGHT { boolean right() { return true; } boolean horizontal() { return true; } },
        UP_IN { boolean in() { return true; } boolean up() { return true; } boolean vertical() { return true; } },
        LEFT_IN { boolean in() { return true; } boolean left() { return true; } boolean horizontal() { return true; } },
        DOWN_IN { boolean in() { return true; } boolean down() { return true; } boolean vertical() { return true; } },
        RIGHT_IN{ boolean in() { return true; } boolean right() { return true; } boolean horizontal() { return true; } },
        ERROR;
        boolean in() { return false; }
        boolean up() { return false; }
        boolean left() { return false; }
        boolean down() { return false; }
        boolean right() { return false; }
        boolean vertical() { return false; }
        boolean horizontal() { return false; }
    }

    private enum State
    {
        AIRBORNE,
        GROUNDED,
        WALL_STICK_LEFT { boolean wallLeft() { return true; } boolean stick() { return true; } },
        WALL_STICK_RIGHT { boolean wallRight() { return true; } boolean stick() { return true; } },
        WALL_CLIMB_LEFT { boolean wallLeft() { return true; } State doneClimbing() { return WALL_STICK_LEFT; } boolean climb() { return true; } },
        WALL_CLIMB_RIGHT { boolean wallRight() { return true; } State doneClimbing() { return WALL_STICK_RIGHT; } boolean climb() { return true; } },
        CROUCHING,
        SLIDING;
        boolean wallLeft() { return false; }
        boolean wallRight() { return false; }
        boolean stick() { return false; }
        boolean climb() { return false; }
        State doneClimbing() { return null; }
    }

    /**
     * For debugging purposes. Will replace with sprite animations later.
     */
    @Override
    Color getColor()
    {
        if (state == State.GROUNDED) return Color.BLUE;
        if (state.climb()) return Color.CYAN;
        if (state.stick()) return Color.INDIGO;
        return Color.GREEN;
    }

    void triggerContacts(ArrayList<Entity> entities)
    {
        state = State.AIRBORNE;
        ContactEdge contactEdge = body.getContactList();
        while (contactEdge != null)
        {
            for (Entity entity : entities)
            {
                if (contactEdge.other == entity.body && contactEdge.contact.isTouching())
                {
                    entity.triggered = true;
                    triggered = true;

                    Direction horizBound = inBoundsHoriz(entity);
                    Direction vertBound = inBoundsVert(entity);

                    if (horizBound.in() && vertBound.down())
                    {
                        state = State.GROUNDED;
                        return;
                    }
                    else if (vertBound.in() && horizBound.left() && body.getLinearVelocity().x <= 0F)
                    {
                        if (body.getLinearVelocity().y >= 0) state = State.WALL_STICK_LEFT;
                        else
                        {
                            if (state != State.WALL_CLIMB_LEFT)
                            {
                                body.getFixtureList().setFriction(0F);
                                useReducedGravity();
                            }
                            state = State.WALL_CLIMB_LEFT;
                        }
                    }
                    else if (vertBound.in() && horizBound.right() && body.getLinearVelocity().x >= 0F)
                    {
                        if (body.getLinearVelocity().y >= 0) state = State.WALL_STICK_RIGHT;
                        else
                        {
                            if (state != State.WALL_CLIMB_RIGHT)
                            {
                                body.getFixtureList().setFriction(0F);
                                useReducedGravity();
                            }
                            state = State.WALL_CLIMB_RIGHT;
                        }
                    }
                    else state = State.AIRBORNE;
                }
            }
            contactEdge = contactEdge.next;
        }
    }

    private void useReducedGravity()
    {
        body.setGravityScale(0.7F);
        usingReducedGravity = true;
    }

    /**
     * Returns which direction the other entity is in relation to this Actor.
     * If their horizontal spaces overlap, one of the "IN" values will be returned.
     */
    private Direction inBoundsHoriz(Entity other)
    {
        if (getRightEdge() < other.getLeftEdge()) return Direction.RIGHT;
        if (getLeftEdge() > other.getRightEdge()) return Direction.LEFT;

        if (getRightEdge() < other.getRightEdge()) return Direction.RIGHT_IN;
        if (getLeftEdge() > other.getLeftEdge()) return Direction.LEFT_IN;

        Print.red("Error: Could not determine relative position");
        return Direction.ERROR;
    }
    /**
     * Returns which direction the other entity is in relation to this Actor.
     * If their vertical spaces overlap, one of the "IN" values will be returned.
     */
    private Direction inBoundsVert(Entity other)
    {
        if (getBottomEdge() < other.getTopEdge()) return Direction.DOWN;
        if (getTopEdge() > other.getBottomEdge()) return Direction.UP;

        if (getBottomEdge() < other.getBottomEdge()) return Direction.DOWN_IN;
        if (getTopEdge() > other.getTopEdge()) return Direction.UP_IN;

        Print.red("Error: Could not determine relative position");
        return Direction.ERROR;
    }

    private float getNewVel(float acceleration)
    {
        float newVel;
        if (body.getLinearVelocity().x <= 0 && acceleration < 0)
        {
            return -maxSpeed;
        }
        if (body.getLinearVelocity().x >= 0 && acceleration > 0)
        {
            return maxSpeed;
        }

        newVel = body.getLinearVelocity().x + acceleration;
        if (acceleration < 0 && newVel < -maxSpeed) newVel = -maxSpeed;
        else if (newVel > maxSpeed) newVel = maxSpeed;
        return newVel;
    }

    /**
     * Called every frame so that the flags can be properly set afterwards.
     */
    @Override
    void resetFlags()
    {
        super.resetFlags();

        if (usingReducedGravity && body.getLinearVelocity().y > 0)
        {
            body.setGravityScale(1F);
            usingReducedGravity = false;
            body.getFixtureList().setFriction(1F);

            State stickState = state.doneClimbing();
            if (stickState != null)
            {
                state = stickState;
            }
        }
    }
}
