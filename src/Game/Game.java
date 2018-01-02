package Game;

import Util.Print;
import Util.Reactor;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;

public class Game extends AnimationTimer implements Reactor
{
    private int width, height;
    private GraphicsContext context;

    Scenery scenery;
    ArrayList<Creature> creatures;
    ArrayList<Block> blocks;

    public Game(GraphicsContext context, int width, int height)
    {
        this.context = context;
        this.width = width;
        this.height = height;

        scenery = new Scenery(context);
        creatures = new ArrayList<>();
        blocks = new ArrayList<>();

        /* For testing */
        creatures.add(new Creature(context,100, 100, 100, 200));
        blocks.add(new Block(context, 100, 800, 150, 50));
    }

    @Override
    /**
     *  Call animateFrame() for everything in order of depth
     */
    public void handle(long now)
    {
        /* TODO: Increment in-game clock, might go in the Sky class */

        scenery.draw(0, 0);

        /* TODO: Animate horizon */

        blockCollisions();

        for (Creature creature : creatures)
        {
            creature.act();
            creature.draw(0, 0);
        }

        for (Block block : blocks)
        {
            block.draw(0, 0);
        }

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
        if (code == KeyCode.ENTER)
        {
            Print.blue(context.getCanvas().getWidth());
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }

    private void blockCollisions()
    {
        for(Creature creature : creatures)
        {
            Direction direction = creature.getDirection();
            int x1 = creature.xPos;
            int x2 = x1 + creature.width;
            int y1 = creature.yPos;
            int y2 = y1 + creature.height;

            for(Block block : blocks)
            {
                creature.collide(block.checkCollision(x1, x2, y1, y2, direction));
            }
        }
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



