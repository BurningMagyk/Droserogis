/* Copyright (C) All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Robin Campos <magyk81@gmail.com>, 2018 - 2020
 */

package Gameplay;
//Game Title: The Lie Made Flesh
import Gameplay.Entities.*;
import Gameplay.Entities.Weapons.Weapon;
import Importer.AudioResource;
import Importer.LevelBuilder;
import Menus.Gamepad;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static org.lwjgl.glfw.GLFW.*;

public class Gameplay implements Reactor
{
    private int viewWidth, viewHeight;
    private GraphicsContext gfx;
    private AnimationTimer timer;
    private int frame = 0;
    private float fps = 0;
    private long fpsLastTime = 0;
    private int  fpsLastFrame = 0;

    private final Gamepad[] GAMEPADS;

    private EntityCollection<Entity> entityList = new EntityCollection();

    private long lastUpdateTime = -1;

    private float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY;
    private float cameraZoom, cameraZoomGoal, cameraZoomLerp = 0.05F;
    private RenderThread renderThread;

    private AudioResource audio;

    public Gameplay(Group root, GraphicsContext context, Gamepad[] gamepads)
    {

        gfx = context;
        this.viewWidth = (int) gfx.getCanvas().getWidth();
        this.viewHeight = (int) gfx.getCanvas().getHeight();

        GAMEPADS = gamepads;

        timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                mainGameLoop(now);
            }
        };

        /* Try importing music */
        audio = Main.IMPORTER.getAudio("robin_song_1.aiff");
    }

    // Gameplay stats would go in here
    public void start()
    {
        /**
         * Sets up all of the blocks, entities, and players that appear in the level.
         * Should later utilize procedural generation.
         */
        Font font = gfx.getFont();
        System.out.println ("Using Font " +font.getName());
        gfx.setFont(Font.font(font.getName(), FontWeight.BOLD, 18));
        renderThread = new RenderThread(gfx, viewWidth, viewHeight);
        entityList = LevelBuilder.loadLevel("Resources/Levels/TestLevel.csv");

        /* Set coveredDirs for every block */
        for (Entity entity : entityList)
        {
            if (entity instanceof Block)
            {
                for (Entity ent : entityList)
                {
                    if (ent instanceof Block)
                    {
                        if (ent.surroundsEitherX(entity))
                        {
                            if (ent.getY() > entity.getY() && ent.getTopEdge() <= entity.getBottomEdge() + 0.001)
                                entity.setTouchBlock(Entity.DOWN, (Block) ent);
                            if (ent.getY() < entity.getY() && ent.getBottomEdge() >= entity.getTopEdge() - 0.001)
                                entity.setTouchBlock(Entity.UP, (Block) ent);
                        }
                        if (ent.surroundsEitherY(entity))
                        {
                            if (ent.getX() > entity.getX() && ent.getLeftEdge() <= entity.getRightEdge() + 0.001)
                                entity.setTouchBlock(Entity.RIGHT, (Block) ent);
                            if (ent.getX() < entity.getX() && ent.getRightEdge() >= entity.getLeftEdge() - 0.001)
                                entity.setTouchBlock(Entity.LEFT, (Block) ent);
                        }
                    }
                }
            }
        }


        /* Set up initial position and zoom of the camera */
        moveCamera(0, 0, 100, true, 1);


        //System.out.println("Level Left Bounds: " + entities.getBoundsLeft());
        //System.out.println("Level Right Bounds: " + entities.getBoundsRight());
        //System.out.println("Level Top Bounds: " + entities.getBoundsTop());
        //System.out.println("Level Bottom Bounds: " + entities.getBoundsBottom());

        timer.start();

        audio.play();
    }

    private void mainGameLoop(long now)
    {
        if (lastUpdateTime < 0)
        {
            lastUpdateTime = System.nanoTime();
            fpsLastTime = System.nanoTime();
            fps = 60.0f;
            return;
        }
        long currentNano = System.nanoTime();
        float deltaSec = (float)((currentNano - lastUpdateTime) * 1e-9);

        lastUpdateTime = currentNano;
        frame++;
        if ((now - fpsLastTime) > 1e9)
        {
            fps = (frame - fpsLastFrame)/((currentNano-fpsLastTime)*1e-9f);
            fpsLastTime = currentNano;
            fpsLastFrame = frame;
        }
        deltaSec = 1.0f/fps;

        queryGamepads();

        for (Entity entity : entityList) entity.resetFlags();

        for (Weapon weapon : entityList.getWeaponList()) weapon.applyInflictions();

        for (Item item : entityList.getDynamicItems()) item.update(entityList, deltaSec);

        for (Weapon weapon : entityList.getWeaponList()) weapon.update(entityList.getDynamicItems());

        Actor player1 = entityList.getPlayer(0);
        float x = player1.getPosition().x;
        float y = player1.getPosition().y;
        moveCamera(x, y, 100, player1.shouldVertCam(), deltaSec);


        //Print.green("Camera: pos(" + cameraPosX + ", " + cameraPosY +")    offset(" + cameraOffsetX + ", " + cameraOffsetY + ")  cameraZoom="+cameraZoom);

        renderThread.renderAll(entityList, cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY, cameraZoom,1);

        gfx.setFill(Color.BLACK);
        gfx.fillText(String.format("%.1f fps", fps), 10, viewHeight-5);

        // Testing
        //GLFWGamepadState gamepadState = GLFWGamepadState.create();
        //glfwGetGamepadState(GLFW_JOYSTICK_1, gamepadState);
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
            entityList.getPlayer(0).debug();
            return;
        }

        if (!GAMEPADS[1].isConnected())
        {
            if (code == KeyCode.LEFT)// && pressed)
            {
                //moveCamera(cameraPosX - 0.1F, cameraPosY, cameraZoom);
                entityList.getPlayer(1).pressLeft(pressed);
                return;
            }
            else if (code == KeyCode.RIGHT)// && pressed)
            {
                //moveCamera(cameraPosX + 0.1F, cameraPosY, cameraZoom);
                entityList.getPlayer(1).pressRight(pressed);
                return;
            }
            else if (code == KeyCode.UP)// && pressed)
            {
                //moveCamera(cameraPosX, cameraPosY - 0.1F, cameraZoom);
                entityList.getPlayer(1).pressUp(pressed);
                return;
            }
            else if (code == KeyCode.DOWN)// && pressed)
            {
                //moveCamera(cameraPosX, cameraPosY + 0.1F, cameraZoom);
                entityList.getPlayer(1).pressDown(pressed);
                return;
            }
            else if (code == KeyCode.NUMPAD0)
            {
                entityList.getPlayer(1).pressJump(pressed);
                return;
            }
            else if (code == KeyCode.N)
            {
                entityList.getPlayer(1).pressJump(pressed);
                return;
            }
            else if (code == KeyCode.M)
            {
                entityList.getPlayer(1).pressAttack(pressed, Actor.ATTACK_KEY_1);
                return;
            }
            else if (code == KeyCode.COMMA)
            {
                entityList.getPlayer(1).pressAttack(pressed, Actor.ATTACK_KEY_2);
                return;
            }
            else if (code == KeyCode.PERIOD)
            {
                entityList.getPlayer(1).pressAttack(pressed, Actor.ATTACK_KEY_3);
                return;
            }
        }

        if (!GAMEPADS[0].isConnected())
        {
            if (code == KeyCode.A)
            {
                entityList.getPlayer(0).pressLeft(pressed);
            }
            else if (code == KeyCode.D)
            {
                entityList.getPlayer(0).pressRight(pressed);
            }
            else if (code == KeyCode.J)
            {
                entityList.getPlayer(0).pressJump(pressed);
            }
            else if (code == KeyCode.W)
            {
                entityList.getPlayer(0).pressUp(pressed);
            }
            else if (code == KeyCode.S)
            {
                entityList.getPlayer(0).pressDown(pressed);
            }
            else if (code == KeyCode.SHIFT)
            {
                entityList.getPlayer(0).pressShift(pressed);
            }
            else if (code == KeyCode.K)
            {
                entityList.getPlayer(0).pressAttack(pressed, Actor.ATTACK_KEY_1);
            }
            else if (code == KeyCode.L)
            {
                entityList.getPlayer(0).pressAttack(pressed, Actor.ATTACK_KEY_2);
            }
            else if (code == KeyCode.SEMICOLON)
            {
                entityList.getPlayer(0).pressAttack(pressed, Actor.ATTACK_KEY_3);
            }
            else if (code == KeyCode.U)
            {
                entityList.getPlayer(0).pressAttackMod(pressed);
            }
        }
    }

    @Override
    public void mouse(boolean pressed, MouseButton button, int x, int y) { }

    @Override
    public void mouse(int x, int y) { }

    private void queryGamepads()
    {
        GAMEPADS[0].query(entityList.getPlayer(0));
        GAMEPADS[1].query(entityList.getPlayer(1));
    }




    /**
     * Call every frame. Movement and zooming should be smooth.
     */
    private void moveCamera(float posX, float posY, float zoom, boolean updateVert, float deltaSec)
    {
        if (zoom != -1) cameraZoomGoal = zoom;
        if (Math.abs(cameraZoomGoal - cameraZoom) < Math.sqrt(cameraZoomLerp)) cameraZoom = cameraZoomGoal;
        else cameraZoom = ((cameraZoomGoal - cameraZoom) * cameraZoomLerp) + cameraZoom;

        //Prevent camera from moving to a location that views beyond the edge of the level
        //System.out.println("posX="+posX +"   viewWidth/2/cameraZoom="+viewWidth/2F/cameraZoom + "      left="+entities.getBoundsLeft() + "    right="+entities.getBoundsRight());
        if (posX - viewWidth/1.99f/cameraZoom < entityList.getBoundsLeft())
        {
            posX = (float) entityList.getBoundsLeft()+viewWidth/1.99f/cameraZoom;
        }
        else if (posX + viewWidth/1.99f/cameraZoom > entityList.getBoundsRight())
        {
            posX = (float) entityList.getBoundsRight() - viewWidth / 1.99f / cameraZoom;
        }
        if (posY - viewHeight/1.99f/cameraZoom < entityList.getBoundsTop())
        {
            posY = (float) entityList.getBoundsTop()+viewHeight/1.99f/cameraZoom;
        }
        else if (posY + viewHeight/1.99f/cameraZoom > entityList.getBoundsBottom())
        {
            posY = (float) entityList.getBoundsBottom()-viewHeight/1.99f/cameraZoom;
        }

        /*
        float _camPosLerp = (cameraPosLerp * topSpeed / 8) + cameraPosLerp;
        if (Math.abs(cameraPosX - posX) < _camPosLerp / 10) cameraPosX = posX;
        else cameraPosX += (posX - cameraPosX) * _camPosLerp;
        if (updateVert)
        {
            if (Math.abs(cameraPosY - posY) < _camPosLerp / 5) cameraPosY = posY;
            else cameraPosY += (posY - cameraPosY) * _camPosLerp * 2;
        }
        */

        float cameraSpeed = Math.min(5f*deltaSec, 1f);
        cameraPosX = cameraPosX*(1f-cameraSpeed) + posX*cameraSpeed;
        if (updateVert)
        {
            cameraPosY = cameraPosY*(1f-cameraSpeed) + posY*cameraSpeed;
        }


        cameraOffsetX = viewWidth / 2F / cameraZoom;
        cameraOffsetY = viewHeight / 2F / cameraZoom;
    }





    public static void main(String[] args)
    {
        Main.debugEnum = DebugEnum.GAMEPLAY;
        Main.main(args);
    }
}



