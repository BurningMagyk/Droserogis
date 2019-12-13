package Gameplay;

import Gameplay.Weapons.Weapon;
import Importer.LevelBuilder;
import Importer.ImageResource;
import Menus.Gamepad;
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
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

public class Gameplay implements Reactor
{
    private int viewWidth, viewHeight;
    private GraphicsContext context;
    private AnimationTimer timer;

    private final Gamepad[] GAMEPADS;

    private EntityCollection<Entity> entities = new EntityCollection();

    private long lastUpdateTime = -1;

    private float cameraPosX, cameraPosY, cameraPosLerp = 0.05F,
            cameraOffsetX, cameraOffsetY,
            cameraZoom, cameraZoomGoal, cameraZoomLerp = 0.05F;

    public Gameplay(Group root, GraphicsContext context, Gamepad[] gamepads)
    {
        this.context = context;
        this.viewWidth = (int) context.getCanvas().getWidth();
        this.viewHeight = (int) context.getCanvas().getHeight();

        GAMEPADS = gamepads;

        /* Set up initial position and zoom of the camera */
        moveCamera(0, 0, 100, 10, true);

        timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                mainGameLoop(now);
            }
        };
    }

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

        float deltaSec = (now - lastUpdateTime) * 1e-9f;
        lastUpdateTime = now;

        //System.out.println(now);
        clearContext();

        context.setFill(Color.BLACK);

        queryGamepads();

        // triggerContacts() sets every entity's flags correctly only if they've all been reset
        for (Entity entity : entities) entity.resetFlags();

        for (Item item : entities.getDynamicItems()) { item.update(entities, deltaSec); }

        for (Item item : entities.getItemList()) { item.update(entities.getDynamicItems()); }

        /* Center the camera on the player
         * TODO: Make the camera move ahead of the player's headed direction */
        //cameraPosX = player1.getPosition().x;
        //cameraPosY = player1.getPosition().y;

        Actor player1 = entities.getPlayer(0);
        moveCamera(player1.getPosition().x, player1.getPosition().y,
                player1.getZoom(entities.getCameraZoneList()), player1.getTopSpeed(), player1.shouldVertCam());

        /* Draw all entities after they've been moved and their flags have been set */
        for (Entity entity : entities) drawEntity(entity);

        // Testing
        GLFWGamepadState gamepadState = GLFWGamepadState.create();
        glfwGetGamepadState(GLFW_JOYSTICK_1, gamepadState);
        //Print.blue(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A));
    }

    @Override
    public void key(boolean pressed, KeyCode code)
    {
        if (code == KeyCode.ESCAPE)
        {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
            Platform.exit();
            System.exit(0);
        }
        else if (code == KeyCode.ENTER && pressed)
        {
            entities.getPlayer(0).debug();
        }
        else if (code == KeyCode.LEFT)// && pressed)
        {
            //moveCamera(cameraPosX - 0.1F, cameraPosY, cameraZoom);
            entities.getPlayer(1).pressLeft(pressed);
        }
        else if (code == KeyCode.RIGHT)// && pressed)
        {
            //moveCamera(cameraPosX + 0.1F, cameraPosY, cameraZoom);
            entities.getPlayer(1).pressRight(pressed);
        }
        else if (code == KeyCode.UP)// && pressed)
        {
            //moveCamera(cameraPosX, cameraPosY - 0.1F, cameraZoom);
            entities.getPlayer(1).pressUp(pressed);
        }
        else if (code == KeyCode.DOWN)// && pressed)
        {
            //moveCamera(cameraPosX, cameraPosY + 0.1F, cameraZoom);
            entities.getPlayer(1).pressDown(pressed);
        }
        else if (code == KeyCode.NUMPAD0)
        {
            entities.getPlayer(1).pressJump(pressed);
        }
        else if (code == KeyCode.A)
        {
            entities.getPlayer(0).pressLeft(pressed);
        }
        else if (code == KeyCode.D)
        {
            entities.getPlayer(0).pressRight(pressed);
        }
        else if (code == KeyCode.J)
        {
            entities.getPlayer(0).pressJump(pressed);
        }
        else if (code == KeyCode.W)
        {
            entities.getPlayer(0).pressUp(pressed);
        }
        else if (code == KeyCode.S)
        {
            entities.getPlayer(0).pressDown(pressed);
        }
        else if (code == KeyCode.SHIFT)
        {
            entities.getPlayer(0).pressShift(pressed);
        }
        else if (code == KeyCode.K)
        {
            entities.getPlayer(0).pressAttack(pressed, Actor.ATTACK_KEY_1);
        }
        else if (code == KeyCode.L)
        {
            entities.getPlayer(0).pressAttack(pressed, Actor.ATTACK_KEY_2);
        }
        else if (code == KeyCode.SEMICOLON)
        {
            entities.getPlayer(0).pressAttack(pressed, Actor.ATTACK_KEY_3);
        }
        else if (code == KeyCode.U)
        {
            entities.getPlayer(0).pressAttackMod(pressed);
        }
        else if (code == KeyCode.N)
        {
            entities.getPlayer(1).pressJump(pressed);
        }
        else if (code == KeyCode.M)
        {
            entities.getPlayer(1).pressAttack(pressed, Actor.ATTACK_KEY_1);
        }
        else if (code == KeyCode.COMMA)
        {
            entities.getPlayer(1).pressAttack(pressed, Actor.ATTACK_KEY_2);
        }
        else if (code == KeyCode.PERIOD)
        {
            entities.getPlayer(1).pressAttack(pressed, Actor.ATTACK_KEY_3);
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) { }

    @Override
    public void mouse(int x, int y) { }

    private void queryGamepads()
    {
        //GAMEPADS[0].query(entities.getPlayer(0));
        //GAMEPADS[0].query(entities.getPlayer(1));
    }

    /**
     * Until we utilize sprites, we'll test the game by drawing shapes that match the
     * blocks' hitboxes. The blocks' colors will help indicate what state they're in.
     */
    private void drawEntity(Entity entity)
    {
        //TODO: Right now the image loader loads every image size 35x70
        //TODO: Java doesn't like resizing images after you've loaded them, but it doesn't mind doing so at load time
        ImageResource sprite = entity.getSprite();
        if (sprite != null)
        {
            /*double xPos = (entity.getPosition().x - cameraPosX + cameraOffsetX) * cameraZoom;
            xPos = xPos - Sprite.getRequestedWidth() / 2; //this is set in the Importer

            double yPos = (entity.getPosition().y - cameraPosY + cameraOffsetY) * cameraZoom;
            yPos = yPos - Sprite.getRequestedHeight() / 2; //this is set in the Importer

            context.drawImage(Sprite,xPos,yPos);*/

            sprite.draw((entity.getX() - entity.getWidth() / 2 - cameraPosX + cameraOffsetX) * cameraZoom,
                    (entity.getY() - entity.getHeight() / 2 - cameraPosY + cameraOffsetY) * cameraZoom,
                    entity.getWidth() * cameraZoom, entity.getHeight() * cameraZoom);
        }
        else
        {
            context.setFill(entity.getColor());

            if (entity.getShape().isTriangle())
            {
                double[] xPos = new double[3];
                double[] yPos = new double[3];

                for (int i = 0; i < 3; i++)
                {
                    xPos[i] = (entity.getVertexX(i) - cameraPosX + cameraOffsetX) * cameraZoom;
                    yPos[i] = (entity.getVertexY(i) - cameraPosY + cameraOffsetY) * cameraZoom;
                }
                context.fillPolygon(xPos, yPos, 3);
            }
            else if (entity.getShape() == Entity.ShapeEnum.RECTANGLE)
            {
                if (entity instanceof Weapon)
                {
                    Vec2[] c = ((Weapon) entity).getShapeCorners();
                    double[] xCorners = {c[0].x, c[1].x, c[2].x, c[3].x};
                    double[] yCorners = {c[0].y, c[1].y, c[2].y, c[3].y};
                    for (int i = 0; i < xCorners.length; i++)
                    {
                        xCorners[i] = (xCorners[i] - cameraPosX + cameraOffsetX) * cameraZoom;
                        yCorners[i] = (yCorners[i] - cameraPosY + cameraOffsetY) * cameraZoom;
                    }
                    context.fillPolygon(xCorners, yCorners, 4);
                } else {
                    Vec2 pos = entity.getPosition();
                    context.fillRect(
                            (pos.x - entity.getWidth() / 2 - cameraPosX + cameraOffsetX) * cameraZoom,
                            (pos.y - entity.getHeight() / 2 - cameraPosY + cameraOffsetY) * cameraZoom,
                            entity.getWidth() * cameraZoom,
                            entity.getHeight() * cameraZoom);
                }
            }

            /* Draws vertical and horizontal lines through the middle for debugging */
            context.setFill(Color.BLACK);
            context.strokeLine(0, viewHeight / 2F, viewWidth, viewHeight / 2F);
            context.strokeLine(viewWidth / 2F, 0, viewWidth / 2F, viewHeight);
        }
    }

    /**
     * Sets up all of the blocks, entities, and players that appear in the level.
     * Should later utilize procedural generation.
     */
    private void buildLevels()
    {
        //entities = LevelBuilder.loadLevel("Resources/Levels/TestLevel.csv");
        //entities = LevelBuilder.loadLevel("D:/Games/Hermano Test Levels/01.csv");

        Actor testActor = new Actor(0, 0, Actor.EnumType.Lyra);
        entities.add(testActor);

        Block testBlock = new Block(0, 5, 30, 3,
                Entity.ShapeEnum.RECTANGLE, null);
        entities.add(testBlock);
    }

    /**
     * Call every frame. Movement and zooming should be smooth.
     */
    private void moveCamera(float posX, float posY, float zoom, float topSpeed, boolean updateVert)
    {
        if (zoom != -1) cameraZoomGoal = zoom;//cameraZoom = zoom;
        if (Math.abs(cameraZoomGoal - cameraZoom) < Math.sqrt(cameraZoomLerp)) cameraZoom = cameraZoomGoal;
        else cameraZoom = ((cameraZoomGoal - cameraZoom) * cameraZoomLerp) + cameraZoom;

        float _camPosLerp = (cameraPosLerp * topSpeed * 10) + cameraPosLerp;
        if (Math.abs(cameraPosX - posX) < _camPosLerp / 10) cameraPosX = posX;
        else cameraPosX += (posX - cameraPosX) * _camPosLerp;
        if (updateVert)
        {
            if (Math.abs(cameraPosY - posY) < _camPosLerp / 5) cameraPosY = posY;
            else cameraPosY += (posY - cameraPosY) * _camPosLerp * 2;
        }

        cameraOffsetX = viewWidth / 2F / cameraZoom;
        cameraOffsetY = viewHeight / 2F / cameraZoom;
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

    public static void main(String[] args)
    {
        Main.debugEnum = DebugEnum.GAMEPLAY;
        Main.main(args);
    }
}



