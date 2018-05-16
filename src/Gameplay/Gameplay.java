package Gameplay;

import Menus.Main;
import Util.DebugEnum;
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

    private Actor player;

    private static float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY, cameraZoom;

    Gameplay(Group root, GraphicsContext context)
    {
        this.context = context;
        this.viewWidth = (int) context.getCanvas().getWidth();
        this.viewHeight = (int) context.getCanvas().getHeight();

        world = new World(new Vec2(0, 20));

        entities = new ArrayList<>();

        cameraPosX = 0; cameraPosY = 0; cameraZoom = 100;
        cameraOffsetX = viewWidth / 2F / cameraZoom;
        cameraOffsetY = viewHeight / 2F / cameraZoom;
    }

    @Override
    public void start(/* Gameplay stats would go in here */)
    {
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
            moveCamera(cameraPosX - 0.1F, cameraPosY, cameraZoom);
        }
        if (code == KeyCode.RIGHT && pressed)
        {
            moveCamera(cameraPosX + 0.1F, cameraPosY, cameraZoom);
        }
        if (code == KeyCode.UP && pressed)
        {
            moveCamera(cameraPosX, cameraPosY - 0.1F, cameraZoom);
        }
        if (code == KeyCode.DOWN && pressed)
        {
            moveCamera(cameraPosX, cameraPosY + 0.1F, cameraZoom);
        }
        if (code == KeyCode.Q && pressed)
        {
            moveCamera(cameraPosX, cameraPosY, cameraZoom - 5);
        }
        if (code == KeyCode.E && pressed)
        {
            moveCamera(cameraPosX, cameraPosY, cameraZoom + 5);
        }
        if (code == KeyCode.A && pressed)
        {
            player.moveLeft(pressed);
        }
        if (code == KeyCode.D && pressed)
        {
            player.moveRight(pressed);
        }
        if (code == KeyCode.W && pressed)
        {
            player.moveUp(pressed);
        }
        if (code == KeyCode.S && pressed)
        {
            player.moveDown(pressed);
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

        /* Different colors for debugging */
        if (entity.isActor()) context.setFill(Color.MAROON);
        else context.setFill(Color.GOLDENROD);

        context.fillRect(
                (position.x - cameraPosX + cameraOffsetX - (entity.getWidth()) / 2)
                        * cameraZoom,
                (position.y - cameraPosY + cameraOffsetY - (entity.getHeight()) / 2)
                        * cameraZoom,
                entity.getWidth()
                        * cameraZoom,
                entity.getHeight()
                        * cameraZoom);

        /* Draws vertical and horizontal lines through the middle for debugging */
        context.setFill(Color.BLACK);
        context.strokeLine(0, viewHeight / 2F, viewWidth, viewHeight / 2F);
        context.strokeLine(viewWidth / 2F, 0, viewWidth / 2F, viewHeight);
    }

    private void buildLevels()
    {
        entities.add(new Block(world, 0, 0, 2F, 2F));

        player = new Actor(world, 1F, -3F, 0.25F, 0.25F);
        entities.add(player);
    }

    private void moveCamera(float posX, float posY, float zoom)
    {
        cameraZoom = zoom;
        cameraPosX = posX;
        cameraPosY = posY;

        cameraOffsetX = viewWidth / 2F / cameraZoom;
        cameraOffsetY = viewHeight / 2F / cameraZoom;
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



