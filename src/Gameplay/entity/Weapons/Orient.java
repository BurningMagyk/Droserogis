package Gameplay.entity.Weapons;

import Gameplay.entity.Weapon;
import Util.Vec2;

public class Orient
{
    private Vec2 pos;
    private float theta;

    public Orient(Vec2 pos, float theta)
    {
        this.pos = new Vec2(pos.x, pos.y);
        this.theta = theta;
    }
    public Orient(Orient orient) { this(orient.getPos(), orient.getTheta()); }

    public float getX() { return pos.x; } void setX(float x) { pos.x = x; }
    public float getY() { return pos.y; } void setY(float y) { pos.y = y; }
    private Vec2 getPos() { return pos; }
    public float getTheta() { return theta; }
    public void setTheta(float theta) { this.theta = theta; }
    public void addTheta(float theta) { this.theta += theta; }

    public void set(Orient orient)
    {
        pos.x = orient.getX();
        pos.y = orient.getY();
        theta = orient.getTheta();
    }

    public void reduceTheta()
    {
        theta = Weapon.reduceTheta(theta);
    }

    public Orient copy()
    {
        return new Orient(new Vec2(pos.x, pos.y), theta);
    }

    public Orient copyOppHoriz() { return new Orient(new Vec2(-pos.x,  pos.y), theta - (float) Math.PI / 2); }

    public float getMagnitude()
    {
        return (float) Math.sqrt(pos.x * pos.x + pos.y * pos.y);
    }
}
