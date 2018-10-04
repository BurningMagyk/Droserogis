package Util;

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

    public void normalize()
    {
        float r = (float)Math.sqrt(x*x+y*y);
        x /= r;
        y /= r;
    }

    public Vec2 mul(float a)
    {
        x *= a;
        y *= a;
        return this;
    }

    public Vec2 add(float a)
    {
        x += a;
        y += a;
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

    public double mag()
  {
      return Math.sqrt(x * x + y * y);
  }

    public void rotate(float theta)
    {
        float xx = (float) (x * Math.cos(theta) - y * Math.sin(theta));
        y = (float) (x * Math.sin(theta) + y * Math.cos(theta));
        x = xx;
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

    public Vec2 clone() { return new Vec2(x, y); }

    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}