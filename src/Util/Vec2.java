package Util;

public class Vec2
{
  public float x,y;
  public static final Vec2 ZERO = new Vec2(0,0);
  public static final Vec2 UP = new Vec2(0,1);
  public static final Vec2 DOWN = new Vec2(0,-1);
  public static final Vec2 LEFT = new Vec2(-1,0);
  public static final Vec2 RIGHT = new Vec2(1,0);

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

  public void mul(float a)
  {
    x *= a;
    y *= a;
  }


  public void add(float a)
  {
    x += a;
    y += a;
  }

  public void add(Vec2 v)
  {
    x += v.x;
    y += v.y;
  }


  public void rotate(float theta)
  {
    float xx = (float)(x*Math.cos(theta) - y*Math.sin(theta));
    y = (float)(x*Math.sin(theta) + y*Math.cos(theta));
    x = xx;
  }

}
