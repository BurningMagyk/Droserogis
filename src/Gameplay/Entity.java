package Gameplay;

import org.jbox2d.common.Vec2;

public interface Entity
{
    /*
     * Formula:
     * (entityPos - cameraPos) * cameraScale = drawCoordinates
     *
     * cameraScale == 1 if it's the same size as the world,
     * otherwise it has a value greater than 1
     */

    Vec2 getPosition();

    float getWidth();
    float getHeight();

    boolean isActor();
}
