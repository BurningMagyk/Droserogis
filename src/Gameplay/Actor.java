package Gameplay;

import Util.Print;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.ArrayList;

/**
 * Created by Joel on 6/14/2018.
 */
public class Actor extends Entity
{
    private Direction dirPrimary = null;
    private Direction dirSecondary = null;
    private Direction dirVertical = null;

    private State state = State.SWIM;
    private Jump jump = new Jump(false);

    /* Stats set up by the Character object */
    private float maxRunSpeed = 3F;
    private float avgRunSpeed = 2F;
    private float groundDecel = 0.2F;
    private float maxAirSpeed = 10F;
    private float avgAirSpeed = 1F;
    private float aerialDecel = 0.1F;
    private float jumpSpeed = 6F;
    private float maxStandSpeed = 0.5F;

    private float jumpGravityReduced = 0.7F;
    // TODO: The variable 'grade' needs to be reset to zero whenever not grounded on sloped surface
    private float grade = 0F;

    void debug()
    {
        Print.blue("dirPrimary: " + dirPrimary + ", dirVertical: " + dirVertical);
    }

    Actor(World world, float xPos, float yPos, float width, float height)
    {
        super(world, xPos, yPos, width, height, true);

        /* For now, all actors are rectangles */
        polygonShape.setAsBox(width, height);

        body.createFixture(fixtureDef);
    }

    /**
     * Called every frame to update the Actor's movement.
     */
    void act(ArrayList<Entity> entities)
    {
        triggerContacts(entities);

        if (state.isGrounded())
        {
            if (dirPrimary == Direction.LEFT)
                move(maxRunSpeed, avgRunSpeed, groundDecel,true);
            else if (dirPrimary == Direction.RIGHT)
                move(maxRunSpeed, avgRunSpeed, groundDecel,false);
        }
        else if (state.isAirborne())
        {
            if (dirPrimary == Direction.LEFT)
                move(maxAirSpeed, avgAirSpeed, aerialDecel,true);
            else if (dirPrimary == Direction.RIGHT)
                move(maxAirSpeed, avgAirSpeed, aerialDecel,false);
        }
        else if (state == State.SWIM)
        {
            if (dirPrimary == Direction.LEFT)
                move(maxAirSpeed, avgAirSpeed, aerialDecel,true);
            else if (dirPrimary == Direction.RIGHT)
                move(maxAirSpeed, avgAirSpeed, aerialDecel,false);
            grade = 1;
            if (dirVertical == Direction.UP)
                move(maxAirSpeed, avgAirSpeed, aerialDecel,true);
            else if (dirVertical == Direction.DOWN)
                move(maxAirSpeed, avgAirSpeed, -aerialDecel,false);
            grade = 0;
        }
        else if (state.isOnWall())
        {

        }
        /*else if (state == State.WALL_STICK_LEFT)
        {
            if (actDirHoriz == null && actDirVert == null) changeState(body.getLinearVelocity().y > 0 ? State.FALLING : State.RISING);
            else if (!jumped) body.setLinearVelocity(new Vec2(Math.min(body.getLinearVelocity().x, -2F), body.getLinearVelocity().y));
            else jumped = false;
        }
        else if (state == State.WALL_STICK_RIGHT)
        {
            if (actDirHoriz == null && actDirVert == null) changeState(body.getLinearVelocity().y > 0 ? State.FALLING : State.RISING);
            else if (!jumped) body.setLinearVelocity(new Vec2(Math.max(body.getLinearVelocity().x, 2F), body.getLinearVelocity().y));
            else jumped = false;
        }*/
    }

    private void move(float maxSpeed, float avgSpeed, float deceleration, boolean left)
    {
        body.setLinearVelocity(new Vec2(
                getNewVel(maxSpeed, avgSpeed, left ? -deceleration : deceleration, true),
                getNewVel(maxSpeed, avgSpeed, -deceleration, false)));
    }

    private float getNewVel(float maxSpeed, float avgSpeed, float deceleration, boolean horizontal)
    {
        float newVel = grade * avgSpeed;
        float oldVel;

        if (horizontal)
        {
            oldVel = body.getLinearVelocity().x;
            if (grade == 1) return oldVel;
            newVel = avgSpeed - newVel;
            if (oldVel <= 0 && deceleration < 0)
                return Math.min(-newVel, oldVel);
            if (oldVel >= 0 && deceleration > 0)
                return Math.max(newVel, oldVel);
        }
        else
        {
            oldVel = body.getLinearVelocity().y;
            if (grade == 0) return oldVel;
            newVel *= (-1);
            if (oldVel <= 0 && deceleration < 0)
                return Math.min(newVel, oldVel);
            if (oldVel >= 0 && deceleration > 0)
                return Math.max(-newVel, oldVel);
        }

        newVel = oldVel + deceleration;

        if (deceleration > 0 && newVel > maxSpeed) newVel = maxSpeed;
        else if (deceleration < 0 && newVel < -maxSpeed) newVel = -maxSpeed;

        return newVel;
    }

    private Vec2 getNewVel()
    {
        Vec2 oldVel = body.getLinearVelocity();
        float xVel = oldVel.x;
        float yVel = oldVel.y - jumpSpeed;
        return new Vec2(xVel, yVel);
    }

    private boolean pressingLeft = false;
    private boolean pressingRight = false;
    private boolean pressingUp = false;
    private boolean pressingDown = false;
    private boolean pressingJump = false;
    void pressLeft(boolean pressed) {
        if (pressed) {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirSecondary = Direction.LEFT;
            /* If you're not on a wall, it changes your primary direction */
            else dirPrimary = Direction.LEFT; }
        /* If you release the key when already moving left without a wall */
        else if (!state.isOnWall() && dirPrimary == Direction.LEFT) {
            if (pressingRight) dirPrimary = Direction.RIGHT;
            else dirPrimary = null; }
        /* If you release the key when already moving left with a wall */
        else if (state.isOnWall() && dirSecondary == Direction.LEFT) {
            if (pressingRight) dirSecondary = Direction.RIGHT;
            else dirSecondary = null; }
        pressingLeft = pressed; }
    void pressRight(boolean pressed) {
        if (pressed) {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirSecondary = Direction.RIGHT;
            /* If you're not on a wall, it changes your primary direction */
            else dirPrimary = Direction.RIGHT; }
        /* If you release the key when already moving right without a wall */
        else if (!state.isOnWall() && dirPrimary == Direction.RIGHT) {
            if (pressingLeft) dirPrimary = Direction.LEFT;
            else dirPrimary = null; }
        /* If you release the key when already moving left with a wall */
        else if (state.isOnWall() && dirSecondary == Direction.RIGHT) {
            if (pressingRight) dirSecondary = Direction.LEFT;
            else dirSecondary = null; }
        pressingRight = pressed; }
    void pressUp(boolean pressed) {
        if (pressed) dirVertical = Direction.UP;
        else if (dirVertical == Direction.UP) {
            if (pressingDown) dirVertical = Direction.DOWN;
            else dirVertical = null; }
        pressingUp = pressed; }
    void pressDown(boolean pressed) {
        if (pressed) dirVertical = Direction.DOWN;
        else if (dirVertical == Direction.DOWN) {
            if (pressingUp) dirVertical = Direction.UP;
            else dirVertical = null; }
        pressingDown = pressed; }
    void pressJump(boolean pressed) {
        if (pressingJump == pressed) return;
        jump = jump.trigger(pressed);
        pressingJump = pressed; }

    private class Jump
    {
        // TODO: Make the armed variable have a short time limit
        private final boolean armed;
        //private boolean started = false;
        Jump(boolean ready)
        {
            armed = ready;
        }
        Jump()
        {
            armed = false;
            //started = true;
            body.setLinearVelocity(getNewVel());
        }
        /* Called whenever the jump key is pressed or released */
        Jump trigger(boolean pressed)
        {
            /* If pressed in the air, the Jump becomes armed.
             * If released in the air, the Jump becomes unarmed
             * and unstarted. */
            if (state.isAirborne()) return new Jump(pressed);
            /* If pressed on the ground, the Jump starts.
             * If released on the ground, the Jump becomes unarmed
             * and unstarted. */
            return pressed ? new Jump() : new Jump(false);
        }
        /* Called whenever the player hits a surface */
        Jump trigger()
        {
            /* If armed when hitting a surface, the Jump starts. */
            if (armed) return new Jump();
            /* If unarmed when hitting a surface, the Jump becomes
             * unarmed and unstarted. */
            return new Jump(false);
        }
    }

    private enum Direction {
        UP { boolean vertical() { return true; } },
        LEFT { boolean horizontal() { return true; } },
        DOWN { boolean vertical() { return true; } },
        RIGHT { boolean horizontal() { return true; } };
        boolean vertical() { return false; }
        boolean horizontal() { return false; } }

    /**
     * Returns which direction the other entity is in relation to this Actor.
     */
    private Direction inBoundsHoriz(Entity other, boolean checkWithin)
    {
        if (!checkWithin)
        {
            if (getRightEdge() < other.getLeftEdge()) return Direction.RIGHT;
            if (getLeftEdge() > other.getRightEdge()) return Direction.LEFT;
            return null;
        }

        if (getRightEdge() < other.getRightEdge()) return Direction.RIGHT;
        if (getLeftEdge() > other.getLeftEdge()) return Direction.LEFT;

        Print.red("Error: Could not determine relative position");
        return null;
    }
    /**
     * Returns which direction the other entity is in relation to this Actor.
     */
    private Direction inBoundsVert(Entity other, boolean checkWithin)
    {
        if (!checkWithin)
        {
            if (getBottomEdge() < other.getTopEdge()) return Direction.DOWN;
            if (getTopEdge() > other.getBottomEdge()) return Direction.UP;
            return null;
        }

        if (getBottomEdge() < other.getBottomEdge()) return Direction.DOWN;
        if (getTopEdge() > other.getTopEdge()) return Direction.UP;

        Print.red("Error: Could not determine relative position");
        return null;
    }

    private enum State {
        PRONE { boolean isGrounded() { return true; } },
        RISE { boolean isAirborne() { return true; } },
        FALL { boolean isAirborne() { return true; } },
        STAND { boolean isGrounded() { return true; } },
        RUN { boolean isGrounded() { return true; } },
        WALL_STICK { boolean isOnWall() { return true; } },
        WALL_CLIMB { boolean isOnWall() { return true; } },
        CROUCH { boolean isGrounded() { return true; } },
        SLIDE { boolean isGrounded() { return true; } },
        SWIM;
        boolean isOnWall() { return false; }
        boolean isAirborne() { return false; }
        boolean isGrounded() { return false; } }

    void setState(State state)
    {
        if (this.state == state) return;

        /* Temporary */
        Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");

        this.state = state;
    }

    void triggerContacts(ArrayList<Entity> entities)
    {
        boolean withinBlockHoriz = false;
        boolean withinBlockVert = false;
        int groundsCounted = 0;
        int wallsCounted = 0;
        ContactEdge contactEdge = body.getContactList();
        while (contactEdge != null)
        {
            for (Entity entity : entities)
            {
                if (contactEdge.other == entity.body && contactEdge.contact.isTouching())
                {
                    /* Where the player affects other blocks upon touch */
                    entity.triggered = true;
                    triggered = true;

                    Direction horizBound = inBoundsHoriz(entity, false);
                    Direction vertBound = inBoundsVert(entity, false);
                    if (horizBound == null)
                    {
                        horizBound = inBoundsHoriz(entity, true);
                        withinBlockHoriz = true;
                    }
                    if (vertBound == null)
                    {
                        vertBound = inBoundsVert(entity, true);
                        withinBlockVert = true;
                    }

                    if (withinBlockHoriz && vertBound == Direction.DOWN) groundsCounted++;
                    else if (withinBlockVert) wallsCounted++;
                }
            }
            contactEdge = contactEdge.next;
        }

        /* Setting the state */
        float xVelocity = body.getLinearVelocity().x;
        float yVelocity = body.getLinearVelocity().y;
        if (inWater()) setState(State.SWIM);
        else if (groundsCounted > 0)
        {
            if (dirVertical == Direction.DOWN)
            {
                if (Math.abs(xVelocity) <= maxStandSpeed) setState(State.CROUCH);
                else setState(State.SLIDE);
            }
            else
            {
                if (Math.abs(xVelocity) <= maxStandSpeed) setState(State.STAND);
                else setState(State.RUN);
            }
        }
        else if (yVelocity >= 0)
        {
            if (wallsCounted > 0) setState(State.WALL_STICK);
            else setState(State.FALL);
        }
        else
        {
            if (wallsCounted > 0) setState(State.WALL_CLIMB);
            else setState(State.RISE);
        }
    }

    boolean inWater()
    {
        /* Temporary */
        return false;
    }
}
