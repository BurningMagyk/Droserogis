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
    private Direction dirPrimary = null;
    private Direction dirSecondary = null;
    private Direction dirVertical = null;

    private Direction wallStickPos = null;

    private State state = State.FALL;


    private Entity[] touchEntity = new Entity[4];


    private boolean pressingLeft = false;
    private boolean pressingRight = false;
    private boolean pressingUp = false;
    private boolean pressingDown = false;
    private float pressedJumpTime = 0;

    private float gravity = 9.8f;


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
        if (pressedJumpTime > 0) pressedJumpTime-=deltaSec;
        //triggerContacts(entities);

        //Location and velocity carry over from frame to frame.
        // Acceleration, however exists only when there is a force. Thus, each frame, we set acceleration to 0,
        //   figure out which forces are acting on it and add in acceleration for those forces.
        setAcceleration(0,0);

        if (!state.isGrounded()) setAccelerationY(gravity);

        if (state == State.RUN)
        {
            if (pressingLeft)
            {
                float vx = getVelocityX() - state.acceleration();
                if (vx < -state.maxSpeed()) vx = -state.maxSpeed();
                setVelocityX(vx);
            }
            if (pressingRight)
            {
                float vx = getVelocityX() + state.acceleration();
                if (vx > state.maxSpeed()) vx = state.maxSpeed();
                setVelocityX(vx);
            }
        }


        float dragX = -state.drag()*getVelocityX();
        float dragY = -state.drag()*getVelocityY();
        setAccelerationX(getAccelerationX() + dragX);
        setAccelerationY(getAccelerationY() + dragY);

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
            if (state.isOnWall()) dirSecondary = Direction.LEFT;
            /* It changes your primary direction regardless */
            dirPrimary = Direction.LEFT;
        }
        /* If you release the key when already moving left */
        else if (dirPrimary == Direction.LEFT)
        {
            if (pressingRight) dirPrimary = Direction.RIGHT;
            else dirPrimary = null;
            /* If you release the key when already moving left with a wall */
            if (state.isOnWall())
            {
                if (pressingRight) dirSecondary = Direction.RIGHT;
                else dirSecondary = null;
            }
        }
        pressingLeft = pressed;
    }



    public void pressRight(boolean pressed)
    {
        if (pressed)
        {
            /* If you're on a wall, it changes your secondary direction */
            if (state.isOnWall()) dirSecondary = Direction.RIGHT;
            /* It changes your primary direction regardless */
            dirPrimary = Direction.RIGHT;

        }
        /* If you release the key when already moving right */
        else if (dirPrimary == Direction.RIGHT)
        {
            if (pressingLeft) dirPrimary = Direction.LEFT;
            else dirPrimary = null;
            /* If you release the key when already moving right with a wall */
            if (state.isOnWall())
            {
                if (pressingLeft) dirSecondary = Direction.LEFT;
                else dirSecondary = null;
            }
        }
        pressingRight = pressed;
    }


    public void pressUp(boolean pressed)
    {
        if (pressed) dirVertical = Direction.UP;
        else if (dirVertical == Direction.UP)
        {
            if (pressingDown) dirVertical = Direction.DOWN;
            else dirVertical = null;
        }
        pressingUp = pressed;
    }


    public void pressDown(boolean pressed)
    {
        if (pressed) dirVertical = Direction.DOWN;
        else if (dirVertical == Direction.DOWN)
        {
            if (pressingUp) dirVertical = Direction.UP;
            else dirVertical = null;
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
        BALISTIC
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
            float acceleration() {return 4.5f;}  //This could be used as a different way of reducing gravity
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
            float acceleration() {return 2;}
        },
        WALL_STICK
        {
            boolean isOnWall() { return true; }
            Color getColor() { return Color.GREENYELLOW; }
            float startSpeed() {return 0f;}
            float maxSpeed()  {return 0f;}
            float acceleration() {return 0;}
        },
        WALL_CLIMB
        {
            boolean isOnWall() { return true; }
            Color getColor() { return Color.LIGHTGREEN; }
            float startSpeed() {return 0.5f;}
            float maxSpeed()  {return 1f;}
            float acceleration() {return 1;}
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
            if (isGrounded()) return 5f;
            else return 0.25f;
        }

        abstract float startSpeed();
        abstract float maxSpeed();
        abstract float acceleration();

    }



    private State determineState()
    {
        if (pressedJumpTime >0 && (touchEntity[DOWN] != null)) return State.RISE;

        if ((!state.isGrounded()) && (touchEntity[DOWN] != null))
        {
            System.out.println("  determineState(): state="+state   +"  , touchEntity[DOWN]="+touchEntity[DOWN]);
            return State.STAND;
        }

        if (state == State.STAND)
        {
            if (pressingLeft || pressingRight) return State.RUN;
        }

        if (state == State.RUN)
        {
            if (!pressingLeft && !pressingRight)
            {
                if (getVelocityX() < 0.01) return State.STAND;
            }
        }
        return state;
    }





    void setState(State state)
    {
        if (this.state == state) return;


        if (state == State.RISE)
        {
            setVelocityY(-state.startSpeed());
            pressedJumpTime = 0;
        }

        if (state == State.RUN)
        {
           float vx = getVelocityX();
           float dir=0;
           if (pressingLeft) dir=-1;
           else if (pressingRight) dir=1;

           vx = vx + dir*state.startSpeed();
           if (Math.abs(vx)>state.maxSpeed()) vx = dir*state.maxSpeed();
           setVelocityX(vx);
        }


        /* Temporary */
        Print.blue("Changing from state \"" + this.state + "\" to \"" + state + "\"");
        //Print.blue("dirPrimary: " + dirPrimary + ", dirVertical: " + dirVertical + "\n");

        this.state = state;
    }



    private void triggerContacts(Vec2 goal, ArrayList<Entity> entityList)
    {
        for (Entity entity : entityList)
        {
            if (entity == this) continue;
            if (!entity.isHit(this, goal)) continue;

            entity.setTriggered(true);

            if (getVelocityY() > 0)
            {
                float top =  entity.getTopEdge();
                if ((getY() + getHeight() / 2 < top) && (goal.y + getHeight() / 2 >= top))
                {
                    setVelocityY(0);
                    goal.y = (entity.getY() - entity.getHeight() / 1.999f) - getHeight() / 2;
                    touchEntity[DOWN] = entity;
                }
            }


            if (getVelocityX() > 0)
            {
                float left = entity.getLeftEdge();
                if ((getX() + getWidth() / 2 < left) && (goal.x + getWidth() / 2 >= left))
                {
                    setVelocityX(0);
                    goal.x = (entity.getX() - entity.getWidth() / 1.999f) - getWidth() / 2;
                    touchEntity[RIGHT] = entity;
                }
            }


            else if (getVelocityX() < 0)
            {
                float right = entity.getRightEdge();
                if ((getX() - getWidth() / 2 > right) && (goal.x - getWidth() / 2 <= right))
                {
                    setVelocityX(0);
                    goal.x = (entity.getX() + entity.getWidth() / 1.999f) + getWidth() / 2;
                    touchEntity[LEFT] = entity;
                }
            }

        }


        if (touchEntity[RIGHT] != null && getVelocityX() < 0)
        {
            touchEntity[RIGHT].setTriggered(false);
            touchEntity[RIGHT] = null;
        }

        if (touchEntity[LEFT] != null && getVelocityX() > 0)
        {
            touchEntity[LEFT].setTriggered(false);
            touchEntity[LEFT] = null;
        }

        if (touchEntity[UP] != null && getVelocityY()> 0)
        {
            touchEntity[UP].setTriggered(false);
            touchEntity[UP] = null;
        }

        if (touchEntity[DOWN] != null && getVelocityY() < 0)
        {
            touchEntity[DOWN].setTriggered(false);
            touchEntity[DOWN] = null;
        }
    }
}