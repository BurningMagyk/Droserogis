package Game;

import Util.Print;
import Util.Reactor;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class Game extends AnimationTimer implements Reactor
{
    private int width, height;
    private GraphicsContext context;

    public Game(GraphicsContext context, int width, int height)
    {
        this.context = context;
        this.width = width;
        this.height = height;
    }

    @Override
    public void handle(long now)
    {

    }

    @Override
    public void key(boolean pressed, KeyCode code) {
        Print.blue("testing");
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }
}
