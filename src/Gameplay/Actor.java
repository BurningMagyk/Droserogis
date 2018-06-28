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

    private Direction wallStickPos = null;

    private State state = State.SWIM;
    private Jump jump = new Jump(false, null);

    /* Stats set up by the Character object */
    private float maxRunSpeed = 3F;
    private float avgRunSpeed = 2F;
    private float groundDecel = 0.2F;
    private float maxAirSpeed = 10F;
    private float avgAirSpeed = 1F;
    private float aerialDecel = 0.1F;
    private float jumpSpeed = 6F;
    private float maxStandSpeed = 0.5F;
    private float wallStickSpeed = 1F;

    private float jumpGravityReduced = 0.7F;
    private float climbGravityReduced = 0.3F;
    private float frictionDefault = 0.7F;
    private float frictionReduced = 0.2F;
    private float frictionAmplified = 1.5F;
    private float steppedGrade = 0F;

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

        if (jump.execute()) return;
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
            steppedGrade = 1;
            if (dirVertical == Direction.UP)
                move(maxAirSpeed, avgAirSpeed, aerialDecel,true);
            else if (dirVertical == Direction.DOWN)
                move(maxAirSpeed, avgAirSpeed, -aerialDecel,false);
            steppedGrade = 0;
        }
        else if (state.isOnWall())
        {
            /* If climbing a wall and moving against it, don't nerf the player's jump height */
            if (state == State.WALL_CLIMB && wallStickPos == dirPrimary) return;
            else if (dirPrimary != null || dirVertical != null)
            {
                if (wallStickPos == null) Print.red("Error: wallStickPos is null when the state isOnWall()");
                else move(wallStickSpeed, wallStickSpeed, wallStickSpeed, wallStickPos == Direction.LEFT);
            }
            if (wallStickPos != dirPrimary) jump = jump.trigger();
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
        float newVel = steppedGrade * avgSpeed;
        float oldVel;

        if (horizontal)
        {
            oldVel = body.getLinearVelocity().x;
            if (steppedGrade == 1) return oldVel;
            newVel = avgSpeed - newVel;
            if (oldVel <= 0 && deceleration < 0)
                return Math.min(-newVel, oldVel);
            if (oldVel >= 0 && deceleration > 0)
                return Math.max(newVel, oldVel);
        }
        else
        {
            oldVel = body.getLinearVelocity().y;
            if (steppedGrade == 0) return oldVel;
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

    private boolean pressingLeft = false;
    private boolean pressingRight = false;
    private boolean pressingUp = false;
    private boolean pressingDown = false;
    private boolean pressingJump = false;
    void pressLeft(boolean pressed) {
        if (pressed) {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirSecondary = Direction.LEFT;
            /* It changes your primary direction regardless */
            dirPrimary = Direction.LEFT; }
        /* If you release the key when already moving left */
        else if (dirPrimary == Direction.LEFT) {
            if (pressingRight) dirPrimary = Direction.RIGHT;
            else dirPrimary = null;
            /* If you release the key when already moving left with a wall */
            if (state.isOnWall()) {
                if (pressingRight) dirSecondary = Direction.RIGHT;
                else dirSecondary = null; } }
        pressingLeft = pressed; }
    void pressRight(boolean pressed) {
        if (pressed) {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirSecondary = Direction.RIGHT;
            /* It changes your primary direction regardless */
            dirPrimary = Direction.RIGHT; }
        /* If you release the key when already moving right */
        else if (dirPrimary == Direction.RIGHT) {
            if (pressingLeft) dirPrimary = Direction.LEFT;
            else dirPrimary = null;
            /* If you release the key when already moving right with a wall */
            if (state.isOnWall()) {
                if (pressingLeft) dirSecondary = Direction.LEFT;
                else dirSecondary = null; } }
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
        if (pressingJump == pressed) { return; }
        jump = jump.trigger(pressed);
        pressingJump = pressed; }

    private class Jump
    {
        // TODO: Make the armed variable have a short time limit
        private final boolean armed;
        private boolean started = false;
        private final boolean interrupted;
        private Vec2 oldVel = new Vec2(0F, 0F);
        Jump(boolean ready, Vec2 oldVel)
        {
            if (oldVel != null) { interrupted = true; started = true; }
            else interrupted = false;
            armed = ready;
        }
        Jump()
        {
            armed = false;
            started = true;
            interrupted = false;
        }
        /* Called whenever the jump key is pressed or released */
        Jump trigger(boolean pressed)
        {
            /* If pressed in the air, the Jump becomes armed.
             * If released in the air, the Jump becomes unarmed
             * and unstarted. */
            if (state.isAirborne() || state == State.WALL_CLIMB) return new Jump(pressed, state == State.RISE ? oldVel : null);
            /* If pressed on the ground, the Jump starts.
             * If released on the ground, the Jump becomes unarmed
             * and unstarted. */
            return pressed ? new Jump() : new Jump(false, new Vec2(0F, 0F));
        }
        /* Called whenever the player hits the ground or sets a direction on a wall */
        Jump trigger()
        {
            /* If armed when hitting a surface, the Jump starts. */
            if (armed) return new Jump();
            /* If unarmed when hitting a surface, the Jump becomes
             * unarmed and unstarted. */
            return new Jump(false, null);
        }
        boolean execute()
        {
            if (!started) return false;
            if (interrupted)
            {
                Vec2 oldVel = body.getLinearVelocity();
                Vec2 newVel = new Vec2(oldVel.x, Math.max(oldVel.y, this.oldVel.y));
                body.setLinearVelocity(newVel);
            }
            else body.setLinearVelocity(getNewVel());
            started = false;
            return true;
        }
        private Vec2 getNewVel()
        {
            /* The variable oldVel will constantly change if not cloned */
            oldVel = body.getLinearVelocity().clone();
            float xVel = 0F, yVel = 0F;
            if (state.isGrounded())
            {
                xVel = oldVel.x;
                yVel = oldVel.y - jumpSpeed;
            }
            else if (state == State.SWIM)
            {
                // TODO: set jump velocities for being underwater
                return new Vec2(0, 0);
            }
            else if (dirVertical == Direction.UP)
            {
                return getJumpSpeed(jumpSpeed, 75, oldVel);
            }
            else if (dirVertical == Direction.DOWN)
            {
                return getJumpSpeed(jumpSpeed, 0, oldVel);
            }
            else if (dirPrimary == wallStickPos.opposite())
            {
                /*xVel = oldVel.x - jumpSpeed / 2F * wallStickPos.dirToNum();
                yVel = oldVel.y - jumpSpeed / 2F;*/
                return getJumpSpeed(jumpSpeed, 45, oldVel);
            }

            return new Vec2(xVel, yVel);
        }
        private Vec2 getJumpSpeed(float defaultSpeed, float angleDegrees, Vec2 velInit)
        {
          double ratioX = Math.cos(Math.toRadians(angleDegrees));
          double ratioY = Math.sin(Math.toRadians(angleDegrees));
          float vecX = (float) (velInit.x - defaultSpeed * ratioX * wallStickPos.dirToNum());
          float vecY = (float) (velInit.y - defaultSpeed * ratioY);

          return new Vec2(vecX , vecY);
        }
    }

    private enum Direction {
        UP { boolean vertical() { return true; } int dirToNum() { return -1; } Direction opposite() { return DOWN; } },
        LEFT { boolean horizontal() { return true; } int dirToNum() { return -1; } Direction opposite() { return RIGHT; } },
        DOWN { boolean vertical() { return true; } int dirToNum() { return 1; } Direction opposite() { return UP; } },
        RIGHT { boolean horizontal() { return true; } int dirToNum() { return 1; } Direction opposite() { return LEFT; } };
        boolean vertical() { return false; }
        boolean horizontal() { return false; }
        abstract int dirToNum();
        Direction opposite() { return null; }
    }

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

        if (state == State.RISE)
        {
            body.setGravityScale(jumpGravityReduced);
        }
        else if (state == State.WALL_CLIMB)
        {
            body.setGravityScale(climbGravityReduced);
            //body.getFixtureList().setFriction(0F);
        }
        else if (state == State.SLIDE)
        {
            body.getFixtureList().setFriction(frictionReduced);
        }
        else if (state == State.CROUCH)
        {
            body.getFixtureList().setFriction(frictionAmplified);
        }
        else
        {
            body.setGravityScale(1F);
            body.getFixtureList().setFriction(frictionDefault);
        }

        if (!this.state.isGrounded() && state.isGrounded())
        {
            jump = jump.trigger();
        }

        /* Temporary */
        Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");
        //Print.blue("dirPrimary: " + dirPrimary + ", dirVertical: " + dirVertical + "\n");

        this.state = state;
    }

    private void triggerContacts(ArrayList<Entity> entities)
    {
        boolean withinBlockHoriz = false;
        boolean withinBlockVert = false;
        wallStickPos = null;
        int groundsCounted = 0;
        int wallsCounted = 0;
        steppedGrade = 0F;
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
                    else
                    {
                        wallStickPos = horizBound;
                    }
                    if (vertBound == null)
                    {
                        vertBound = inBoundsVert(entity, true);
                        withinBlockVert = true;
                        steppedGrade = entity.getGrade() / 2F;
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
        //else if (jump.started) { jump.started = false; return; }
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
