package Game;

import Util.Reactor;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
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
    /**
     *  Call animateFrame() for everything in order of depth
     */
    public void handle(long now)
    {
        /* TODO: Animate sky */

        /* TODO: Animate horizon */

        /* TODO: Animate further entities */

        /* TODO: Animate closer entities */

        /* TODO: Animate subtitles */
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        if (code == KeyCode.ESCAPE)
        {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }

    /* Place needed classes here temporarily until decided where they belong */

    /**
     * Will mostly just draw a gradient.
     * Can be acted upon by certain Actors.
     */
    class Sky implements Actor
    {
        GraphicsContext context;
        Sky(GraphicsContext context)
        {
            this.context = context;
        }
        public void act(){}
        public void draw(int x, int y)
        {

        }
    }

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

    /**
     * Will be what the actors access to indicate a significant change.
     * Checked by the Game class in handle() alongside the Actors.
     */
    class Level
    {
        void change(){}
    }
}



