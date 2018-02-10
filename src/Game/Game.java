package Game;

import Engine.Level;
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
    public GraphicsContext context;

    ArrayList<Creature> creatures;
    ArrayList<Block> blocks;

    Level testLevel;

    public Game(GraphicsContext context, int width, int height)
    {
        this.context = context;
        this.width = width;
        this.height = height;

        creatures = new ArrayList<>();
        blocks = new ArrayList<>();

        /* For testing */
        creatures.add(new Creature(context,100, 100, 10, 20));
        blocks.add(new Block(context, 100, 800, 150, 50));
        blocks.add(new Block(context, 250, 850, 150, 50));

        for (Creature creature : creatures)
        {
            creature.setBlocks(blocks);
        }

        testLevel = new Level(context);
    }

    @Override
    /**
     *  Call animateFrame() for everything in order of depth
     */
    public void handle(long now)
    {
        //if (true) return;

        /* TODO: Increment in-game clock */

        /* TODO: Animate horizon */

        /*for (Creature creature : creatures)
        {
            creature.act();
        }

        for (Block block : blocks)
        {
            block.draw(0, 0);
        }

        for (Creature creature : creatures)
        {
            creature.draw(0, 0);
        }*/

        //testLevel.draw(context);

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
        if (code == KeyCode.LEFT)
        {
            creatures.get(0).moveLeft(pressed);
        }
        if (code == KeyCode.RIGHT)
        {
            creatures.get(0).moveRight(pressed);
        }
        if (code == KeyCode.UP)
        {
            creatures.get(0).jump(pressed);
        }
        /* For testing */
        if (code == KeyCode.SPACE && pressed)
        {
            testLevel.draw(context);

            /*for (Creature creature : creatures)
            {
                creature.act();
            }

            for (Block block : blocks)
            {
                block.draw(0, 0);
            }

            for (Creature creature : creatures)
            {
                creature.draw(0, 0);
            }*/
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }
}



