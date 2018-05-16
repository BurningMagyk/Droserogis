package Gameplay;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class Actor extends Entity
{

    Actor(World world, float xPos, float yPos, float width, float height)
    {
        super(world, xPos, yPos, width, height, true);
    }

    void moveLeft(boolean pressed)
    {
        body.setLinearVelocity(new Vec2(-2F, body.getLinearVelocity().y));
    }
    void moveRight(boolean pressed)
    {
        body.setLinearVelocity(new Vec2(2F, body.getLinearVelocity().y));
    }
    void moveUp(boolean pressed)
    {
        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -2F));
    }
    void moveDown(boolean pressed)
    {
        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, 2F));
    }
}
