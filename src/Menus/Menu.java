package Menus;

import Util.Reactor;

public interface Menu extends Reactor
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

    void stopMusic();
    void reset();
}
