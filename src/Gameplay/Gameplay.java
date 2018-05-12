package Gameplay;

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
    private ArrayList<Entity> entities;

    private Actor player, player2;

    private static float cameraPosX, cameraPosY, cameraZoom;

    Gameplay(Group root, GraphicsContext context)
    {
        this.context = context;
        this.viewWidth = (int) context.getCanvas().getWidth();
        this.viewHeight = (int) context.getCanvas().getHeight();

        world = new World(new Vec2(0, 20));
        //world = new World(new Vec2(0, 0));
        entities = new ArrayList<>();

        cameraPosX = 0; cameraPosY = 0; cameraZoom = 100;
    }

    @Override
    public void start(/* Gameplay stats would go in here */)
    {
        // TODO: build levels based on game stats in parameters
        buildLevels();

        /* Start calling handle */
        super.start();
    }

    @Override
    public void handle(long now)
    {
        clearContext();

        context.setFill(Color.BLACK);
        for (Entity entity : entities) drawEntity(entity);

        world.step(1 / 60F,10,10);

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
        }
        if (code == KeyCode.LEFT && pressed)
        {
            cameraPosX -= 1;
        }
        if (code == KeyCode.RIGHT && pressed)
        {
            cameraPosX += 1;
        }
        if (code == KeyCode.UP && pressed)
        {
            cameraPosY -= 1;
        }
        if (code == KeyCode.DOWN && pressed)
        {
            cameraPosY += 1;
        }
        if (code == KeyCode.A && pressed)
        {
            player2.moveLeft(pressed);
        }
        if (code == KeyCode.D && pressed)
        {
            player2.moveRight(pressed);
        }
        if (code == KeyCode.W && pressed)
        {
            player2.moveUp(pressed);
        }
        if (code == KeyCode.S && pressed)
        {
            player2.moveDown(pressed);
        }
        if (code == KeyCode.SPACE && pressed)
        {
            cameraZoom -= 10;
            //cameraPosX -= 10F / cameraZoom;
            //cameraPosY -= 10F / cameraZoom;
        }
        if (code == KeyCode.SHIFT && pressed)
        {
            cameraZoom += 10;
            //cameraPosX += 10;
            //cameraPosY += 10;
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }

    private void drawEntity(Entity entity)
    {
        Vec2 position = entity.getPosition();
        /*context.fillRect((position.x - cameraPosX) * cameraZoom,
                         (position.y - cameraPosY) * cameraZoom,
                         entity.getWidth() * cameraZoom,
                         entity.getHeight() * cameraZoom);*/

        if (entity.isActor()) context.setFill(Color.MAROON);
        else context.setFill(Color.GOLDENROD);

        context.fillRect((position.x - cameraPosX - (entity.getWidth()) / 2) * cameraZoom,
                (position.y - cameraPosY - (entity.getHeight()) / 2) * cameraZoom,
                entity.getWidth() * cameraZoom,
                entity.getHeight() * cameraZoom);
    }

    private void buildLevels()
    {
        /*Actor actor = new Actor(world, 2F, 1.5F, 0.05F, 0.05F);
        entities.add(actor);*/

        /* For testing */
        /*entities.add(new Block(world, 3F, 1.2F, 0.5F, 0.5F));*/

        entities.add(new Block(world, 0F, 6F, 4F, 2.5F));
        entities.add(new Block(world, 10F, 10F, 4F, 4F));

        player = new Actor(world, 10F, 0.5F, 0.25F, 0.25F);
        entities.add(player);

        player2 = new Actor(world, 10F, 1F, 0.25F, 0.25F);
        entities.add(player2);
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



