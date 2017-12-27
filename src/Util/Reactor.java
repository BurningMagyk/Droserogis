package Util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public interface Reactor
{
    void key(boolean pressed, KeyCode code);
    void mouse(boolean pressed, MouseButton button, int x, int y);
    void mouse(int x, int y);
}
