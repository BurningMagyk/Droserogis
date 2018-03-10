package Menus;

import Util.Reactor;
import javafx.scene.image.Image;

interface Menu extends Reactor
{
    MenuEnum animateFrame(int framesToGo);

    enum MenuEnum
    {
        START,
        TOP,
        STORYTIME,
        VERSUS,
        OPTIONS,
        GALLERY,
        QUIT,
        GAME
    }

    Image getBackground();

    void startMedia();
    void stopMedia();
    void reset();
}
