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
    private ArrayList<Actor> actors;

    private Actor player;

    private static float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY, cameraZoom;

    Gameplay(Group root, GraphicsContext context)
    {
        this.context = context;
        this.viewWidth = (int) context.getCanvas().getWidth();
        this.viewHeight = (int) context.getCanvas().getHeight();

        /* The parameter for the world determines the gravity */
        world = new World(new Vec2(0, 0));

        entities = new ArrayList<>();
        actors = new ArrayList<>();

        /* Set up initial position and zoom of the camera */
        moveCamera(0, 0, 100);
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
        for (Entity entity : entities) entity.resetFlags();
        /* triggerContacts() sets every entity's flags correctly only
         * if they've all been reset */
        for (Actor actor : actors) actor.act(entities);

        /* Center the camera on the player
         * TODO: Make the camera move ahead of the player's headed direction */
        cameraPosX = player.getPosition().x;
        cameraPosY = player.getPosition().y;

        /* Draw all entities after they've been moved and their flags have been set */
        for (Entity entity : entities) drawEntity(entity);

        /* Handle is called 60 times per second, so world-step should be 1/60
         * Parameters for velocityIterations and positionIterations may need adjusting */
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
        else if (code == KeyCode.ENTER && pressed)
        {
            player.debug();
        }
        else if (code == KeyCode.LEFT && pressed)
        {
            moveCamera(cameraPosX - 0.1F, cameraPosY, cameraZoom);
        }
        else if (code == KeyCode.RIGHT && pressed)
        {
            moveCamera(cameraPosX + 0.1F, cameraPosY, cameraZoom);
        }
        else if (code == KeyCode.UP && pressed)
        {
            moveCamera(cameraPosX, cameraPosY - 0.1F, cameraZoom);
        }
        else if (code == KeyCode.DOWN && pressed)
        {
            moveCamera(cameraPosX, cameraPosY + 0.1F, cameraZoom);
        }
        else if (code == KeyCode.Q && pressed)
        {
            moveCamera(cameraPosX, cameraPosY, cameraZoom - 5);
        }
        else if (code == KeyCode.E && pressed)
        {
            moveCamera(cameraPosX, cameraPosY, cameraZoom + 5);
        }
        else if (code == KeyCode.A)
        {
            player.pressLeft(pressed);
        }
        else if (code == KeyCode.D)
        {
            player.pressRight(pressed);
        }
        else if (code == KeyCode.J)
        {
            //player.jump(pressed);
            player.pressJump(pressed);
        }
        else if (code == KeyCode.W)
        {
            player.pressUp(pressed);
        }
        else if (code == KeyCode.S)
        {
            player.pressDown(pressed);
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) {

    }

    @Override
    public void mouse(int x, int y) {

    }

    /**
     * Until we utilize sprites, we'll test the game by drawing shapes that match the
     * blocks' hitboxes. The blocks' colors will help indicate what state they're in.
     */
    private void drawEntity(Entity entity)
    {
        context.setFill(entity.getColor());

        /* If entity has triangular shape */
        if (entity.triangular)
        {
            double xPos[] = new double[3];
            double yPos[] = new double[3];
            Vec2 points[] = entity.polygonShape.getVertices();
            Vec2 cPos = entity.getPosition();
            for (int i = 0; i < 3; i++)
            {
                xPos[i] = (points[i].x + cPos.x - cameraPosX + cameraOffsetX) * cameraZoom;
                yPos[i] = (points[i].y + cPos.y - cameraPosY + cameraOffsetY) * cameraZoom;

                xPos[i] = (points[i].x + cPos.x - cameraPosX + cameraOffsetX) * cameraZoom;
                yPos[i] = (points[i].y + cPos.y - cameraPosY + cameraOffsetY) * cameraZoom;
            }
            context.fillPolygon(xPos, yPos, 3);
        }
        /* If entity has rectangle shape */
        else
        {
            Vec2 position = entity.getPosition();
            context.fillRect(
                    (position.x - entity.width - cameraPosX + cameraOffsetX)
                            * cameraZoom,
                    (position.y - entity.height - cameraPosY + cameraOffsetY)
                            * cameraZoom,
                    entity.getWidth()
                            * cameraZoom,
                    entity.getHeight()
                            * cameraZoom);
        }

        /* Draws vertical and horizontal lines through the middle for debugging */
        context.setFill(Color.BLACK);
        context.strokeLine(0, viewHeight / 2F, viewWidth, viewHeight / 2F);
        context.strokeLine(viewWidth / 2F, 0, viewWidth / 2F, viewHeight);
    }

    /**
     * Sets up all of the blocks, entities, and players that appear in the level.
     * Should later utilize procedural generation.
     */
    private void buildLevels()
    {
        addEntity(new Block(world, 0, 0, 2F, 2F, null));
        addEntity(new Block(world, 3, -2, 1F, 3F, null));
        addEntity(new Block(world, -1, -1.5F, 2F, 2F, Block.Orient.UP_RIGHT));

        player = new Actor(world, 1F, -3F, 0.25F, 0.25F);
        addEntity(player);
    }

    /**
     * Call whenever the player(s) moves. Camera should be moved further in the direction
     * that the player is moving towards. Movement and zooming should be smooth.
     */
    private void moveCamera(float posX, float posY, float zoom)
    {
        cameraZoom = zoom;
        cameraPosX = posX;
        cameraPosY = posY;

        cameraOffsetX = viewWidth / 2F / cameraZoom;
        cameraOffsetY = viewHeight / 2F / cameraZoom;
    }

    private void addEntity(Entity entity)
    {
        if (entity.getClass() == Actor.class) actors.add((Actor) entity);

        entities.add(entity);
    }

    /**
     * Canvas is cleared at the beginning of every frame.
     */
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



