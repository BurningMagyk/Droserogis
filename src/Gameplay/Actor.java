package Gameplay;

import org.jbox2d.dynamics.World;

/**
 * Created by Joel on 6/14/2018.
 */
public class Actor extends Entity
{
  Direction dirHoriz = null;
  Direction dirVert = null;
  /* actDirPrim should only have horizontal values */
  Direction dirPrim = null;

  State state = State.FALLING;

  Actor(World world, float xPos, float yPos, float width, float height, boolean dynamic)
  {
    super(world, xPos, yPos, width, height, dynamic);
  }

  boolean pressingLeft = false;
  boolean pressingRight = false;
  boolean pressingUp = false;
  boolean pressingDown = false;
  boolean pressingJump = false;
  void pressLeft(boolean pressed)
  {
    if (pressed)
    {
      /* If you're on a wall, it changes your secondary direction */
      if (state.isOnWall()) dirHoriz = Direction.LEFT;
      /* If you're not on a wall, it changes your primary direction */
      else dirPrim = Direction.LEFT;
      pressingLeft = true;
      return;
    }
    /* If you release the key when already moving left without a wall */
    if (!state.isOnWall() && dirPrim == Direction.LEFT)
    {
      if (pressingRight) dirPrim = Direction.RIGHT;
      else dirPrim = null;
    }
    /* If you release the key when already moving left with a wall */
    else if (state.isOnWall() && dirHoriz == Direction.LEFT)
    {
      if (pressingRight) dirHoriz = Direction.RIGHT;
      else dirHoriz = null;
    }
    pressingLeft = false;
  }
  void pressRight(boolean pressed)
  {
    if (pressed)
    {
      actDirHoriz = Direction.RIGHT;
      pressingRight = true;
      if (pressingJump && state.isWall() && state.isLeft())
      {
        pressingJump = false;
        //jump(true);
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
      if (pressingJump && (state.isWall()))
      {
        pressingJump = false;
        //jump(true);
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
      if (pressingJump && (state.isWall()))
      {
        pressingJump = false;
        //jump(true);
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
  void pressJump(boolean pressed)
  {
    //currentJump = currentJump.trigger(state.jumpSource(), pressed);
    // TODO: Parameter for trigger() should come from the State enum
    pressingJump = pressed;
  }

  private enum Direction
  {
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
    boolean horizontal() { return false; }
  }

  private enum State
  {
    PRONE_UP { boolean grounded() { return true; } },
    PRONE_DOWN { boolean grounded() { return true; } },
    RISE { boolean airborne() { return true; } },
    FALL { boolean airborne() { return true; } },
    STAND { boolean grounded() { return true; } },
    RUN { boolean grounded() { return true; } },
    WALL_STICK { boolean isOnWall() { return true; } },
    WALL_CLIMB { boolean isOnWall() { return true; } },
    CROUCH { boolean grounded() { return true; } },
    SLIDE { boolean grounded() { return true; } };
    boolean isOnWall() { return false; }
    boolean isAirborne() { return false; }
    boolean isGrounded() { return false; }
  }
}
