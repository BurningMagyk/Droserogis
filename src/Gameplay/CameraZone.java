package Gameplay;

import Util.GradeEnum;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CameraZone extends Entity
{
    private float zoom;

    CameraZone(float xPos, float yPos, float width, float height, float zoom)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE, null);
        this.zoom = zoom;
    }

    float getZoom() { return zoom; }

    private Color color = Color.color(1, 1, 1, 0.2);
    @Override
    public Color getColor() { return color; }

    float getDistanceFromEdge(Actor actor)
    {
        float w = actor.getWidth() / 2, h = actor.getHeight() / 2;
        return Math.min(
                Math.min(actor.getX() - w - getLeftEdge(), getRightEdge() - actor.getX() - w),
                Math.min(actor.getY() - h - getTopEdge(), getBottomEdge() - actor.getY() - h));
    }

    @Override
    public void damage(GradeEnum amount) {}
}
