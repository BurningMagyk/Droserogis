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
        fixtureDef.density = 1;
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);
    }

    @Override
    public Vec2 getPosition()
    {
        return body.getPosition();
    }

    @Override
    public float getWidth() { return width; }
    @Override
    public float getHeight() { return height; }

    /* For debugging */
    public void test(){
        Print.blue(body.getPosition().x);
    }

    void moveLeft()
    {
        body.applyForceToCenter(new Vec2(-0.1F, 0));
    }
    void moveRight()
    {
        body.applyForceToCenter(new Vec2(0.1F, 0));
    }
    void moveUp()
    {
        body.applyForceToCenter(new Vec2(0, -0.1F));
    }
    void moveDown()
    {
        body.applyForceToCenter(new Vec2(0, 0.1F));
    }

    @Override
    public boolean isActor()
    {
        return true;
    }
}
