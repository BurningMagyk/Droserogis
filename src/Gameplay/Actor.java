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

    private float gravity = 9.8F;
    private float gravJumpReduct = 0.3F;
    private float gravClimbReduct = 0.7F;
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

        float vx = getVelocityX();

        if (state.isGrounded())
        {
            if (dirHoriz == Direction.LEFT)
            {
                if (vx > -topRunSpeed) addAccelerationX(-runAccel);
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (vx < topRunSpeed) addAccelerationX(runAccel);
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

            if (pressedJumpTime == -1 && state == State.RISE)
            {
                // TODO: set gravity back to normal
            }
        }

        else if (state == State.SWIM)
        {
            float vy = getVelocityY();

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
            /*float vy = getVelocityY();
            if (pressingUp)
            {
                vy = vy - state.acceleration()*deltaSec;
                if (vy < -state.maxSpeed()) vy = -state.maxSpeed();
            }
            else if (pressingDown)
            {
                vy = vy + state.acceleration()*deltaSec;
                if (vy > state.maxSpeed()) vy = state.maxSpeed();
            }

            setVelocityY(vy);*/
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

        /*===================================================================*/
        /*                      Apply drag and friction                      */
        /*===================================================================*/
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
        float frictionX = 0, frictionY = 0;
        if (state.isGrounded())
        {
            if (dirHoriz == null
                    || getVelocityX() < 0 && dirHoriz == Direction.RIGHT
                    || getVelocityX() > 0 && dirHoriz == Direction.LEFT)
            {
                frictionX = touchEntity[DOWN].getFriction() * getFriction();
            }
            else frictionX = 0F;
        }
        else if (state.isOnWall())
        {
            if ((dirHoriz == null && dirVert == null) || getVelocityY() < 0)
                frictionY = -gravity * gravClimbReduct;
            else if (touchEntity[LEFT] == null)
                frictionY = touchEntity[RIGHT].getFriction() * getFriction();
            else frictionY = touchEntity[LEFT].getFriction() * getFriction();
        }
        else if (state == State.RISE) frictionY = -gravity * gravJumpReduct;

        if (getVelocityX() > 0) frictionX = -frictionX;
        if (getVelocityY() > 0) frictionY = -frictionY;

        /*if (dragX + frictionX < 0 && getAccelerationX() > 0 && Math.abs(dragX + frictionX) > getAccelerationX()) setAccelerationX(0F);
        else if (dragX + frictionX > 0 && getAccelerationX() < 0 && Math.abs(dragX + frictionX) > Math.abs(getAccelerationX())) setAccelerationX(0F);
        else setAccelerationX(getAccelerationX() + dragX + frictionX);

        if (dragY + frictionY < 0 && getAccelerationY() > 0 && Math.abs(dragY + frictionY) > getAccelerationY()) setAccelerationY(0F);
        else if (dragY + frictionY > 0 && getAccelerationY() < 0 && Math.abs(dragY + frictionY) > Math.abs(getAccelerationY())) setAccelerationY(0F);
        else setAccelerationY(getAccelerationY() + dragY + frictionY);*/

        //frictionX = 0F;
        //frictionY = 0F;

        boolean deceleratingX = false;
        if (Math.abs(getAccelerationX()) < Math.abs(dragX + frictionX))
        {
            if (getAccelerationX() < 0 && (dragX + frictionX) > 0
                    || getAccelerationX() > 0 && (dragX + frictionX) < 0)
                deceleratingX = true;
        }
        boolean deceleratingY = false;
        if (Math.abs(getAccelerationY()) < Math.abs(dragY + frictionY))
        {
            if (getAccelerationY() < 0 && (dragY + frictionY) > 0
                    || getAccelerationY() > 0 && (dragY + frictionY) < 0)
                deceleratingY = true;
        }

        Vec2 a_old = new Vec2(getAccelerationX(), getAccelerationY());
        setAccelerationX(getAccelerationX() + dragX + frictionX);
        setAccelerationY(getAccelerationY() + dragY + frictionY);

        //=====================================================================
        //     Move actor according to current acceleration and velocity
        //=====================================================================
        Vec2 a = getAcceleration();
        Vec2 v = getVelocity();
        Vec2 v_old = new Vec2(v.x, v.y);
        a.mul(deltaSec);
        setVelocity(v.add(a));
        if (deceleratingX) neutralizeVelocity(v_old, a_old, true);
        if (deceleratingY) neutralizeVelocity(v_old, a_old, false);

        Vec2 goal = getPosition();
        v.mul(deltaSec);
        goal.add(v);
        Vec2 orginalVel = triggerContacts(goal, entities); //returns null if the actor does not hit anything
        setPosition(goal);

        setState(determineState());
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
    /* Only for the y-velocity */
    private float initJumpVel = 0F;
    public void pressJump(boolean pressed)
    {
        if (pressed && !pressingJump)
        {
            pressedJumpTime = 1F;
            initJumpVel = getVelocityY();
            Print.green("initJumpVel: " + initJumpVel);
        }
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
        /*if (pressedJumpTime >0)
        {
            System.out.println("   determineState(): jump: touchEntity[DOWN]="+touchEntity[DOWN]);
        }*/
        //if (pressedJumpTime >0 && (touchEntity[DOWN] != null)) return State.RISE;
        if (inWater()) return State.SWIM;
        else if (touchEntity[DOWN] != null)
        {
            if (dirVert == Direction.DOWN)
            {
                if (getVelocityX() >= minCrawlSpeed && dirHoriz == Direction.RIGHT) return State.CRAWL;
                else if (getVelocityX() <= -minCrawlSpeed && dirHoriz == Direction.LEFT) return State.CRAWL;
                return State.CROUCH;
            }
            if (getVelocityX() > 0 && touchEntity[RIGHT] != null)
            {
                if (dirVert == Direction.UP || dirHoriz == Direction.RIGHT) return State.WALL_CLIMB;
                else return State.STAND;
            }
            if (getVelocityX() < 0 && touchEntity[LEFT] != null)
            {
                if (dirVert == Direction.UP || dirHoriz == Direction.LEFT) return State.WALL_CLIMB;
                else return State.STAND;
            }
            if (Math.abs(getVelocityX()) >= minRunSpeed)  return State.RUN;
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

    void setState(State state)
    {
        if (this.state == state) return;

        if (state == State.STAND)
        {
            setVelocity(Vec2.ZERO);
        }

        /*
        else if (state == State.RUN)
        {
           float vx = getVelocityX();
           float dir=0;
           if (pressingLeft) dir=-1;
           else if (pressingRight) dir=1;

           vx = vx + dir*state.startSpeed();
           if (Math.abs(vx)>state.maxSpeed()) vx = dir*state.maxSpeed();
           setVelocityX(vx);
        }*/

        else if (state == State.WALL_STICK)
        {
            setVelocity(Vec2.ZERO);
        }

        else if (state == State.WALL_CLIMB)
        {
            float vx = Math.abs(getVelocityX()); //change horz speed to vertical
            float vy = getVelocityY();
            setVelocity(0, vy + vx);
        }


        /* Temporary */
        Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");
        //Print.blue("dirPrimary: " + dirPrimary + ", dirVertical: " + dirVertical + "\n");

        this.state = state;
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
            int edge = entity.getTouchEdge(this, goal);
            if (edge < 0) continue;

            if (orginalVel == null) orginalVel = this.getVelocity();
            setVelocity(Vec2.ZERO);
            entity.setTriggered(true);
            touchEntity[edge] = entity;


            if (edge == DOWN) goal.y = entity.getTopEdge() - getHeight() / 2;
            else if (edge == LEFT) goal.x = entity.getRightEdge() + getWidth() / 2;
            else if (edge == RIGHT) goal.x = entity.getLeftEdge() - getWidth() / 2;
        }
        return orginalVel;
    }

    /**
     *  Call this method
     */
    private void neutralizeVelocity(Vec2 oldVecVel, Vec2 oldVecAcc,  boolean isHoriz)
    {
        float acc, newVel, oldVel;
        if (isHoriz)
        {
            acc = oldVecAcc.x;
            newVel = getVelocityX();
            oldVel = oldVecVel.x;
            /* If going left, decelerating to the right, and now is going right */
            if (oldVel < 0 && acc > 0 && newVel > 0) setVelocityX(0F);
            /* If going right, decelerating to the left, and now is going left */
            if (oldVel > 0 && acc < 0 && newVel < 0) setVelocityX(0F);
        }
        else
        {
            acc = oldVecAcc.y;
            newVel = getVelocityY();
            oldVel = oldVecVel.y;
            /* If going up, decelerating to the down, and now is going down */
            if (oldVel < 0 && acc > 0 && newVel > 0) setVelocityY(0F);
            /* If going down, decelerating to the up, and now is going up */
            if (oldVel > 0 && acc < 0 && newVel < 0) setVelocityY(0F);
        }
    }

    /*=======================================================================*/
    /* Variables that are set by the character's stats                       */
    /*=======================================================================*/

    /* This is the highest speed the player can be running before changing
     * their state to TUMBLE. */
    private float maxRunSpeed = 9F;

    /* This is the highest speed the player can get from running alone.
     * They can go faster while running with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float topRunSpeed = 7F;

    /* This is the lowest speed the player can be running before changing
     * their state to STAND. */
    private float minRunSpeed = 0.5F;

    /* This is the speed the player start with when transitioning states from
     * STAND to RUN. */
    private float initRunSpeed = 5F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is running on the ground. */
    private float runAccel = 3F;

    /* This is the highest speed the player can be crawling or crouching
     * before changing their state to TUMBLE. */
    private float maxCrawlSpeed = 3F;

    /* This is the highest speed the player can get from crawling alone.
     * They can go faster while crawling with the help of external influences,
     * such as going down a slope or being pushed by a faster object. */
    private float topCrawlSpeed = 1F;

    /* This is the lowest speed the player can be crawling before changing
     * their state to CROUCH. */
    private float minCrawlSpeed = 0.5F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is crawling on the ground. */
    private float crawlAccel = 1F;

    /* This is the highest speed the player can be sliding before changing
     * their state to TUMBLE. */
    private float maxSlideSpeed = 12F;

    /* This is the lowest speed the player can be sliding before changing
     * their state to PRONE. */
    private float minSlideSpeed = 2F;

    /* This is the highest speed the player can be climbing before changing
     * their state to RISE. */
    private float maxClimbSpeed = 7F;

    /* This is the highest speed the player can be skidding down a wall before
     * changing their state to FALL. */
    private float maxStickSpeed = 12F;

    // This is the acceleration that is applied to the player when on a wall.
    private float climbAccel = 5F;

    /* This is the highest speed the player can move in the air. */
    private float maxAirSpeed = 25F;

    /* This is the highest speed the player can get from moving themselves in
     * the air. They can go faster in the air with the help of external
     * influences such as wind or being pushed by a faster object. */
    private float topAirSpeed = 7F;

    /* This is the acceleration that is applied to the player when dirPrimary
     * is not null and the player is airborne. */
    private float airAccel = 1F;

    /* This is the highest speed the player can move in water. */
    private float maxSwimSpeed = 3F;

    /* A variable "topSwimSpeed" won't be needed because the drag underwater
     * will be high enough to cap the player's speed. */

    /* This is the acceleration that is applied to the player when in water. */
    private float swimAccel = 1F;

    /* The velocity used to jump */
    private float jumpVel = 10F;

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
}