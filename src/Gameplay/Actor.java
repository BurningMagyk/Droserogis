package Gameplay;

import Util.Print;
import Util.Vec2;
import javafx.scene.paint.Color;

import java.util.ArrayList;

//================================================================================================================
// Key Commands:
//    WASD   : move left, right, up, down
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

    private boolean pressingLeft = false;
    private boolean pressingRight = false;
    private boolean pressingUp = false;
    private boolean pressingDown = false;

    private float pressedJumpTime = 0;
    private float gravity = 9.8f;
    private float airDrag = 0.25F;
    private float waterDrag = 1F;

    void debug()
    {
        Print.blue(state);
    }

    @Override
    public Color getColor() { return state.getColor(); }

    Actor(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE, true);
    }

    /**
     * Called every frame to update the Actor's will.
     */
    void act(ArrayList<Entity> entities, float deltaSec)
    {
        if (pressedJumpTime > 0) pressedJumpTime -= deltaSec;
        //triggerContacts(entities);

        //Location and velocity carry over from frame to frame.
        // Acceleration, however exists only when there is a force. Thus, each frame, we set acceleration to 0,
        //   figure out which forces are acting on it and add in acceleration for those forces.
        setAcceleration(0,0);

        if (!state.isGrounded()) setAccelerationY(gravity);

        float vx = getVelocityX();

        if (state.isGrounded())
        {
            if (dirHoriz == Direction.LEFT)
            {
                if (vx > -topRunSpeed) vx -= runAccel * deltaSec;
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (vx < topRunSpeed) vx += runAccel * deltaSec;
            }

            setVelocityX(vx);
        }

        else if (state.isAirborne())
        {
            if (dirHoriz == Direction.LEFT)
            {
                if (vx > -topAirSpeed) vx -= airAccel * deltaSec;
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (vx < topAirSpeed) vx += airAccel * deltaSec;
            }

            if (vx > maxAirSpeed) vx = maxAirSpeed;
            else if (vx < -maxAirSpeed) vx = -maxAirSpeed;

            setVelocityX(vx);
        }

        else if (state == State.SWIM)
        {
            float vy = getVelocityY();

            if (dirHoriz == Direction.LEFT)
            {
                if (vx > -maxSwimSpeed) vx -= swimAccel * deltaSec;
            }
            else if (dirHoriz == Direction.RIGHT)
            {
                if (vx < maxSwimSpeed) vx += swimAccel * deltaSec;
            }
            if (dirVert == Direction.UP)
            {
                if (vy > -maxSwimSpeed) vy -= swimAccel * deltaSec;
            }
            else if (dirVert == Direction.DOWN)
            {
                if (vy < maxSwimSpeed) vy += swimAccel * deltaSec;
            }

            setVelocity(vx, vy);
        }

        else if (state == State.WALL_CLIMB)
        {
            float vy = getVelocityY();
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
            setVelocityY(vy);
        }

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
            frictionX = touchEntity[DOWN].getFriction() + getFriction();
            frictionX *= -getVelocityX();
        }
        else if (state.isOnWall())
        {
            if (touchEntity[LEFT] == null)
                frictionY = touchEntity[RIGHT].getFriction() + getFriction();
            else frictionY = touchEntity[LEFT].getFriction() + getFriction();
            frictionY *= -getVelocityY();
        }

        setAccelerationX(getAccelerationX() + dragX + frictionX);
        setAccelerationY(getAccelerationY() + dragY + frictionY);

        //System.out.println("vel="+getVelocity() + ",  dragX="+dragX +  ",  dragY="+dragY);

        Vec2 a = getAcceleration();
        Vec2 v = getVelocity();
        a.mul(deltaSec);
        setVelocity(v.add(a));

        Vec2 goal = getPosition();
        v.mul(deltaSec);
        goal.add(v);
        triggerContacts(goal, entities); //Note: this method will modify goal so that it does not pass into an object.
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
    public void pressJump(boolean pressed)
    {
        if (pressedJumpTime >0) { return; }
        else pressedJumpTime = 1.0f;
    }

    private enum Direction
    {
        UP { boolean vertical() { return true; } int dirToNum() { return -1; } Direction opposite() { return DOWN; } },
        LEFT { boolean horizontal() { return true; } int dirToNum() { return -1; } Direction opposite() { return RIGHT; } },
        DOWN { boolean vertical() { return true; } int dirToNum() { return 1; } Direction opposite() { return UP; } },
        RIGHT { boolean horizontal() { return true; } int dirToNum() { return 1; } Direction opposite() { return LEFT; } };
        boolean vertical() { return false; }
        boolean horizontal() { return false; }
        abstract int dirToNum();
        Direction opposite() { return null; }
    }

    private State determineState()
    {
        if (pressedJumpTime >0)
        {
            System.out.println("   determineState(): jump: touchEntity[DOWN]="+touchEntity[DOWN]);
        }
        if (pressedJumpTime >0 && (touchEntity[DOWN] != null)) return State.RISE;
        if ((!state.isGrounded()) && (touchEntity[DOWN] != null))
        {
            //System.out.println("  determineState(): state="+state   +"  , touchEntity[DOWN]="+touchEntity[DOWN]);
            return State.STAND;
        }

        //if (pressingRight && touchEntity[RIGHT]!=null) pressingRight = false;
        //if (pressingLeft && touchEntity[LEFT]!=null) pressingLeft = false;
        //if (pressingUp && touchEntity[UP]!=null) pressingUp = false;
        if (pressingDown && touchEntity[DOWN]!=null)
        {
            if (state == State.STAND) return State.CROUCH;
        }

        if (state == State.WALL_CLIMB)
        {
            //System.out.println("     walls: "+ touchEntity[LEFT] +", " + touchEntity[RIGHT]);
            if (touchEntity[LEFT] == null && touchEntity[RIGHT] == null)
            {
                return State.FALL;
            }
        }


        //if (state == State.WALL_STICK)
        //{
        //    if (!pressingUp && !pressingDown && !pressingRight && !pressingLeft) return State.WALL_STICK;
        //    if (pressingUp || pressingDown) return State.WALL_CLIMB;
        //    if (pressingRight || pressingLeft) return State.RISE;
        //}


        if (state != State.WALL_CLIMB)
        {
            if (touchEntity[LEFT] != null || touchEntity[RIGHT] != null)
            {
                if (pressingUp || (touchEntity[DOWN] == null)) return State.WALL_CLIMB;
                //if (!pressingDown && !pressingUp && (touchEntity[DOWN] == null)) return State.WALL_STICK;
            }
        }

        if (state == State.STAND  || state == State.RUN)
        {
           if (touchEntity[DOWN] == null) return State.FALL;
        }

        if (state == State.STAND)
        {
            if (pressingLeft || pressingRight) return State.RUN;
        }

        if (state == State.RUN)
        {
            if (!pressingLeft && !pressingRight)
            {
                if (getVelocityX() < 0.01)
                {
                    return State.STAND;
                }
            }
        }
        return state;
    }





    void setState(State state)
    {
        if (state == State.STAND)
        {
            setVelocity(Vec2.ZERO);
        }

        if (this.state == state) return;


        else if (state == State.RISE)
        {
            if (this.state == State.WALL_CLIMB)
            {
                if (pressingLeft) setVelocityX(-State.RUN.startSpeed());
                else if (pressingRight) setVelocityX(State.RUN.startSpeed());
                setVelocityY(-state.startSpeed()/2);
            }
            else setVelocityY(-state.startSpeed());
            pressedJumpTime = 0;
        }

        else if (state == State.RUN)
        {
           float vx = getVelocityX();
           float dir=0;
           if (pressingLeft) dir=-1;
           else if (pressingRight) dir=1;

           vx = vx + dir*state.startSpeed();
           if (Math.abs(vx)>state.maxSpeed()) vx = dir*state.maxSpeed();
           setVelocityX(vx);
        }

        else if (state == State.WALL_STICK)
        {
            setVelocity(Vec2.ZERO);
        }

        else if (state == State.WALL_CLIMB)
        {
            float vx = Math.abs(getVelocityX()); //change horz speed to vertical
            float vy = getVelocityY();
            float dir=-1; //go up
            if (pressingDown) dir=1;
            if (pressingUp) vy = vy - State.WALL_CLIMB.startSpeed();
            setVelocity(0, vy + dir*vx);
            System.out.println("    setState(WALL_CLIMB): velocity="+getVelocity());
        }


        /* Temporary */
        Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");
        //Print.blue("dirPrimary: " + dirPrimary + ", dirVertical: " + dirVertical + "\n");

        this.state = state;
    }



    private void triggerContacts(Vec2 goal, ArrayList<Entity> entityList)
    {
        touchEntity[UP] = null;
        touchEntity[DOWN] = null;
        touchEntity[LEFT] = null;
        touchEntity[RIGHT] = null;
        for (Entity entity : entityList)
        {
            if (entity == this) continue;
            int edge = entity.getTouchEdge(this, goal);
            if (edge < 0) continue;

            entity.setTriggered(true);
            touchEntity[edge] = entity;

            if (edge == DOWN) goal.y = entity.getTopEdge() - getHeight() / 2;
            else if (edge == LEFT) goal.x = entity.getRightEdge() + getWidth() / 2;
            else if (edge == RIGHT) goal.x = entity.getLeftEdge() - getWidth() / 2;
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
                    float startSpeed() {return 0;}
                    float maxSpeed()  {return 0;}
                    float acceleration() {return 0;}
                },
        TUMBLE
                {
                    boolean isGrounded() { return true; }
                    boolean isIncapacitated() { return true; }
                    Color getColor() { return Color.GREY; }
                    float startSpeed() {return 2f;}
                    float maxSpeed()  {return 2f;}
                    float acceleration() {return 1f;}
                },
        BALLISTIC
                {
                    boolean isAirborne() { return true; }
                    boolean isIncapacitated() { return true; }
                    Color getColor() { return Color.BLACK; }
                    float startSpeed() {return 0f;}
                    float maxSpeed()  {return 0f;}
                    float acceleration() {return 0f;}
                },
        RISE
                {
                    boolean isAirborne() { return true; }
                    Color getColor() { return Color.CORNFLOWERBLUE; }
                    float startSpeed() {return 10f;}
                    float maxSpeed()  {return 25f;}
                    float acceleration() {return 0;}  //This could be used as a different way of reducing gravity
                },
        FALL
                {
                    boolean isAirborne() { return true; }
                    Color getColor() { return Color.CYAN; }
                    float startSpeed() {return 1f;}
                    float maxSpeed()  {return 10f;}
                    float acceleration() {return 9.8f;}
                },
        STAND
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.MAROON; }
                    float startSpeed() {return 0;}
                    float maxSpeed()  {return 0;}
                    float acceleration() {return 0;}
                },
        RUN
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.BROWN; }
                    float startSpeed() {return 5f;}
                    float maxSpeed()  {return 7f;}
                    float acceleration() {return 15;}
                },
        WALL_STICK
                {
                    boolean isOnWall() { return true; }
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.GREENYELLOW; }
                    float startSpeed() {return 0f;}
                    float maxSpeed()  {return 0f;}
                    float acceleration() {return 0;}
                },
        WALL_CLIMB
                {
                    boolean isOnWall() { return true; }
                    Color getColor() { return Color.LIGHTGREEN; }
                    float startSpeed() {return 2;}   //Keep whatever y speed you have and change x speed to y speed plus this boost.
                    float maxSpeed()  {return 12f;}  //can only reach this when climbing down (with gravity)
                    float acceleration() {return 5;} //less than gravity (9.8)
                },
        CROUCH
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.RED; }
                    float startSpeed() {return 0.7f;}
                    float maxSpeed()  {return 1f;}
                    float acceleration() {return 1f;}
                },
        SLIDE
                {
                    boolean isGrounded() { return true; }
                    Color getColor() { return Color.HOTPINK; }
                    float startSpeed() {return 2f;}
                    float maxSpeed()  {return 4f;}
                    float acceleration() {return 2f;}
                },
        SWIM
                {
                    Color getColor() { return Color.BLUE; }
                    float startSpeed() {return 0.5f;}
                    float maxSpeed()  {return 1f;}
                    float acceleration() {return 1f;}
                };

        boolean isOnWall() { return false; }
        boolean isAirborne() { return false; }
        boolean isGrounded() { return false; }
        boolean isIncapacitated() { return false; }
        Color getColor() { return Color.BLACK; }


        //Drag is a a force that acts in the opposite direction to velocity with a magnitude directly proportional
        //  to the magnitude of the velocity.
        //The acceleration caused by this force is applied independently in X and Y directions.
        //In these calculations, drag is expressed in meters/sec/sec where a drag of 1 means an object moving
        //  without other forces acting on it will come to rest in 1 second.
        float drag()
        {
            if (isAirborne()) return 0.25f;
            else return 0f;
        }

        abstract float startSpeed();
        abstract float maxSpeed();
        abstract float acceleration();

    }
}