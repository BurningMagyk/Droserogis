package Gameplay;

import Util.GradeEnum;
import javafx.scene.paint.Color;

public class CameraZone extends Entity
{
    private float zoom;

    CameraZone(float xPos, float yPos, float width, float height, float zoom)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE);
        this.zoom = zoom;
    }

    float getZoom() { return zoom; }

    private Color color = Color.color(1, 1, 1, 0.2);
    @Override
    public Color getColor() { return color; }

    float getDistanceFromEdge(Actor actor)
    {
        return Math.min(
                Math.min(actor.getX() - getLeftEdge(), getRightEdge() - actor.getX()),
                Math.min(actor.getY() - getTopEdge(), getBottomEdge() - actor.getY()));
    }

    @Override
    public void damage(GradeEnum amount) {}
}
