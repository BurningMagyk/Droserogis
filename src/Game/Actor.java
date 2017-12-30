package Game;

/**
 * act() and draw() are called in handle() in that order.
 * draw() needs coordinate specs because Game handles what appears in
 * the scene and where it goes relative to the rest of the actors.
 */
interface Actor
{
    void act();
    void draw(int x, int y);
}
