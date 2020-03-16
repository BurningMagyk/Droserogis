package Menus;

import Gameplay.Entities.Actor;
import javafx.application.Platform;
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad
{
    private final float DEADZONE = 0.2F; //Remote Xbox controller reading axis as high as of 0.15 when untouched
    private final int gamepadIdx;

    private GLFWGamepadState gamepadState;
    private boolean connected = false;
    private byte lastButtonA = GLFW_RELEASE;
    private byte lastButtonB = GLFW_RELEASE;
    private byte lastButtonX = GLFW_RELEASE;
    private byte lastButtonY = GLFW_RELEASE;
    private byte lastButtonLeftThumb = GLFW_RELEASE;
    private byte lastButtonRightbumper = GLFW_RELEASE;
    private boolean lastUp    = false;
    private boolean lastDown  = false;
    private boolean lastLeft  = false;
    private boolean lastRight = false;


    public Gamepad(int i)
    {
        gamepadIdx = i;
        gamepadState = GLFWGamepadState.create();
        glfwGetGamepadState(gamepadIdx, gamepadState);
    }

    public boolean isConnected() {return connected;}

    /*
    public void checkConnection()
    {
        if (connected)
        {
            if (!glfwJoystickPresent(gamepadIdx) || !glfwJoystickIsGamepad(gamepadIdx))
            {
                connected = false;
                Print.yellow("Gamepad " + gamepadIdx + " disconnected");
            }
        }
        else
        {
            if (glfwJoystickPresent(gamepadIdx) && glfwJoystickIsGamepad(gamepadIdx))
            {
                connected = true;
                Print.blue("Gamepad " + gamepadIdx + " connected");
            }
        }
    }
    */

    public void query(Actor player)
    {
        connected = glfwGetGamepadState(gamepadIdx, gamepadState);
        if (!connected) return;

        //if (code == KeyCode.ESCAPE)
        if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_START) == 1)
        {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
            Platform.exit();
            System.exit(0);
            return;
        }
        //if (code == KeyCode.ENTER && pressed)
        if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK) == 1)
        {
            player.debug();
        }

        float axisX = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X);
        float axisY = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y);
        //Print.blue("("+ axisX + " ," + axisY +")");

        boolean pressUp = false;
        boolean pressDown = false;
        boolean pressLeft = false;
        boolean pressRight = false;

        if ((Math.abs(axisX) > DEADZONE) && (Math.abs(axisX) >= Math.abs(axisY)))
        {
            if (axisX > 0) pressRight = true; else pressLeft = true;
            if ((Math.abs(axisY) > DEADZONE) && (Math.abs(axisY)*2 > Math.abs(axisX)))
            {
                if (axisY > 0) pressDown = true; else pressUp = true;
            }
        }
        else if (Math.abs(axisY) > DEADZONE)
        {
            if (axisY > 0) pressDown = true; else pressUp = true;
            if ((Math.abs(axisX) > DEADZONE) && (Math.abs(axisX)*2 > Math.abs(axisY)))
            {
                if (axisX > 0) pressRight = true; else pressLeft = true;
            }
        }
        if (lastDown != pressDown)
        {
            //Print.blue("pressDown( " + pressDown + "):     " + axisX + ", " + axisY);
            player.pressDown(pressDown);
            lastDown = pressDown;
        }
        if (lastUp != pressUp)
        {
            //Print.blue("pressUp( " + pressUp + "):     " + axisX + ", " + axisY);
            player.pressUp(pressUp);
            lastUp = pressUp;
        }
        if (lastLeft != pressLeft)
        {
            //Print.green("pressLeft( " + pressLeft + "):     " + axisX + ", " + axisY);
            player.pressLeft(pressLeft);
            lastLeft = pressLeft;
        }
        if (lastRight != pressRight)
        {
            //Print.green("pressRight( " + pressRight + "):     " + axisX + ", " + axisY);
            player.pressRight(pressRight);
            lastRight = pressRight;
        }


        //It seems this only gives two states: GLFW_PRES, when pressed and GLFW_RELEASE when not pressed. T
        //     here is no actual release event.
        //This causes a problem when the joystick is connected, but the user is trying to use the keyboard.
        //     To correct, we need to detect the actual release event.

        //if (code == KeyCode.J)
        byte buttonA_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A);
        if (lastButtonA != buttonA_state)
        {
            player.pressJump(buttonA_state == GLFW_PRESS);
            lastButtonA = buttonA_state;
        }

        //if (code == KeyCode.SHIFT)
        byte buttonLeftThumb_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
        if ( lastButtonLeftThumb != buttonLeftThumb_state)
        {
            player.pressShift(buttonLeftThumb_state == GLFW_PRESS);
            lastButtonLeftThumb = buttonLeftThumb_state;
        }

        byte buttonX_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X);
        if (lastButtonX != buttonX_state)
        {
            player.pressAttack(buttonX_state == GLFW_PRESS, Actor.ATTACK_KEY_1);
            lastButtonX = buttonX_state;
        }


        //if (code == KeyCode.L)
        byte buttonY_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y);
        if (lastButtonY != buttonY_state)
        {
            player.pressAttack(buttonY_state == GLFW_PRESS, Actor.ATTACK_KEY_2);
            lastButtonY = buttonY_state;
        }


        //if (code == KeyCode.SEMICOLON)
        byte buttonB_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B);
        if (lastButtonB != buttonB_state)
        {
            player.pressAttack(buttonB_state == GLFW_PRESS, Actor.ATTACK_KEY_3);
            lastButtonB = buttonB_state;
        }


        //if (code == KeyCode.U)
        byte buttonRightbumper_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
        if (lastButtonRightbumper != buttonRightbumper_state)
        {
            player.pressAttackMod(buttonRightbumper_state == GLFW_PRESS);
            lastButtonRightbumper = buttonRightbumper_state;
        }
    }
}