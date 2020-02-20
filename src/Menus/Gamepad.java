package Menus;

import Gameplay.Actor;
import Util.Print;
import javafx.application.Platform;
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad
{
    private final float DEADZONE = 0.05F;
    private final int gamepadIdx;

    private GLFWGamepadState gamepadState;
    private boolean connected = false;
    private byte lastButtonA_state = GLFW_RELEASE;
    private byte lastButtonB_state = GLFW_RELEASE;
    private byte lastButtonX_state = GLFW_RELEASE;
    private byte lastButtonY_state = GLFW_RELEASE;
    private byte lastButtonRbumper_state = GLFW_RELEASE;

    Gamepad(int i)
    {
        gamepadIdx = i;
        gamepadState = GLFWGamepadState.create();
        glfwGetGamepadState(gamepadIdx, gamepadState);
    }

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

    public void query(Actor player)
    {
        if (!glfwGetGamepadState(gamepadIdx, gamepadState)) return;

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
        //if (code == KeyCode.A)
        player.pressLeft(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) <= DEADZONE - 1);
        //if (code == KeyCode.D)
        player.pressRight(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) >= 1 - DEADZONE);
        //if (code == KeyCode.W)
        player.pressUp(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) <= -DEADZONE);
        //if (code == KeyCode.S)
        player.pressDown(gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) >= DEADZONE);

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

        //player.pressJump(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) == GLFW_PRESS);
        //if (code == KeyCode.SHIFT)

        //byte buttonA_state = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A);
        player.pressShift(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == GLFW_PRESS);
        //if (code == KeyCode.K)
        player.pressAttack(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS, Actor.ATTACK_KEY_1);
        //if (code == KeyCode.L)
        player.pressAttack(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y) == GLFW_PRESS, Actor.ATTACK_KEY_2);
        //if (code == KeyCode.SEMICOLON)
        player.pressAttack(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B) == GLFW_PRESS, Actor.ATTACK_KEY_3);
        //if (code == KeyCode.U)
        player.pressAttackMod(gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS);

    }
}