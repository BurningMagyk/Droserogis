/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay.Entities;

import javafx.scene.paint.Color;

public class CameraZone extends Entity
{
    private float zoom;

    public CameraZone(float xPos, float yPos, float width, float height, float zoom)
    {
        super(xPos, yPos, width, height, ShapeEnum.RECTANGLE, null);
        this.zoom = zoom;
    }

    public float getZoom() { return zoom; }
    public void setZoom(float zoom) { this.zoom = zoom; }

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
}
