package Menus;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class TopMenu implements Menu
{
    private final GraphicsContext context;

    TopMenu(GraphicsContext context, int width, int height)
    {
        this.context = context;
    }

    @Override
    public Menu animateFrame()
    {
        context.fillText("Each day,\nwe stray further from God", 200, 300);
        return null;
    }

    @Override
    public MenuEnum getMenuType() {
        return null;
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
}
