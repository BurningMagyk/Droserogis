package Gameplay;

import Engine.Level;
import Menus.Main;
import Util.DebugEnum;
import Util.Print;
import Util.Reactor;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class Gameplay extends AnimationTimer implements Reactor
{
    private int viewWidth, viewHeight;
    public GraphicsContext context;

    private static World world;
    ArrayList<Entity> entities;

    private static double cameraPosX, cameraPosY, cameraZoom;

    Level testLevel;

    public Gameplay(Group root, GraphicsContext context)
    {
        this.context = context;
        this.viewWidth = (int) context.getCanvas().getWidth();
        this.viewHeight = (int) context.getCanvas().getHeight();

        world = new World(new Vec2(0, -9.8f));
        entities = new ArrayList<>();

        cameraPosX = 0; cameraPosY = 0; cameraZoom = 0;

        /* For testing */
        entities.add(new Block(context, world, 10, 10, 10, 10));

        //testLevel = new Level(context);
    }

    @Override
    public void start(/* Gameplay stats would go in here */)
    {
        // TODO: build levels based on game stats in parameters
        buildLevels();



        /* Start calling handle */
        super.start();
    }

    /**
     *  Call animateFrame() for everything in order of depth
     */
    @Override
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

        clearContext();

        context.setFill(Color.BLUE);
        context.fillRect(200, 200, 200, 200);

        for (Entity entity : entities) entity.draw();

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
            //creatures.get(0).moveLeft(pressed);
        }
        if (code == KeyCode.RIGHT)
        {
            //creatures.get(0).moveRight(pressed);
        }
        if (code == KeyCode.UP)
        {
            //creatures.get(0).jump(pressed);
        }
        /* For testing */
        if (code == KeyCode.SPACE && pressed)
        {
            ((Block) entities.get(0)).test();

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

    static double[] getCameraDrawPoint(double entityPosX, double entityPosY)
    {
        return new double[] {(entityPosX - cameraPosX) / cameraZoom,
                             (entityPosY - cameraPosY) / cameraZoom};
    }

    private void buildLevels()
    {

    }

    private void clearContext()
    {
        /* Clear canvas */
        context.clearRect(0, 0, context.getCanvas().getWidth(),
                context.getCanvas().getHeight());
    }

    public static void main(String args[])
    {
        Main.debugEnum = DebugEnum.GAMEPLAY;
        Main.main(args);
    }
}



