package Gameplay;

import Util.Print;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class Actor implements Entity
{
    private BodyDef bodyDef = new BodyDef();
    private PolygonShape polygonShape = new PolygonShape();
    private Body body;
    private FixtureDef fixtureDef = new FixtureDef();

    private float width, height;

    Actor(World world, float xPos, float yPos, float width, float height)
    {
        this.width = width; this.height = height;

        bodyDef.position.set(xPos, yPos);
        bodyDef.type = BodyType.DYNAMIC;
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(width, height);
        fixtureDef.density = 0.005F;
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);

        body.setFixedRotation(true);
    }

    @Override
    public Vec2 getPosition()
    {
        return body.getPosition();
    }

    @Override
    public float getWidth() { return width * 2; }
    @Override
    public float getHeight() { return height * 2; }

    /* For debugging */
    public void test(){
        Print.blue(body.getPosition().x);
    }

    void moveLeft(boolean pressed)
    {
        //body.applyForceToCenter(new Vec2(-0.5F, 0));
        body.setLinearVelocity(new Vec2(-2F, body.getLinearVelocity().y));
    }
    void moveRight(boolean pressed)
    {
        //body.applyForceToCenter(new Vec2(-0.5F, 0));
        body.setLinearVelocity(new Vec2(2F, body.getLinearVelocity().y));
    }
    void moveUp(boolean pressed)
    {
        //body.applyForceToCenter(new Vec2(0, -0.5F));
        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, -2F));
    }
    void moveDown(boolean pressed)
    {
        //body.applyForceToCenter(new Vec2(0, 0.5F));
        body.setLinearVelocity(new Vec2(body.getLinearVelocity().x, 2F));
    }

    @Override
    public boolean isActor()
    {
        return true;
    }
}