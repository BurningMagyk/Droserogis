package Gameplay;

import Gameplay.Weapons.Weapon;
import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

import javax.print.attribute.standard.OrientationRequested;
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

    private final float ORIGINAL_HEIGHT;

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
            pressingUp = false, pressingDown = false, pressingShift = false;
    // change to more when other attack buttons get implemented
    private boolean[] pressingAttack = new boolean[2];

    private Weapon weapon;
    private float[] status = new float[Status.values().length];

    @Override
    public Color getColor() { return state.getColor(); }

    Actor(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height);

        NORMAL_FRICTION = 1F;
        GREATER_FRICTION = NORMAL_FRICTION * 3;
        REDUCED_FRICTION = NORMAL_FRICTION / 3;
        setFriction(NORMAL_FRICTION);

        ORIGINAL_HEIGHT = height;
    }

    protected void update(ArrayList<Entity> entities, float deltaSec)
    {
        resetAcceleration();
        act(deltaSec);
        applyPhysics(entities, deltaSec);
        countdownStatus(deltaSec);
    }

    /**
     * Called every frame to update the Actor's will.
     */
    private void act(float deltaSec)
    {
        float vx = getVelocityX(), vy = getVelocityY();

        /* Late surfaces acts wacky when not airborne. */
        if (pressedJumpTime > 0 && state.isAirborne())
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
                accel = status[Status.STAGNANT.ID()] > 0
                        || status[Status.CLUMPED.ID()] > 0 ? 0 : crawlAccel;
                topSpeed = getTopSpeed(true);
            }
            else
            {
                accel = status[Status.STAGNANT.ID()] > 0 ? 0 : runAccel;
                topSpeed = getTopSpeed(false);
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
                if (status[Status.RUSHED.ID()] > 0 && getVelocityX() > -rushSpeed) setVelocityX(-rushSpeed);
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
                if (status[Status.RUSHED.ID()] > 0 && getVelocityX() < rushSpeed) setVelocityX(rushSpeed);
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

    private float getTopSpeed(boolean low)
    {
        if (status[Status.STAGNANT.ID()] > 0
                || status[Status.CLUMPED.ID()] > 0) return 0;
        if (low) return shouldSprint() ? topLowerSprintSpeed : topCrawlSpeed;
        if (status[Status.PLODDED.ID()] > 0) return plodSpeed;
        if (shouldSprint()) return topSprintSpeed;
        return topRunSpeed;
    }

    private boolean shouldSprint()
    {
        return pressingShift
                && dirFace != -1 && dirHoriz != -1 && dirFace == dirHoriz;
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
                    || state == State.SLIDE
                    || (Math.abs(getVelocityX()) > plodSpeed && status[Status.PLODDED.ID()] > 0)
                    || status[Status.STAGNANT.ID()] > 0
                    || status[Status.CLUMPED.ID()] > 0)
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

    public void setPosition(Vec2 p)
    {
        weapon.updatePosition(p, getDims(), getWeaponFace());
        super.setPosition(p);
    }

    private void countdownStatus(float deltaSec)
    {
        for (int i = 0; i < status.length; i++)
        {
            status[i] -= deltaSec;
            if (status[i] < 0) status[i] = 0;
        }
    }

    void pressLeft(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = LEFT;
            /* It changes your primary direction regardless */
            dirHoriz = LEFT;
            /* If you're not forcing a secondary direction,
             * this will change it */
            if (dirFace != LEFT && !pressingRight) dirFace = LEFT;
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
            /* If you're not forcing a secondary direction,
             * this will change it */
            if (dirFace != RIGHT && !pressingLeft) dirFace = RIGHT;
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
    void pressShift(boolean pressed) { pressingShift = pressed; }

    private boolean pressingJump = false;
    private float pressedJumpTime = 0;
    void pressJump(boolean pressed)
    {
        if (pressed && !pressingJump) pressedJumpTime = 1F;
        else if (!pressed) pressedJumpTime = -1F;
        pressingJump = pressed;
    }

    public void debug() { weapon.test(); }

    public static int COMBO_UP = 10, COMBO_DOWN = 20, COMBO_HORIZ = 40,
            ATTACK_KEY_1 = 0, ATTACK_KEY_2 = 1, ATTACK_KEY_3 = 2;
    void pressAttack(boolean pressed, int attackKey)
    {
        Weapon.OpContext status = Weapon.OpContext.STANDARD;
        if (state == State.SPRINT || state == State.LOWER_SPRINT)
            status = Weapon.OpContext.LUNGE;
        if (state == State.CROUCH || state == State.CRAWL)
            status = Weapon.OpContext.LOW;
        else if (state.isAirborne()) status = Weapon.OpContext.FREE;

        int keyCombo = attackKey;
        if ((dirVert == UP || dirVert == DOWN) && dirHoriz >= 0)
            keyCombo += COMBO_HORIZ;
        if (dirVert == UP) keyCombo += COMBO_UP;
        else if (dirVert == DOWN) keyCombo += COMBO_DOWN
                // When the player is crouching while also pressing up
                + (pressingUp ? COMBO_UP : 0);

        if (pressingAttack[attackKey] != pressed)
            weapon.operate(pressed, keyCombo, status);
        else if (!pressed) weapon.operate(false, keyCombo, status);
        pressingAttack[attackKey] = pressed;
    }

    public DirEnum getWeaponFace()
    {
        return DirEnum.get(dirFace < 0
                ? dirHoriz : dirFace, dirVert);
    }

    public void changeDirFace()
    {
        dirHoriz = opp(dirHoriz);
        dirFace = opp(dirFace);
        weapon.updatePosition(getPosition(), getDims(), getWeaponFace());
    }

    private State determineState()
    {
        if (submerged || (inWater && touchLateSurface[DOWN] == null))
            return State.SWIM;
        else if (touchEntity[DOWN] != null)
        {
            if (dirVert == DOWN
                    && status[Status.STAGNANT.ID()] == 0
                    && status[Status.RUSHED.ID()] == 0)
            {
                setHeight(ORIGINAL_HEIGHT / 2);

                if (Math.abs(getVelocityX()) > maxCrawlSpeed)
                    return State.SLIDE;
                if (dirHoriz != -1)
                {
                    if (Math.abs(getVelocityX()) > topCrawlSpeed
                            && shouldSprint()) return State.LOWER_SPRINT;
                    return State.CRAWL;
                }
                return State.CROUCH;
            }
            else setHeight(ORIGINAL_HEIGHT);

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

            if (dirHoriz != -1)
            {
                if (Math.abs(getVelocityX()) > topRunSpeed
                        && shouldSprint()) return State.SPRINT;
                return State.RUN;
            }
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
        for (int i = 0; i < touchLateSurface.length; i++)
        {
            if (touchLateSurface[i] != null
                    && touchLateSurface[i].countdown(deltaSec))
                touchLateSurface[i] = null;
            else if (touchLateSurface[i] == null && touchEntity[i] != null)
                touchLateSurface[i] = new LateSurface(touchEntity[i], getVelocity());
        }

        return super.triggerContacts(goal, entityList);
    }

    /*=======================================================================*/
    /* Variables that are set by the character's stats                       */
    /*=======================================================================*/

    /* This is the highest speed the player can be running or sprinting before
     * changing their state to TUMBLE. */
    private float maxRunSpeed = 0.2F;

    /* This is the highest speed the player can get from plodding alone.
     * They can go faster while plodding with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float plodSpeed = 0.04F;

    private float rushSpeed = 0.3F;

    /* This is the highest speed the player can get from sprinting alone.
     * They can go faster while sprinting with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float topSprintSpeed = 0.13F;

    /* This is the highest speed the player can get from running alone.
     * They can go faster while running with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float topRunSpeed = 0.08F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is running or sprinting on the ground. */
    private float runAccel = 0.4F;

    /* This is the highest speed the player can be crawling or crouching before
     * changing their state to TUMBLE or SLIDE. */
    private float maxCrawlSpeed = 0.15F;

    /* This is the highest speed the player can get from sprinting on their
     * hands alone. They can go faster while sprinting on their hands with the
     * help of external influences, such as going down a slope or being pushed
     * by a faster object. */
    private float topLowerSprintSpeed = 0.10F;

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
        SPRINT
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.DARKGRAY; }
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
        LOWER_SPRINT
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.DEEPPINK; }
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

    //================================================================================================================
    // Status
    //================================================================================================================
    public enum Status
    {
        PLODDED { int ID() { return 0; } },
        STAGNANT { int ID() { return 1; } },
        CLUMPED { int ID() { return 2; } },
        RUSHED { int ID() { return 3; } };
        int ID() { return -1; }
    }
    public void addStatus(float time, Status status)
    {
        if (this.status[status.ID()] < time) this.status[status.ID()] = time;
    }
    //public void stagnate(float time) { addStatus(time, Status.STAGNANT); }
    //public void plod(float time) { addStatus(time, Status.PLODDED); }

    boolean setTriggered(boolean triggered)
    {
        super.setTriggered(triggered);
        return true;
    }

    private class LateSurface
    {
        private Entity entity;
        private Vec2 lateVel;
        private float duration = 0.2F;

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