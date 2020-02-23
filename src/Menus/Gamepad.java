package Menus;

import Gameplay.Actor;
import Util.Print;
import javafx.application.Platform;
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad
{
    private final float DEADZONE = 0.2F; //Remote Xbox controller reading axis as high as of 0.15 when untouched
    private final int gamepadIdx;

    private GLFWGamepadState gamepadState;
    private boolean connected = false;
    private byte lastButtonA_state = GLFW_RELEASE;
    private byte lastButtonB_state = GLFW_RELEASE;
    private byte lastButtonX_state = GLFW_RELEASE;
    private byte lastButtonY_state = GLFW_RELEASE;
    private boolean lastUp_state    = false;
    private boolean lastDown_state  = false;
    private boolean lastLeft_state  = false;
    private boolean lastRight_state = false;


    private byte lastButtonRbumper_state = GLFW_RELEASE;


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
        Print.blue("("+ axisX + " ," + axisY +")");

        boolean pressUp = false;
        boolean pressDown = false;
        boolean pressLeft = false;
        boolean pressRight = false;

        if ((Math.abs(axisX) > DEADZONE) && (Math.abs(axisX) >= Math.abs(axisY)))
        {
            if (axisX > 0) pressRight = true; else pressLeft = true;
            if ((Math.abs(axisY) > DEADZONE) && (Math.abs(axisY) > 2 * Math.abs(axisX)))
            {
                if (axisY > 0) pressDown = true; else pressUp = true;
            }
        }
        else if (Math.abs(axisY) > DEADZONE)
        {
            if (axisY > 0) pressDown = true; else pressUp = true;
            if ((Math.abs(axisX) > DEADZONE) && (Math.abs(axisX) > 2 * Math.abs(axisY)))
            {
                if (axisX > 0) pressRight = true; else pressLeft = true;
            }
        }
        if (lastDown_state != pressDown)
        {
            player.pressDown(pressDown);
            lastDown_state = pressDown;
        }
        if (lastUp_state != pressUp)
        {
            player.pressUp(pressUp);
            lastUp_state = pressUp;
        }
        if (lastLeft_state != pressLeft)
        {
            player.pressLeft(pressLeft);
            lastLeft_state = pressLeft;
        }
        if (lastRight_state != pressRight)
        {
            player.pressRight(pressRight);
            lastRight_state = pressRight;
        }



        //It seems this only gives two states: GLFW_PRES, when pressed and GLFW_RELEASE when not pressed. T
        //     here is no actual release event.
        //This causes a problem when the joystick is connected, but the user is trying to use the keyboard.
        //     To correct, we need to detect the actual release event.

        //if (code == KeyCode.J)
        byte buttonA_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A);
        if (buttonA_state == GLFW_PRESS)
        {
            player.pressJump(true);
            lastButtonA_state = buttonA_state;
        }
        else if (lastButtonA_state == GLFW_PRESS)
        {
            player.pressJump(false);
            lastButtonA_state =GLFW_RELEASE;
        }


        //if (code == KeyCode.SHIFT)
        //byte buttonA_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A);
        player.pressShift(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == GLFW_PRESS);

        //if (code == KeyCode.K)
        byte buttonX_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X);
        if (buttonX_state == GLFW_PRESS)
        {
            player.pressAttack(true, Actor.ATTACK_KEY_1);
            lastButtonX_state = buttonX_state;
        }
        else if (lastButtonX_state == GLFW_PRESS)
        {
            player.pressAttack(false, Actor.ATTACK_KEY_1);
            lastButtonX_state = GLFW_RELEASE;
        }

        //if (code == KeyCode.L)
        byte buttonY_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y);
        if (buttonY_state == GLFW_PRESS)
        {
            player.pressAttack(true, Actor.ATTACK_KEY_2);
            lastButtonY_state = buttonX_state;
        }
        else if (lastButtonY_state == GLFW_PRESS)
        {
            player.pressAttack(false, Actor.ATTACK_KEY_2);
            lastButtonY_state = GLFW_RELEASE;
        }


        //if (code == KeyCode.SEMICOLON)
        byte buttonB_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B);
        if (buttonB_state == GLFW_PRESS)
        {
            player.pressAttack(true, Actor.ATTACK_KEY_3);
            lastButtonB_state = buttonX_state;
        }
        else if (lastButtonB_state == GLFW_PRESS)
        {
            player.pressAttack(false, Actor.ATTACK_KEY_3);
            lastButtonB_state = GLFW_RELEASE;
        }


        //if (code == KeyCode.U)
        byte buttonRbumper_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B);
        if (buttonRbumper_state == GLFW_PRESS)
        {
            player.pressAttackMod(true);
            lastButtonRbumper_state = buttonX_state;
        }
        else if (lastButtonRbumper_state == GLFW_PRESS)
        {
            player.pressAttackMod(false);
            lastButtonRbumper_state = GLFW_RELEASE;
        }

    }
}