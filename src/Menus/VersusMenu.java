package Menus;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class VersusMenu implements Menu
{
    VersusMenu(GraphicsContext context, int width, int heigh)
    {

    }

    @Override
    public MenuEnum animateFrame(int framesToGo) {
        return MenuEnum.GAME;
    }

    @Override
    public void key(boolean pressed, KeyCode code) {

    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }

    @Override
    public void stopMusic() {

    }

    @Override
    public void reset() {

    }
}
