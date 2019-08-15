package Gameplay;

import Gameplay.Characters.CharacterStat;
import Gameplay.Weapons.Sword;
import Gameplay.Weapons.Weapon;
import Gameplay.Weapons.WeaponStat;
import Menus.Gamepad;
import Menus.Main;
import Util.DebugEnum;
import Util.Print;
import Util.Reactor;
import Util.Vec2;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Gameplay implements Reactor
{
    private int viewWidth, viewHeight;
    private GraphicsContext context;
    private AnimationTimer timer;

    private final Gamepad[] GAMEPADS;

    private ArrayList<Entity> entities;
    private ArrayList<Item> items;

    private Actor player1, player2;
    private long lastUpdateTime = -1;

    private float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY,
            cameraZoom, cameraZoomGoal, cameraZoomLerp = 0.05F;

    public Gameplay(Group root, GraphicsContext context, Gamepad[] gamepads)
    {
        this.context = context;
        this.viewWidth = (int) context.getCanvas().getWidth();
        this.viewHeight = (int) context.getCanvas().getHeight();

        GAMEPADS = gamepads;

        entities = new ArrayList<>();
        items = new ArrayList<>();

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

        for (Item item : items) { item.update(entities, deltaSec); }

        for (Item item : items)
        {
            if (item instanceof Weapon) ((Weapon) item).update(items);
        }

        /* Center the camera on the player
         * TODO: Make the camera move ahead of the player's headed direction */
        //cameraPosX = player1.getPosition().x;
        //cameraPosY = player1.getPosition().y;

        moveCamera(player1.getPosition().x, player1.getPosition().y, player1.getZoom(entities));

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
            player1.debug();
        }
        else if (code == KeyCode.LEFT)// && pressed)
        {
            //moveCamera(cameraPosX - 0.1F, cameraPosY, cameraZoom);
            player2.pressLeft(pressed);
        }
        else if (code == KeyCode.RIGHT)// && pressed)
        {
            //moveCamera(cameraPosX + 0.1F, cameraPosY, cameraZoom);
            player2.pressRight(pressed);
        }
        else if (code == KeyCode.UP)// && pressed)
        {
            //moveCamera(cameraPosX, cameraPosY - 0.1F, cameraZoom);
            player2.pressUp(pressed);
        }
        else if (code == KeyCode.DOWN)// && pressed)
        {
            //moveCamera(cameraPosX, cameraPosY + 0.1F, cameraZoom);
            player2.pressDown(pressed);
        }
        else if (code == KeyCode.NUMPAD0)
        {
            player2.pressJump(pressed);
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
            player1.pressLeft(pressed);
        }
        else if (code == KeyCode.D)
        {
            player1.pressRight(pressed);
        }
        else if (code == KeyCode.J)
        {
            player1.pressJump(pressed);
        }
        else if (code == KeyCode.W)
        {
            player1.pressUp(pressed);
        }
        else if (code == KeyCode.S)
        {
            player1.pressDown(pressed);
        }
        else if (code == KeyCode.SHIFT)
        {
            player1.pressShift(pressed);
        }
        else if (code == KeyCode.K)
        {
            player1.pressAttack(pressed, Actor.ATTACK_KEY_1);
        }
        else if (code == KeyCode.L)
        {
            player1.pressAttack(pressed, Actor.ATTACK_KEY_2);
        }
        else if (code == KeyCode.SEMICOLON)
        {
            player1.pressAttack(pressed, Actor.ATTACK_KEY_3);
        }
        else if (code == KeyCode.U)
        {
            player1.pressAttackMod(pressed);
        }
        else if (code == KeyCode.N)
        {
            player2.pressJump(pressed);
        }
        else if (code == KeyCode.M)
        {
            player2.pressAttack(pressed, Actor.ATTACK_KEY_1);
        }
        else if (code == KeyCode.COMMA)
        {
            player2.pressAttack(pressed, Actor.ATTACK_KEY_2);
        }
        else if (code == KeyCode.PERIOD)
        {
            player2.pressAttack(pressed, Actor.ATTACK_KEY_3);
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) { }

    @Override
    public void mouse(int x, int y) { }

    private void queryGamepads()
    {
        //GAMEPADS[0].query(player1);
        GAMEPADS[0].query(player2);
    }

    /**
     * Until we utilize sprites, we'll test the game by drawing shapes that match the
     * blocks' hitboxes. The blocks' colors will help indicate what state they're in.
     */
    private void drawEntity(Entity entity)
    {
        Image sprite = entity.getSprite();
        if (sprite != null)
        {
            // TODO: Draw the sprite using entity.getSprite()
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
        addEntity(new CameraZone(0, -1, 50F, 4F, 100));
        addEntity(new CameraZone(0, -3, 50F, 4F, 100));

        addEntity(new Block(0, 2, 50F, 2F, Entity.ShapeEnum.RECTANGLE, new String[]{}));
        addEntity(new Block(5.5F, 1F, 2F, 0.6F, Entity.ShapeEnum.RECTANGLE, new String[]{}));
        //addEntity(new Block(-10, 0, 9F, 2F, Entity.ShapeEnum.RECTANGLE));
        //addEntity(new Block(-8, -2.5F, 6F, 3F, Entity.ShapeEnum.TRIANGLE_UP_R));
        addEntity(new Block(-10, 0.5F, 6F, 1F, Entity.ShapeEnum.TRIANGLE_UP_L, new String[]{}));
        addEntity(new Block(-4, -1F, 6F, 4F, Entity.ShapeEnum.TRIANGLE_UP_R, new String[]{}));

        addEntity(new Block(15, -0.5F, 6F, 1F, Entity.ShapeEnum.TRIANGLE_DW_R, new String[]{}));

        CharacterStat player1Stat = new CharacterStat(
                "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C");
        WeaponStat player1NaturalStat = new WeaponStat("C",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
        player1 = new Actor(player1Stat, player1NaturalStat,1F, -3F, .35f, .7f, 1F, new String[]{});

        WeaponStat swordStat = new WeaponStat("C",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
        Sword sword = new Sword(swordStat,0, -4, 0.45F, 0.075F, 0.1F, new String[]{});
        player1.equip(sword);
        addEntity(player1);
        addEntity(sword);

        CharacterStat player2Stat = new CharacterStat(
            "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C");
        WeaponStat player2NaturalStat = new WeaponStat("C",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
        player2 = new Actor(player2Stat, player2NaturalStat,1F, -5F, .35f, .7f, 1F, new String[]{});

        Sword sword2 = new Sword(swordStat,0, -4, 0.45F, 0.075F, 0.1F, new String[]{});
        player2.equip(sword2);
        //addEntity(player2);
        addEntity(sword2);

        Block water = new Block(8F, -1.75F, 3F, 5.5F, Entity.ShapeEnum.RECTANGLE, new String[]{});
        water.setLiquid(true);
        addEntity(water);

        //addEntity(new Item(1F, -5F, .5f, .5f));
    }

    /**
     * Call every frame. Movement and zooming should be smooth.
     */
    private void moveCamera(float posX, float posY, float zoom)
    {
        if (zoom != -1) cameraZoomGoal = zoom;//cameraZoom = zoom;
        if (Math.abs(cameraZoomGoal - cameraZoom) < Math.sqrt(cameraZoomLerp)) cameraZoom = cameraZoomGoal;
        else cameraZoom = ((cameraZoomGoal - cameraZoom) * cameraZoomLerp) + cameraZoom;

        cameraPosX = posX;
        cameraPosY = posY;

        cameraOffsetX = viewWidth / 2F / cameraZoom;
        cameraOffsetY = viewHeight / 2F / cameraZoom;
    }

    /**
     * Checks to make sure duplicates aren't being added.
     * Also adds the entity to a list of items if it's an Item or Actor.
     */
    private void addEntity(Entity entity)
    {
        if (entity instanceof Item)
        {
            if (items.contains(entity))
            {
                Print.red("Error: Attempted to add duplicate Item");
                return;
            }
            else
            {
                if (entity instanceof Actor)
                {
                    items.add((Actor) entity);
                    for (Entity ent : ((Actor) entity).getItems())
                        addEntity(ent);
                }
                else items.add((Item) entity);
            }
        }

        if (entities.contains(entity))
            Print.red("Error: Attempted to add duplicate Entity");
        else entities.add(entity);
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



