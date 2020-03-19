package Gameplay.Entities.Weapons;

import Util.Vec2;

public class Orient
{
    private Vec2 pos;
    private float theta;

    Orient(Vec2 pos, float theta)
    {
        this.pos = new Vec2(pos.x, pos.y);
        this.theta = theta;
    }
    Orient(Orient orient) { this(orient.getPos(), orient.getTheta()); }

    float getX() { return pos.x; } void setX(float x) { pos.x = x; }
    float getY() { return pos.y; } void setY(float y) { pos.y = y; }
    private Vec2 getPos() { return pos; }
    float getTheta() { return theta; }
    void setTheta(float theta) { this.theta = theta; }
    void addTheta(float theta) { this.theta += theta; }

    void set(Orient orient)
    {
        pos.x = orient.getX();
        pos.y = orient.getY();
        theta = orient.getTheta();
    }

    void reduceTheta()
    {
        theta = Weapon.reduceTheta(theta);
    }

    Orient copy()
    {
        return new Orient(new Vec2(pos.x, pos.y), theta);
    }

    Orient copyOppHoriz() { return new Orient(new Vec2(-pos.x,  pos.y), theta - (float) Math.PI / 2); }

    float getMagnitude()
    {
        return (float) Math.sqrt(pos.x * pos.x + pos.y * pos.y);
    }
}
