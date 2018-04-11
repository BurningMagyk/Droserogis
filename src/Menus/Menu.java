package Menus;

import Util.Reactor;
import javafx.scene.Group;
import javafx.scene.image.Image;

interface Menu extends Reactor
{
    int ARM_COUNTDOWN = 5;

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
        GAMEPLAY
    }

    Image getBackground();

    void startMedia();
    void stopMedia();
    void reset(Group group);
    void setup(Group group);
}
