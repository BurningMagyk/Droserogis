package Gameplay;

public interface Entity
{
    /*
     * Formula:
     * (entityPos - cameraPos) / cameraScale = drawCoordinates
     *
     * cameraScale == 1 if it's the same size as the world,
     * otherwise it has a value less than 1
     */
    void draw();
}
