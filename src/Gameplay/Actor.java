package Gameplay;

import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

import java.util.ArrayList;

//================================================================================================================
// Key Commands:
//    W,A,S,D: move left, right, up, down
//    J      : jump
//    ESC    : exit game
//    Arrow keys pan the camera. Q and E zoom in and out
//    Crouch : press down when on surface.
//    When crouching, A and D: player crawl.
//    If player jumps and holds down an arrow as he comes into contact with the wall, he'll stick to it.
//    If player presses jump and presses one of the other movement keys, he'll jump off the wall.
//================================================================================================================
public class Actor extends Entity
{
    private final float NORMAL_GRAVITY = 2;
    private final float REDUCED_GRAVITY = NORMAL_GRAVITY * 0.7F;

    private final float NORMAL_FRICTION, GREATER_FRICTION, REDUCED_FRICTION;

    /* The horizontal direction that the player intends to move towards */
    private Direction dirHoriz = null;
    /* The horizontal direction that the player intends to face towards.
     * This does not need to keep track of vertical direction. */
    private Direction dirFace = null;
    /* The vertical direction that the player intents to move towards */
    private Direction dirVert = null;

    //private Direction dirWall = null;

    private State state = State.FALL;

    private Entity[] touchEntity = new Entity[4];

    private boolean
            pressingLeft = false, pressingRight = false,
            pressingUp = false, pressingDown = false;

    private float gravity = NORMAL_GRAVITY;
    private float airDrag = 0.25F;
    private float waterDrag = 1F;

    void debug()
    {
        Print.blue("velX: " + getVelocityX() + ", velY: " + getVelocityY());
    }

    @Override
    public Color getColor() { return state.getColor(); }

    Actor(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE);

        NORMAL_FRICTION = 0.5F;
        GREATER_FRICTION = NORMAL_FRICTION * 3;
        REDUCED_FRICTION = NORMAL_FRICTION / 3;
        setFriction(NORMAL_FRICTION);
    }

    /**
     * Called every frame to update the Actor's will.
     */
    void act(ArrayList<Entity> entities, float deltaSec)
    {
        /* Location and velocity carry over from frame to frame.
         * Acceleration, however exists only when there is a force.
         * Thus, each frame, we set acceleration to 0, figure out which forces
         * are acting on it and add in acceleration for those forces. */
        setAcceleration(0,0);

        if (!state.isGrounded()) setAccelerationY(gravity);
        else if (touchEntity[DOWN] != null) setAcceleration(touchEntity[DOWN].getSlopeGravity());

        float vx = getVelocityX(), vy = getVelocityY();

        if (state.isGrounded())
        {
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

            if (dirHoriz == Direction.LEFT)
            {
                if (state == State.SLIDE) { if (vx > 0) addAccelerationX(-accel); }
                else if (vx > -topSpeed) addAccelerationX(-accel);
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (state == State.SLIDE) { if (vx < 0) addAccelerationX(accel); }
                else if (vx < topSpeed) addAccelerationX(accel);
            }

            if (pressedJumpTime > 0)
            {
                addVelocityY(-jumpVel);
                pressedJumpTime = 0F;
            }
        }

        else if (state.isAirborne())
        {
            if (dirHoriz == Direction.LEFT)
            {
                if (vx > -topAirSpeed) addAccelerationX(-airAccel);
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (vx < topAirSpeed) addAccelerationX(airAccel);
            }
        }

        else if (state == State.SWIM)
        {
            if (dirHoriz == Direction.LEFT)
            {
                if (vx > -maxSwimSpeed) addAccelerationX(-swimAccel);
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (vx < maxSwimSpeed) addAccelerationX(swimAccel);
            }
            if (dirVert == Direction.UP)
            {
                if (vy > -maxSwimSpeed) addAccelerationY(-swimAccel);
            }
            else if (dirVert == Direction.DOWN)
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
                    if (dirHoriz == Direction.RIGHT)
                    {
                        addVelocityX(jumpVel * 0.70712F); // sin(45)
                        addVelocityY(-jumpVel * 0.70712F); // cos(45)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == Direction.UP)
                    {
                        addVelocityX(jumpVel * 0.34202F); // sin(20)
                        addVelocityY(-jumpVel * 0.93969F); // cos(20)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == Direction.DOWN)
                    {
                        addVelocityX(jumpVel);
                        pressedJumpTime = 0;
                    }
                }
                else // if (touchEntity[RIGHT] != null)
                {
                    if (dirHoriz == Direction.LEFT)
                    {
                        addVelocityX(-jumpVel * 0.70712F); // sin(45)
                        addVelocityY(-jumpVel * 0.70712F); // cos(45)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == Direction.UP)
                    {
                        addVelocityX(-jumpVel * 0.34202F); // sin(20)
                        addVelocityY(-jumpVel * 0.93969F); // cos(20)
                        pressedJumpTime = 0F;
                    }
                    else if (dirVert == Direction.DOWN)
                    {
                        addVelocityX(-jumpVel);
                        pressedJumpTime = 0;
                    }
                }
            }

            if (dirHoriz != null)
            {
                if (dirHoriz == Direction.UP
                        || touchEntity[dirHoriz.ID()] != null)
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

        if (getVelocityY() >= 0) gravity = NORMAL_GRAVITY;

        applyAcceleration(getAcceleration(), deltaSec);
        Vec2 beforeDrag = applyAcceleration(determineDrag(), deltaSec);
        neutralizeVelocity(beforeDrag);
        Vec2 beforeFriction = applyAcceleration(determineFriction(), deltaSec);
        neutralizeVelocity(beforeFriction);
        Vec2 contactVelocity = applyVelocity(deltaSec, entities);
        if (setState(determineState()) && contactVelocity != null)
            addVelocityY(-Math.abs(contactVelocity.x));
    }

    private Vec2 applyAcceleration(Vec2 acceleration, float deltaSec)
    {
        Vec2 v = getVelocity();
        acceleration.mul(deltaSec);

        Vec2 oldVel = getVelocity();
        setVelocity(v.add(acceleration));

        return oldVel;
    }

    void neutralizeVelocity(Vec2 oldVel)
    {
        int unitPosVelX = 0;
        if (oldVel.x > 0) unitPosVelX = 1;
        else if (oldVel.x < 0) unitPosVelX = -1;

        int unitPosVelY = 0;
        if (oldVel.y > 0) unitPosVelY = 1;
        else if (oldVel.y < 0) unitPosVelY = -1;

        Vec2 newVel = getVelocity();

        int unitPosVelXNew = 0;
        if (newVel.x > 0) unitPosVelXNew = 1;
        else if (newVel.x < 0) unitPosVelXNew = -1;

        int unitPosVelYNew = 0;
        if (newVel.y > 0) unitPosVelYNew = 1;
        else if (newVel.y < 0) unitPosVelYNew = -1;

        if (unitPosVelX != unitPosVelXNew) setVelocityX(0);
        if (unitPosVelY != unitPosVelYNew) setVelocityY(0);
    }

    private Vec2 determineDrag()
    {
        float dragX, dragY;
        if (state == State.SWIM)
        {
            dragX = -waterDrag * getVelocityX();
            dragY = -waterDrag * getVelocityY();
        }
        else
        {
            dragX = -airDrag * getVelocityX();
            dragY = -airDrag * getVelocityY();
        }
        return new Vec2(dragX, dragY);
    }

    private Vec2 determineFriction()
    {
        float frictionX = 0, frictionY = 0;
        if (state.isGrounded())
        {
            if (dirHoriz == null
                    || (getVelocityX() < 0 && dirHoriz == Direction.RIGHT)
                    || (getVelocityX() > 0 && dirHoriz == Direction.LEFT)
                    || state == State.SLIDE)
            {
                frictionX = touchEntity[DOWN].getFriction() * getFriction();
            }
        }
        else if (state.isOnWall())
        {
            /* Don't apply friction if climbing up a wall */
            if (getVelocityY() < 0) return new Vec2(frictionX, frictionY);

            if (dirHoriz != null || dirVert != null)
                frictionY = touchEntity[touchEntity[LEFT] != null
                        ? LEFT : RIGHT].getFriction() * getFriction();
        }

        if (getVelocityX() > 0) frictionX = -frictionX;
        if (getVelocityY() > 0) frictionY = -frictionY;

        return new Vec2(frictionX, frictionY);
    }

    /**
     *  Returns the velocity upon hitting a surface
     */
    private Vec2 applyVelocity(float deltaSec, ArrayList<Entity> entities)
    {
        Vec2 goal = getPosition();
        getVelocity().mul(deltaSec);
        goal.add(getVelocity());
        /* triggerContacts() returns null if the actor does not hit anything */
        Vec2 contactVel = triggerContacts(goal, entities);
        setPosition(goal);
        return contactVel;
    }

    public void pressLeft(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = Direction.LEFT;
            /* It changes your primary direction regardless */
            dirHoriz = Direction.LEFT;
        }
        /* If you release the key when already moving left */
        else if (dirHoriz == Direction.LEFT)
        {
            if (pressingRight) dirHoriz = Direction.RIGHT;
            else dirHoriz = null;
            /* If you release the key when already moving left with a wall */
            if (state.isOnWall())
            {
                if (pressingRight) dirFace = Direction.RIGHT;
                else dirFace = null;
            }
        }
        pressingLeft = pressed;
    }
    public void pressRight(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirFace = Direction.RIGHT;
            /* It changes your primary direction regardless */
            dirHoriz = Direction.RIGHT;

        }
        /* If you release the key when already moving right */
        else if (dirHoriz == Direction.RIGHT)
        {
            if (pressingLeft) dirHoriz = Direction.LEFT;
            else dirHoriz = null;
            /* If you release the key when already moving right with a wall */
            if (state.isOnWall())
            {
                if (pressingLeft) dirFace = Direction.LEFT;
                else dirFace = null;
            }
        }
        pressingRight = pressed;
    }
    public void pressUp(boolean pressed)
    {
        if (pressed) dirVert = Direction.UP;
        else if (dirVert == Direction.UP)
        {
            if (pressingDown) dirVert = Direction.DOWN;
            else dirVert = null;
        }
        pressingUp = pressed;
    }
    public void pressDown(boolean pressed)
    {
        if (pressed) dirVert = Direction.DOWN;
        else if (dirVert == Direction.DOWN)
        {
            if (pressingUp) dirVert = Direction.UP;
            else dirVert = null;
        }
        pressingDown = pressed;
    }

    private boolean pressingJump = false;
    private float pressedJumpTime = 0;
    public void pressJump(boolean pressed)
    {
        if (pressed && !pressingJump) pressedJumpTime = 1F;
        else if (!pressed) pressedJumpTime = -1F;
        pressingJump = pressed;
    }

    private enum Direction
    {
        UP { boolean vertical() { return true; } int ID() { return 0; }
        int dirToNum() { return -1; } Direction opposite() { return DOWN; } },
        LEFT { boolean horizontal() { return true; } int ID() { return 3; }
        int dirToNum() { return -1; } Direction opposite() { return RIGHT; } },
        DOWN { boolean vertical() { return true; } int ID() { return 2; }
        int dirToNum() { return 1; } Direction opposite() { return UP; } },
        RIGHT { boolean horizontal() { return true; } int ID() { return 1; }
        int dirToNum() { return 1; } Direction opposite() { return LEFT; } };
        boolean vertical() { return false; }
        boolean horizontal() { return false; }
        abstract int dirToNum();
        int ID() { return -1; }
        Direction opposite() { return null; }
    }

    private State determineState()
    {
        if (inWater()) return State.SWIM;
        else if (touchEntity[DOWN] != null)
        {
            if (dirVert == Direction.DOWN)
            {
                if (Math.abs(getVelocityX()) > maxCrawlSpeed)
                    return State.SLIDE;
                if (dirHoriz != null && dirHoriz.horizontal())
                    return State.CRAWL;
                return State.CROUCH;
            }
            if (getVelocityX() > 0 && touchEntity[RIGHT] != null)
            {
                if (dirVert == Direction.UP || dirHoriz == Direction.RIGHT)
                    return State.WALL_CLIMB;
                else return State.STAND;
            }
            if (getVelocityX() < 0 && touchEntity[LEFT] != null)
            {
                if (dirVert == Direction.UP || dirHoriz == Direction.LEFT)
                    return State.WALL_CLIMB;
                else return State.STAND;
            }

            if (dirHoriz == Direction.RIGHT) return State.RUN;
            else if (dirHoriz == Direction.LEFT) return State.RUN;
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

        if (state == State.RISE) gravity = REDUCED_GRAVITY;
        else if (state == State.WALL_CLIMB && getVelocityY() < 0)
            gravity = REDUCED_GRAVITY;

        if (this.state == State.RISE && state == State.WALL_CLIMB)
        {
            Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");
            this.state = state;
            return true;
        }

        if (state == State.CROUCH) setFriction(GREATER_FRICTION);
        else if (state == State.SLIDE) setFriction(REDUCED_FRICTION);
        else setFriction(NORMAL_FRICTION);

        /* Temporary */
        Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");
        //Print.blue("dirPrimary: " + dirPrimary + ", dirVertical: " + dirVertical + "\n");

        this.state = state;
        return false;
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
    // It a collision is detected, this actor's velocity is set to zero.
    //
    // The method returns null if this actor's velocity is unchanged. Otherwise, it returns this actors original
    // velocity.
    //===============================================================================================================
    private Vec2 triggerContacts(Vec2 goal, ArrayList<Entity> entityList)
    {
        Vec2 orginalVel = null;
        touchEntity[UP] = null;
        touchEntity[DOWN] = null;
        touchEntity[LEFT] = null;
        touchEntity[RIGHT] = null;
        for (Entity entity : entityList)
        {
            if (entity == this) continue;
            int[] edge = entity.getTouchEdge(this, goal);
            if (edge[0] < 0) continue;

            /* Actor has touched another entity a this point */
            orginalVel = this.getVelocity();
            entity.setTriggered(true);
            touchEntity[edge[0]] = entity;

            if (edge[0] == UP)
            {
                goal.y = entity.getBottomEdge() + getHeight() / 2;
                setVelocityY(0);
            }
            else if (edge[0] == DOWN)
            {
                goal.y = entity.getTopEdge(goal.x) - getHeight() / 2;
                setVelocityY(0);
            }
            else if (edge[0] == LEFT)
            {
                goal.x = entity.getRightEdge() + getWidth() / 2;
                setVelocityX(0);
            }
            else if (edge[0] == RIGHT)
            {
                goal.x = entity.getLeftEdge() - getWidth() / 2;
                setVelocityX(0);
            }
        }
        return orginalVel;
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

    /* This is the highest speed the player can be running before changing
     * their state to TUMBLE or SLIDE. */
    private float maxCrawlSpeed = 0.06F;

    /* This is the highest speed the player can get from crawling alone.
     * They can go faster while crawling with the help of external influences,
     * such as going down a slope or being pushed by a faster object.
     *
     * This is the highest speed the player can be crawling or crouching
     * before changing their state to TUMBLE. */
    private float topCrawlSpeed = 0.05F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is crawling on the ground. */
    private float crawlAccel = 0.1F;

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
    private float swimAccel = 1F;

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

    private boolean inWater() { return false; }

    private int getSlopeType()
    {
        if (touchEntity[DOWN] == null) return UP;
        else if (touchEntity[LEFT] != null
                && touchEntity[LEFT] == touchEntity[DOWN]) return LEFT;
        else if (touchEntity[RIGHT] != null
                && touchEntity[RIGHT] == touchEntity[DOWN]) return RIGHT;
        return DOWN;
    }
}