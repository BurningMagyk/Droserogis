package Gameplay;

import Gameplay.Weapons.Weapon;
import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Key Commands:
 *   W,A,S,D: move left, right, up, down
 *   J      : jump
 *   ESC    : exit game
 *   Arrow keys pan the camera. Q and E zoom in and out.
 *   Crouch : press down when on surface.
 *   When crouching, A and D: player crawl.
 *   If player jumps and holds down an arrow as he comes into contact with the wall, he'll stick to it.
 *   If player presses jump and presses one of the other movement keys, he'll jump off the wall.
 */
public class Actor extends Item
{
    private final float NORMAL_GRAVITY = gravity;
    private final float REDUCED_GRAVITY = NORMAL_GRAVITY * 0.7F;
    private final float WEAK_GRAVITY = NORMAL_GRAVITY * 0.1F;

    private final float NORMAL_FRICTION, GREATER_FRICTION, REDUCED_FRICTION;

    /* The horizontal direction that the player intends to move towards */
    private int dirHoriz = -1;
    /* The horizontal direction that the player intends to face towards.
     * This does not need to keep track of vertical direction. */
    private int dirFace = -1;
    /* The vertical direction that the player intents to move towards */
    private int dirVert = -1;

    private State state = State.FALL;

    private LateSurface[] touchLateSurface = new LateSurface[4];

    private boolean
            pressingLeft = false, pressingRight = false,
            pressingUp = false, pressingDown = false;

    private Weapon weapon;

    @Override
    public Color getColor() { return state.getColor(); }

    Actor(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        NORMAL_FRICTION = 1F;
        GREATER_FRICTION = NORMAL_FRICTION * 3;
        REDUCED_FRICTION = NORMAL_FRICTION / 3;
        setFriction(NORMAL_FRICTION);
    }

    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        resetAcceleration();
        act(deltaSec);
        applyPhysics(entities, deltaSec);
    }

    /**
     * Called every frame to update the Actor's will.
     */
    private void act(float deltaSec)
    {
        float vx = getVelocityX(), vy = getVelocityY();

        if (pressedJumpTime > 0)
        {
            if (touchLateSurface[DOWN] != null)
            {
                float lateVelY = touchLateSurface[DOWN].getLateVel().y;
                setVelocityY(touchLateSurface[DOWN].getShape().getDirs()[UP]
                        ? -jumpVel + lateVelY : -jumpVel - slopeJumpBuffer + lateVelY);
                pressedJumpTime = 0F;
            }
            else if (touchLateSurface[LEFT] != null)
            {
                Vec2 lateVel = touchLateSurface[LEFT].getLateVel();
                if (dirHoriz == RIGHT)
                {
                    setVelocityX(jumpVel * 0.70712F + lateVel.x); // sin(45)
                    setVelocityY(-jumpVel * 0.70712F + lateVel.y); // cos(45)
                    pressedJumpTime = 0F;
                }
                else if (dirVert == UP)
                {
                    setVelocityX(jumpVel * 0.34202F + lateVel.x); // sin(20)
                    setVelocityY(-jumpVel * 0.93969F + lateVel.y); // cos(20)
                    pressedJumpTime = 0F;
                }
                else if (dirVert == DOWN)
                {
                    setVelocityX(jumpVel + lateVel.x);
                    pressedJumpTime = 0;
                }
            }
            else if (touchLateSurface[RIGHT] != null)
            {
                Vec2 lateVel = touchLateSurface[RIGHT].getLateVel();
                if (dirHoriz == LEFT)
                {
                    setVelocityX(-jumpVel * 0.70712F + lateVel.x); // sin(45)
                    setVelocityY(-jumpVel * 0.70712F + lateVel.y); // cos(45)
                    pressedJumpTime = 0F;
                }
                else if (dirVert == UP)
                {
                    setVelocityX(-jumpVel * 0.34202F + lateVel.x); // sin(20)
                    setVelocityY(-jumpVel * 0.93969F + lateVel.y); // cos(20)
                    pressedJumpTime = 0F;
                }
                else if (dirVert == DOWN)
                {
                    setVelocityX(-jumpVel + lateVel.x);
                    pressedJumpTime = 0;
                }
            }
        }

        if (state.isGrounded())
        {
            /* If the entity being stood on is an upward-slope triangle */
            if (!touchEntity[DOWN].getShape().getDirs()[UP])
                setAcceleration(touchEntity[DOWN].applySlopeY(this.gravity));

            float accel, topSpeed;
            if (state == State.CROUCH || state == State.CRAWL
                    || state == State.SLIDE)
            {
                accel = crawlAccel;
                topSpeed = topCrawlSpeed;
            }
            else
            {
                accel = runAccel;
                topSpeed = topRunSpeed;
            }

            if (dirHoriz == LEFT)
            {
                if (state == State.SLIDE) { if (vx > 0) addAccelerationX(-accel); }
                else if (vx > -topSpeed)
                {
                    addAccelerationX(-accel);
                    addVelocityX((float) -minThreshSpeed * 1.5F);
                }
                //addAcceleration(touchEntity[DOWN].applySlopeX(-accel));
            }
            else if (dirHoriz == RIGHT)
            {
                if (state == State.SLIDE) { if (vx < 0) addAccelerationX(accel); }
                else if (vx < topSpeed)
                {
                    addAccelerationX(accel);
                    addVelocityX((float) minThreshSpeed * 1.5F);
                }
                //addAcceleration(touchEntity[DOWN].applySlopeX(accel));
            }

            if (pressedJumpTime > 0)
            {
                addVelocityY(touchEntity[DOWN].getShape().getDirs()[UP]
                        ? -jumpVel : -jumpVel - slopeJumpBuffer);
                pressedJumpTime = 0F;
            }
        }

        else if (state.isAirborne())
        {
            suspendedMovement(vx, topAirSpeed, airAccel);
        }

        else if (state == State.SWIM)
        {
            suspendedMovement(vx, maxSwimSpeed, swimAccel);
            if (dirVert == UP)
            {
                if (vy > -maxSwimSpeed) addAccelerationY(-swimAccel);
            }
            else if (dirVert == DOWN)
            {
                if (vy < maxSwimSpeed) addAccelerationY(swimAccel);
            }
        }

        else if (state.isOnWall())
        {
            if (pressedJumpTime > 0)
            {
                if (touchEntity[LEFT] != null)
                {
                    if (dirHoriz == RIGHT)
                    {
                        addVelocityX(jumpVel * 0.70712F); // sin(45)
                        addVelocityY(-jumpVel * 0.70712F); // cos(45)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == UP)
                    {
                        addVelocityX(jumpVel * 0.34202F); // sin(20)
                        addVelocityY(-jumpVel * 0.93969F); // cos(20)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == DOWN)
                    {
                        addVelocityX(jumpVel);
                        pressedJumpTime = 0;
                    }
                }
                else // if (touchEntity[RIGHT] != null)
                {
                    if (dirHoriz == LEFT)
                    {
                        addVelocityX(-jumpVel * 0.70712F); // sin(45)
                        addVelocityY(-jumpVel * 0.70712F); // cos(45)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == UP)
                    {
                        addVelocityX(-jumpVel * 0.34202F); // sin(20)
                        addVelocityY(-jumpVel * 0.93969F); // cos(20)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == DOWN)
                    {
                        addVelocityX(-jumpVel);
                        pressedJumpTime = 0;
                    }
                }
            }

            if (dirHoriz != -1)
            {
                if (dirHoriz == UP
                        || touchEntity[dirHoriz] != null)
                    addAccelerationY(-climbAccel);
            }
        }

        if (pressedJumpTime > 0)
        {
            pressedJumpTime -= deltaSec;
            if (pressedJumpTime < 0) pressedJumpTime = 0F;
        }
        /* If pressedJumpTime is -1, that means the player let go of the
         * jump key while airborne and its effect on the player's movement
         * already occurred this frame. */
        else if (pressedJumpTime == -1) pressedJumpTime = 0F;

        /* Cap overall speed */
        if (getVelocityX() > maxTotalSpeed) setVelocityX(maxTotalSpeed);
        else if (getVelocityX() < -maxTotalSpeed) setVelocityX(-maxTotalSpeed);
        if (getVelocityY() > maxTotalSpeed) setVelocityY(maxTotalSpeed);
        else if (getVelocityY() < -maxTotalSpeed) setVelocityY(-maxTotalSpeed);

        /* When travelling on a ramp, they get weak gravity */
        if (touchEntity[DOWN] != null
                && !touchEntity[DOWN].getShape().getDirs()[UP]
                && dirHoriz != -1) {
            //gravity = WEAK_GRAVITY;
            gravity = REDUCED_GRAVITY;
        }
        /* When starting to fall, they lose reduced gravity */
        else if (getVelocityY() >= 0)
        {
            /* If in water, the gravity is weak when still,
             * zero when swimming. */
            if (inWater){
                if (dirHoriz == -1 && dirVert == -1)
                    gravity = WEAK_GRAVITY;
                else gravity = 0;
            } else gravity = NORMAL_GRAVITY;
        }
    }

    void applyPhysics(ArrayList<Entity> entities, float deltaSec)
    {
        applyAcceleration(getAcceleration(), deltaSec);
        Vec2 beforeDrag = applyAcceleration(determineDrag(), deltaSec);
        neutralizeVelocity(beforeDrag);
        Vec2 beforeFriction = applyAcceleration(determineFriction(), deltaSec);
        neutralizeVelocity(beforeFriction);
        Vec2 contactVelocity = applyVelocity(deltaSec, entities);
        if (setState(determineState()) && contactVelocity != null)
            addVelocityY(-Math.abs(contactVelocity.x));
    }

    /* Used for airborne and swimming, horizontal */
    private void suspendedMovement(float vx, float topAirSpeed, float airAccel) {
        if (dirHoriz == LEFT)
        {
            if (vx > -topAirSpeed) addAccelerationX(-airAccel);
        }
        else if (dirHoriz == RIGHT)
        {
            if (vx < topAirSpeed) addAccelerationX(airAccel);
        }
    }

    private Vec2 determineFriction()
    {
        float frictionX = 0, frictionY = 0;
        if (state.isGrounded())
        {
            if (dirHoriz == -1
                    || (getVelocityX() < 0 && dirHoriz == RIGHT)
                    || (getVelocityX() > 0 && dirHoriz == LEFT)
                    || state == State.SLIDE)
            {
                frictionX = touchEntity[DOWN].getFriction() * getFriction();
                if (touchEntity[DOWN] != null && !touchEntity[DOWN].getShape().getDirs()[UP])
                {
                    Vec2 slopeVel = touchEntity[DOWN].applySlopeX(frictionX);
                    frictionX = slopeVel.x;
                    frictionY = slopeVel.y;
                }
            }
        }
        else if (state.isOnWall())
        {
            /* Don't apply friction if climbing up a wall */
            if (getVelocityY() < 0) return new Vec2(frictionX, frictionY);

            if (dirHoriz != -1 || dirVert != -1)
                frictionY = touchEntity[touchEntity[LEFT] != null
                        ? LEFT : RIGHT].getFriction() * getFriction();
        }

        if (getVelocityX() > 0) frictionX = -frictionX;
        if (getVelocityY() > 0) frictionY = -frictionY;

        return new Vec2(frictionX, frictionY);
    }

    /**
     *  Returns the velocity upon hitting a surface.
     *  Useful for determining how much vertical velocity is generated by
     *  running into another entity.
     */
    private Vec2 applyVelocity(float deltaSec, ArrayList<Entity> entities)
    {
        Vec2 posOriginal = getPosition();
        Vec2 goal = getPosition();
        getVelocity().mul(deltaSec);
        goal.add(getVelocity());
        /* triggerContacts() returns null if the actor does not hit anything */
        Vec2 contactVel = triggerContacts(deltaSec, goal, entities);
        setPosition(goal);

        /* Stop horizontal velocity from building up by setting it to match change in
         * position. Needed for jumping to work correctly and when falling off block. */
        if (touchEntity[DOWN] != null
                && !touchEntity[DOWN].getShape().getDirs()[UP])
            setVelocityY(getY() - posOriginal.y + slopeJumpBuffer);

        return contactVel;
    }

    void pressLeft(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = LEFT;
            /* It changes your primary direction regardless */
            dirHoriz = LEFT;
        }
        /* If you release the key when already moving left */
        else if (dirHoriz == LEFT)
        {
            if (pressingRight) dirHoriz = RIGHT;
            else dirHoriz = -1;
            /* If you release the key when already moving left with a wall */
            if (state.isOnWall())
            {
                if (pressingRight) dirFace = RIGHT;
                else dirFace = -1;
            }
        }
        pressingLeft = pressed;
    }
    void pressRight(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = RIGHT;
            /* It changes your primary direction regardless */
            dirHoriz = RIGHT;

        }
        /* If you release the key when already moving right */
        else if (dirHoriz == RIGHT)
        {
            if (pressingLeft) dirHoriz = LEFT;
            else dirHoriz = -1;
            /* If you release the key when already moving right with a wall */
            if (state.isOnWall())
            {
                if (pressingLeft) dirFace = LEFT;
                else dirFace = -1;
            }
        }
        pressingRight = pressed;
    }
    void pressUp(boolean pressed)
    {
        if (pressed) dirVert = UP;
        else if (dirVert == UP)
        {
            if (pressingDown) dirVert = DOWN;
            else dirVert = -1;
        }
        pressingUp = pressed;
    }
    void pressDown(boolean pressed)
    {
        if (pressed) dirVert = DOWN;
        else if (dirVert == DOWN)
        {
            if (pressingUp) dirVert = UP;
            else dirVert = -1;
        }
        pressingDown = pressed;
    }

    private boolean pressingJump = false;
    private float pressedJumpTime = 0;
    void pressJump(boolean pressed)
    {
        if (pressed && !pressingJump) pressedJumpTime = 1F;
        else if (!pressed) pressedJumpTime = -1F;
        pressingJump = pressed;
    }

    void pressAttack(boolean pressed, boolean primary)
    {
        //weapon.operate(0, dirFace);
    }

    private State determineState()
    {
        if (submerged || (inWater && touchLateSurface[DOWN] == null))
            return State.SWIM;
        else if (touchEntity[DOWN] != null)
        {
            if (dirVert == DOWN)
            {
                if (Math.abs(getVelocityX()) > maxCrawlSpeed)
                    return State.SLIDE;
                if (dirHoriz != -1) return State.CRAWL;
                return State.CROUCH;
            }
            if (getVelocityX() > 0 && touchEntity[RIGHT] != null)
            {
                if ((dirVert == UP || dirHoriz == RIGHT)
                        && touchEntity[RIGHT] instanceof Block)
                    return State.WALL_CLIMB;
                else return State.STAND;
            }
            if (getVelocityX() < 0 && touchEntity[LEFT] != null)
            {
                if ((dirVert == UP || dirHoriz == LEFT)
                        && touchEntity[LEFT] instanceof Block)
                    return State.WALL_CLIMB;
                else return State.STAND;
            }

            if (dirHoriz != -1) return State.RUN;
            return State.STAND;
        }
        else if (touchEntity[LEFT] != null || touchEntity[RIGHT] != null)
        {
            if (getVelocityY() < 0) return State.WALL_CLIMB;
            return State.WALL_STICK;
        }
        else
        {
            if (getVelocityY() < 0) return State.RISE;
            else return State.FALL;
        }
    }

    /**
     *  Returns true if Actor transitioned from RISE to WALL_CLIMB
     */
    boolean setState(State state)
    {
        if (this.state == state) return false;
        else if (state == State.RISE) gravity = REDUCED_GRAVITY;
        else if (state == State.WALL_CLIMB && getVelocityY() < 0)
            gravity = REDUCED_GRAVITY;

        if (this.state == State.RISE && state == State.WALL_CLIMB)
        {
            Print.blue(this.state + " -> " + state);
            this.state = state;
            return true;
        }

        if (state == State.CROUCH) setFriction(GREATER_FRICTION);
        else if (state == State.SLIDE) setFriction(REDUCED_FRICTION);
        else setFriction(NORMAL_FRICTION);

        Print.blue(this.state + " -> " + state);

        this.state = state;
        return false;
    }

    void equip(Weapon weapon)
    {
        this.weapon = weapon.equip(this);
    }

    //===============================================================================================================
    //  triggerContacts(Vec2 goal, ArrayList<Entity> entityList)
    //
    // This method iterates through entityList and assigns each element of the class array touchEntity[] to null
    //  (if this actor is not touching anything on that edge) or to an entity reference of whatever entity it is
    //  touching.
    //
    // The given Vec2 goal is the location where this actor would move if it does not hit anything.
    //   This method changes the location in goal so that the object overlap anything.
    //
    // If a collision is detected, this actor's velocity adjusted accordingly.
    //
    // The method returns null if this actor's velocity is unchanged. Otherwise, it returns this actors original
    // velocity.
    //===============================================================================================================
    private Vec2 triggerContacts(float deltaSec, Vec2 goal, ArrayList<Entity> entityList)
    {
        if (touchLateSurface[UP] != null
                && touchLateSurface[UP].countdown(deltaSec))
            touchLateSurface[UP] = null;
        if (touchLateSurface[DOWN] != null
                && touchLateSurface[DOWN].countdown(deltaSec))
            touchLateSurface[DOWN] = null;
        if (touchLateSurface[LEFT] != null
                && touchLateSurface[LEFT].countdown(deltaSec))
            touchLateSurface[LEFT] = null;
        if (touchLateSurface[RIGHT] != null
                && touchLateSurface[RIGHT].countdown(deltaSec))
            touchLateSurface[RIGHT] = null;

        return super.triggerContacts(goal, entityList);
    }

    /*=======================================================================*/
    /* Variables that are set by the character's stats                       */
    /*=======================================================================*/

    /* This is the highest speed the player can be running before changing
     * their state to TUMBLE. */
    private float maxRunSpeed = 0.2F;

    /* This is the highest speed the player can get from running alone.
     * They can go faster while running with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float topRunSpeed = 0.15F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is running on the ground. */
    private float runAccel = 0.2F;

    /* This is the highest speed the player can be crawling or crouching before
     * changing their state to TUMBLE or SLIDE. */
    private float maxCrawlSpeed = 0.06F;

    /* This is the highest speed the player can get from crawling alone.
     * They can go faster while crawling with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float topCrawlSpeed = 0.05F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is crawling on the ground. */
    private float crawlAccel = 0.3F;

    /* This is the highest speed the player can be sliding before changing
     * their state to TUMBLE. */
    private float maxSlideSpeed = 0.3F;

    /* This is the highest speed the player can be climbing before changing
     * their state to RISE. */
    private float maxClimbSpeed = 1F;

    /* This is the highest speed the player can be skidding down a wall before
     * changing their state to FALL. */
    private float maxStickSpeed = 1.5F;

    /* This is the acceleration that is applied to the player when on a wall.
     * Most characters should use their crawlAccel value for this, unless they
     * know how to climb without needing a running start. */
    private float climbAccel = crawlAccel;

    /* This is the highest speed the player can move.
     * (In the air or anywhere) */
    private float maxTotalSpeed = 5F;

    /* This is the highest speed the player can get from moving themselves in
     * the air. They can go faster in the air with the help of external
     * influences such as wind or being pushed by a faster object. */
    private float topAirSpeed = 0.2F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is airborne. */
    private float airAccel = 0.1F;

    /* This is the highest speed the player can move in water. */
    private float maxSwimSpeed = 3F;

    /* A variable "topSwimSpeed" won't be needed because the drag underwater
     * will be high enough to cap the player's speed. */

    /* This is the acceleration that is applied to the player when in water. */
    private float swimAccel = 0.3F;

    /* The velocity used to jump */
    private float jumpVel = 0.4F;


    //================================================================================================================
    // State
    //================================================================================================================
    private enum State
    {
        PRONE
                {
                    boolean isGrounded() { return true; }
                    boolean isIncapacitated() { return true; }
                    Color getColor() { return Color.BLACK; }
                },
        TUMBLE
                {
                    boolean isGrounded() { return true; }
                    boolean isIncapacitated() { return true; }
                    Color getColor() { return Color.GREY; }
                },
        BALLISTIC
                {
                    boolean isAirborne() { return true; }
                    boolean isIncapacitated() { return true; }
                    Color getColor() { return Color.BLACK; }
                },
        RISE
                {
                    boolean isAirborne() { return true; }
                    Color getColor() { return Color.CORNFLOWERBLUE; }
                },
        FALL
                {
                    boolean isAirborne() { return true; }
                    Color getColor() { return Color.CYAN; }
                },
        STAND
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.MAROON; }
                },
        RUN
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.BROWN; }
                },
        WALL_STICK
                {
                    boolean isOnWall() { return true; }
                    Color getColor() { return Color.GREENYELLOW; }
                },
        WALL_CLIMB
                {
                    boolean isOnWall() { return true; }
                    Color getColor() { return Color.LIGHTGREEN; }
                },
        CROUCH
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.RED; }
                },
        CRAWL
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.HOTPINK; }
                },
        SLIDE
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.PINK; }
                },
        SWIM
                {
                    Color getColor() { return Color.BLUE; }
                };

        boolean isOnWall() { return false; }
        boolean isAirborne() { return false; }
        boolean isGrounded() { return false; }
        boolean isIncapacitated() { return false; }
        Color getColor() { return Color.BLACK; }
    }

    boolean setTriggered(boolean triggered)
    {
        super.setTriggered(triggered);
        return true;
    }

    private class LateSurface
    {
        private Entity entity;
        private Vec2 lateVel;
        private float duration = 0.1F;

        LateSurface(Entity entity, Vec2 lateVel)
        {
            this.entity = entity;
            this.lateVel = lateVel;
        }

        boolean countdown(float deltaSec)
        {
            duration -= deltaSec;
            if (duration <= 0) return true;
            return false;
        }

        Vec2 getLateVel() { return lateVel; }

        ShapeEnum getShape() { return entity.getShape(); }
    }
}