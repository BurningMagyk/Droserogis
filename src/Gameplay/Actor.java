package Gameplay;

import Util.Print;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * Created by Joel on 6/14/2018.
 */
public class Actor extends Entity
{
    Direction dirPrimary = null;
    Direction dirSecondary = null;
    Direction dirVertical = null;


    State state = State.STAND;

    /* Stats set up by the Character object */
    private float maxRunSpeed = 3F;
    private float avgRunSpeed = 2F;
    private float groundDecel = 0.2F;
    private float aerialDecel = 0.1F;
    private float jumpSpeed = 6F;

    private float jumpGravityReduced = 0.7F;
    // TODO: The variable 'grade' needs to be reset to zero whenever not grounded on sloped surface
    private float grade = 0F;

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
    void act()
    {
        if (state.isGrounded())
        {
            if (dirPrimary == Direction.LEFT) move(groundDecel,true);
            else if (dirPrimary == Direction.RIGHT) move(groundDecel,false);
        }
        else if (state.isAirborne())
        {
            //if (dirPrimary == Direction.LEFT) body.setLinearVelocity(new Vec2(Math.min(body.getLinearVelocity().x, getNewVel(-0.1F)), body.getLinearVelocity().y));
            //else if (dirPrimary == Direction.RIGHT) body.setLinearVelocity(new Vec2(Math.max(body.getLinearVelocity().x, getNewVel(0.1F)), body.getLinearVelocity().y));
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

    private void move(float deceleration, boolean left)
    {
        float vertDecel = grade * deceleration;
        float horizDecel = deceleration - vertDecel;
        vertDecel *= (-1);
        horizDecel *= left ? (-1) : 1;

        body.setLinearVelocity(new Vec2(
                getNewVel(horizDecel, true),
                getNewVel(vertDecel, false)));
    }

    // TODO: Partition the speed based on the grade
    private float getNewVel(float deceleration, boolean horizontal)
    {
        float oldVel = horizontal? body.getLinearVelocity().x
                : body.getLinearVelocity().y;

        if (oldVel <= 0 && deceleration < 0)
            return Math.min(-avgRunSpeed, oldVel);
        if (oldVel >= 0 && deceleration > 0)
            return Math.max(avgRunSpeed, oldVel);

        float newVel = oldVel + deceleration;

        if (deceleration > 0 && newVel > maxRunSpeed) newVel = maxRunSpeed;
        else if (deceleration < 0 && newVel < -maxRunSpeed) newVel = -maxRunSpeed;

        return newVel;
    }

    boolean pressingLeft = false;
    boolean pressingRight = false;
    boolean pressingUp = false;
    boolean pressingDown = false;
    boolean pressingJump = false;
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
        //currentJump = currentJump.trigger(state.jumpSource(), pressed);
        // TODO: Parameter for trigger() should come from the State enum
        pressingJump = pressed; }

    private enum Direction {
        UP { boolean up() { return true; } boolean vertical() { return true; } },
        LEFT { boolean left() { return true; } boolean horizontal() { return true; } },
        DOWN { boolean down() { return true; } boolean vertical() { return true; } },
        RIGHT { boolean right() { return true; } boolean horizontal() { return true; } },
        /*UP_IN { boolean in() { return true; } boolean up() { return true; } boolean vertical() { return true; } },
        LEFT_IN { boolean in() { return true; } boolean left() { return true; } boolean horizontal() { return true; } },
        DOWN_IN { boolean in() { return true; } boolean down() { return true; } boolean vertical() { return true; } },
        RIGHT_IN{ boolean in() { return true; } boolean right() { return true; } boolean horizontal() { return true; } },*/
        ERROR;
        boolean in() { return false; }
        boolean up() { return false; }
        boolean left() { return false; }
        boolean down() { return false; }
        boolean right() { return false; }
        boolean vertical() { return false; }
        boolean horizontal() { return false; } }

    private enum State {
        PRONE_UP { boolean isGrounded() { return true; } },
        PRONE_DOWN { boolean isGrounded() { return true; } },
        RISE { boolean isAirborne() { return true; } },
        FALL { boolean isAirborne() { return true; } },
        STAND { boolean isGrounded() { return true; } },
        RUN { boolean isGrounded() { return true; } },
        WALL_STICK { boolean isOnWall() { return true; } },
        WALL_CLIMB { boolean isOnWall() { return true; } },
        CROUCH { boolean isGrounded() { return true; } },
        SLIDE { boolean isGrounded() { return true; } };
        boolean isOnWall() { return false; }
        boolean isAirborne() { return false; }
        boolean isGrounded() { return false; } }
}
