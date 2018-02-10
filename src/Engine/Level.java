package Engine;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Level
{
    Scenery scenery;

    Block block;
    ArrayList<Block> blocks;
    Actor actor;

    Force force;

    public Level(GraphicsContext context)
    {
        scenery = new Scenery(context);

        force = new Force(false, false,
                false, false,
                false, false);
        force.setValues(null, null, null, null, null, 5);

        /* For testing */
        block = new Block(400, 800, 200, 100);
        blocks = new ArrayList<>();
        blocks.add(block);
        actor = new Actor(100, 100, 20, 50);
    }

    public void draw(GraphicsContext context)
    {
        actor.apply(force);
        actor.act(blocks);

        scenery.draw(0, 0);

        block.draw(context, 0, 0);

        actor.draw(context, 0, 0);
    }
}
