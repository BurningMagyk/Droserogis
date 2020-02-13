package Util;

import Gameplay.DirEnum;

public class Vec2
{
    public float x,y;
    public static final Vec2 ZERO = new Vec2(0,0);
    public static final Vec2 UP = new Vec2(0,1);
    public static final Vec2 DOWN = new Vec2(0,-1);
    public static final Vec2 LEFT = new Vec2(-1,0);
    public static final Vec2 RIGHT = new Vec2(1,0);

    private static double sin = 0, cos = 1;

    public Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 v)
    {
        this.x = v.x;
        this.y = v.y;
    }

    //Return a value so operations can be chained
    public Vec2 normalize()
    {
        float r = (float)Math.sqrt(x*x+y*y);
        x /= r;
        y /= r;
        return this;
    }

    public Vec2 mul(float a)
    {
        x *= a;
        y *= a;
        return this;
    }

    public Vec2 div(float a)
    {
        x /= a;
        y /= a;
        return this;
    }

    public Vec2 add(float a)
    {
        x += a;
        y += a;
        return this;
    }

    public Vec2 add(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2 add(Vec2 v)
    {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vec2 minus(Vec2 v)
    {
        return new Vec2(x - v.x, y - v.y);
    }

    //==============================================================================================================
    // Magnitude is the length of the vector. Magnitude is always >=0.
    //==============================================================================================================
    public double magnitude()
  {
      return Math.sqrt(x * x + y * y);
  }


    //==============================================================================================================
    // Like magnitude, this is always >=0.  Its only advantage over magnitude is that it is quicker to calculate.
    // a.magnitudeSquared() > b.magnitudeSquared() if and only if a.magnitude() > b.magnitude()
    // Thus, prefer to magnitude if all that is needed is greater than or equal to comparison.
    //==============================================================================================================
    public double magnitudeSquared() { return x * x + y * y; }



    //==============================================================================================================
    // Return this so operation can be chained.
    //==============================================================================================================
    public Vec2 rotate(float theta)
    {
        float xx = (float) (x * Math.cos(theta) - y * Math.sin(theta));
        y = (float) (x * Math.sin(theta) + y * Math.cos(theta));
        x = xx;
        return this;
    }

    public void rotate()
    {
        float xx = (float) (x * cos - y * sin);
        y = (float) (x * sin + y * cos);
        x = xx;
    }

    public static void setTheta(float theta)
    {
        sin = Math.sin(theta);
        cos = Math.cos(theta);
    }

    public Vec2 copy() { return new Vec2(x, y); }

    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

    public Vec2 bound(float min, float max)
    {
        if (x<min) x=min;
        else if (x>max) x=max;

        if (y<min) y=min;
        else if (y>max) y=max;

        return this;
    }


    public static float bound(float x, float min, float max)
    {
        if (x<min) x=min;
        else if (x>max) x=max;

        return x;
    }
}