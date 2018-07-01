package Gameplay;

import Menus.Main;
import Util.DebugEnum;
import Util.Reactor;
import Util.Vec2;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Gameplay implements Reactor
{
    private int viewWidth, viewHeight;
    private GraphicsContext context;
    private AnimationTimer timer;

    private float gravity = 9.8f;             //meters per sec per sec
    private Vec2 scale = new Vec2(1,1); //pixels per meter

    private ArrayList<Entity> entities;
    private ArrayList<Actor> actors;

    private Actor player;
    private long lastUpdateTime = -1;


    private static float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY, cameraZoom;

    public Gameplay(Group root, GraphicsContext context)
    {
      this.context = context;
      this.viewWidth = (int) context.getCanvas().getWidth();
      this.viewHeight = (int) context.getCanvas().getHeight();

      entities = new ArrayList<>();
      actors = new ArrayList<>();

      /* Set up initial position and zoom of the camera */
      moveCamera(0, 0, 100);


      timer = new AnimationTimer()
      {
        @Override
        public void handle(long now)
        {
          mainGameLoop(now);
        }
      };
    }

    //this.setOnMouseEntered(event ->
    //{
    //  if (state == STATE.PLACING) return;
    //  if (!hasData) return;
    //  this.setCursor(Cursor.CROSSHAIR);
    //});
    // Gameplay stats would go in here
    public void start()
      {
        buildLevels();

        timer.start();
      }

    private void mainGameLoop(long now)
    {
      if (lastUpdateTime < 0)
      {
        lastUpdateTime = now;
        return;
      }

      float deltaSec = (now - lastUpdateTime)*1e-9f;
      lastUpdateTime = now;

      //System.out.println(now);
      clearContext();

      context.setFill(Color.BLACK);

      // triggerContacts() sets every entity's flags correctly only if they've all been reset
     // for (Entity entity : entities) entity.resetFlags();

      for (Actor actor : actors) actor.act(entities, deltaSec);

      //for (Actor actor : actors) actor.move(entities, deltaSec);

      /* Center the camera on the player
       * TODO: Make the camera move ahead of the player's headed direction */
      cameraPosX = player.getPosition().x;
      cameraPosY = player.getPosition().y;

      /* Draw all entities after they've been moved and their flags have been set */
      for (Entity entity : entities) drawEntity(entity);

      /* Handle is called 60 times per second, so world-step should be 1/60
       * Parameters for velocityIterations and positionIterations may need adjusting */
      //world.step(1 / 60F,10,10);
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

        if (entity.getShape().isTriangle())
        {
            double xPos[] = new double[3];
            double yPos[] = new double[3];

            Vec2 cPos = entity.getPosition();
            for (int i = 0; i < 3; i++)
            {
                xPos[i] = (entity.getVertexX(i) + cPos.x - cameraPosX + cameraOffsetX) * cameraZoom;
                yPos[i] = (entity.getVertexY(i) + cPos.y - cameraPosY + cameraOffsetY) * cameraZoom;
            }
            context.fillPolygon(xPos, yPos, 3);

        }
        else if (entity.getShape() == Entity.ShapeEnum.RECTANGLE)
        {
            Vec2 position = entity.getPosition();
            context.fillRect(
                    (position.x - entity.getWidth()/2 - cameraPosX + cameraOffsetX)
                            * cameraZoom,
                    (position.y - entity.getHeight()/2 - cameraPosY + cameraOffsetY)
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
        addEntity(new Block(0, 0, 4F, 4F, Entity.ShapeEnum.RECTANGLE));
        addEntity(new Block(3, -4, 2F, 6F, Entity.ShapeEnum.RECTANGLE));
        //addEntity(new Block(-2, -3F, 2F, 2F, Entity.ShapeEnum.TRIANGLE_UP_R));
        addEntity(new Block(-2, -3F, 2F, 2F, Entity.ShapeEnum.RECTANGLE));

        player = new Actor(1F, -3F, .5f, .5f);
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



