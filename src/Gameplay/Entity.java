package Gameplay;

import javafx.scene.paint.Color;
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
    boolean triggered;
    boolean triangular;

    Entity(World world, float xPos, float yPos, float width, float height, TriangleOrient triOri, boolean dynamic)
    {
        this.width = width; this.height = height;

        bodyDef.position.set(xPos, yPos);
        bodyDef.type = dynamic ? BodyType.DYNAMIC : BodyType.KINEMATIC;
        body = world.createBody(bodyDef);

        if (triOri == null)
        {
            polygonShape.setAsBox(width, height);
            triangular = false;
        }
        else if (triOri == TriangleOrient.UP_RIGHT)
        {
            Vec2 vectors[] = new Vec2[3];
            vectors[0] = new Vec2(xPos - (width / 2F), yPos + (height / 2F));
            vectors[1] = new Vec2(xPos - (width / 2F), yPos - (height / 2F));
            vectors[2] = new Vec2(xPos + (width / 2F), yPos + (height / 2F));
            polygonShape.set(vectors, 3);

            triangular = true;
        }

        //fixtureDef.density = 0.005F;
        fixtureDef.friction = 0.3F;
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

    Color getColor()
    {
        return Color.BLACK;
    }

    float getLeftEdge() { return getPosition().x - width; }
    float getRightEdge() { return getPosition().x + width; }
    float getTopEdge() { return getPosition().y - height; }
    float getBottomEdge() { return getPosition().y + height; }

    void resetFlags()
    {
        triggered = false;
    }
}
