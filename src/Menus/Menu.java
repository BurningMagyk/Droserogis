package Menus;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public interface Menu
{
    Menu animateFrame(int framesToGo);

    MenuEnum getMenuType();

    enum MenuEnum
    {
        START,
        TOP;
    }

    void key(boolean pressed, KeyCode code);
    void mouse(boolean pressed, MouseButton button, int x, int y);
    void mouse(int x, int y);
}
