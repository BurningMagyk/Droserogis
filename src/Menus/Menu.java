package Menus;

import Util.Reactor;

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

    void startMedia();
    void stopMedia();
    void reset();
}
