package Gameplay;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class Entity
{
    BodyDef bodyDef = new BodyDef();
    PolygonShape polygonShape = new PolygonShape();
    Body body;
    FixtureDef fixtureDef = new FixtureDef();

    float width, height;

    Entity(World world, float xPos, float yPos, float width, float height, boolean dynamic)
    {
        this.width = width; this.height = height;

        bodyDef.position.set(xPos, yPos);
        bodyDef.type = dynamic ? BodyType.DYNAMIC : BodyType.KINEMATIC;
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(width, height);
        //fixtureDef.density = 0.005F;
        fixtureDef.friction = 0.1F;
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);

        body.setFixedRotation(true);
    }

    public Vec2 getPosition()
    {
        return body.getPosition();
    }

    public float getWidth() { return width * 2; }
    public float getHeight() { return height * 2; }

    boolean isActor()
    {
        return bodyDef.type == BodyType.DYNAMIC;
    }
}
